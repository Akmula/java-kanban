package ru.yandex.javacource.isaev.schedule.manager;

import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.task.Status;
import ru.yandex.javacource.isaev.schedule.task.Epic;
import ru.yandex.javacource.isaev.schedule.task.SubTask;
import ru.yandex.javacource.isaev.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int generatorId = 0; // идентификатор
    private final Map<Integer, Task> tasks = new HashMap<>(); // карта с задачами
    private final Map<Integer, Epic> epics = new HashMap<>(); // карта с эпиками
    private final Map<Integer, SubTask> subTasks = new HashMap<>(); // карта с подзадачами
    private final HistoryManager historyManager = Managers.getDefaultHistory(); // менеджер истории просмотра задач

    // методы для задач

    @Override
    public List<Task> getTaskList() { // получение списка всех задач.
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task addTask(Task task) { //добавление задачи
        int id = generatorId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public void updateTask(Task task) { // обновление задачи
        int id = task.getId();
        Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    @Override
    public void deleteTask(int id) { //
        tasks.remove(id);
    }

    @Override
    public void deleteAllTask() { // удаление всех задач
        tasks.clear();
    }

    @Override
    public Task getTask(int id) { // получение задачи по идентификатору
        final Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    // методы для эпиков

    @Override
    public List<Epic> getEpicList() { // получение списка эпиков
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic addEpic(Epic epic) { //добавление эпика
        int id = generatorId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) { // обновление эпика
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        epic.setSubTaskId(savedEpic.getSubTaskId());
        epic.setStatus(savedEpic.getStatus());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteEpic(int id) { // удаление эпика
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subTaskId : epic.getSubTaskId()) {
            subTasks.remove(subTaskId);
        }
    }

    @Override
    public void deleteAllEpic() { // удаление всех эпиков.
        epics.clear();
        subTasks.clear(); //так как подзадачи не могут существовать без эпиков, их тоже удаляем
    }

    @Override
    public Epic getEpic(int id) { // получение эпика по идентификатору.
        final Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    // методы для подзадач

    @Override
    public List<SubTask> getSubTaskList() { // получение списка подзадач
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void addSubTask(SubTask subTask) { //добавление подзадачи
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        int id = generatorId();
        subTask.setId(id);
        subTasks.put(id, subTask);
        epic.addSubTaskId(subTask.getId());
        updateEpicStatus(epicId);
    }

    @Override
    public void updateSubTask(SubTask subTask) { // обновление подзадачи
        int id = subTask.getId();
        int epicId = subTask.getEpicId();
        SubTask savedSubTask = subTasks.get(id);
        if (savedSubTask == null) {
            return;
        }
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subTasks.put(id, subTask);
        updateEpicStatus(epicId);
    }

    @Override
    public void deleteSubTask(int id) { // удаление подзадачи
        SubTask subTask = subTasks.remove(id);
        if (subTask == null) {
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void deleteSubtasks() { // удаление всех подзадач
        for (Epic epic : epics.values()) {
            epic.cleanSubTaskIds();
            updateEpicStatus(epic.getId());
        }
        subTasks.clear();
    }

    @Override
    public SubTask getSubTask(int id) { //получение подзадачи по id
        final SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private Integer generatorId() { // генератор идентификтора
        generatorId++;
        return generatorId;
    }

    private void updateEpicStatus(int epicId) {  // проверка статуса эпика
        Epic updateEpic = epics.get(epicId);
        ArrayList<Status> status = new ArrayList<>();
        for (int subTaskId : updateEpic.getSubTaskId()) {
            status.add(subTasks.get(subTaskId).getStatus());
        }
        if (status.contains(Status.NEW) && status.contains(Status.DONE) || status.contains(Status.IN_PROGRESS)) {
            updateEpic.setStatus(Status.IN_PROGRESS);
        } else if (status.contains(Status.NEW)) {
            updateEpic.setStatus(Status.NEW);
        } else {
            updateEpic.setStatus(Status.DONE);
        }
    }
}
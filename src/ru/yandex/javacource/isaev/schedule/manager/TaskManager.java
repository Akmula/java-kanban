package ru.yandex.javacource.isaev.schedule.manager;

import ru.yandex.javacource.isaev.schedule.task.Status;
import ru.yandex.javacource.isaev.schedule.task.Epic;
import ru.yandex.javacource.isaev.schedule.task.SubTask;
import ru.yandex.javacource.isaev.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private int generatorId = 0; // идентификатор
    private final Map<Integer, Task> tasks = new HashMap<>(); // карта с задачами
    private final Map<Integer, Epic> epics = new HashMap<>(); // карта с эпиками
    private final Map<Integer, SubTask> subTasks = new HashMap<>(); // карта с подзадачами

    // методы для задач

    public List<Task> getTaskList() { // получение списка всех задач.
        return new ArrayList<>(tasks.values());
    }

    public Task addTask(Task task) { //добавление задачи
        int id = generatorId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public void updateTask(Task task) { // обновление задачи
        int id = task.getId();
        Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    public void deleteTask(int id) { //
        tasks.remove(id);
    }

    public void deleteAllTask() { // удаление всех задач
        tasks.clear();
    }

    public Task getTask(int id) { // получение задачи по идентификатору
        return tasks.get(id);
    }

    // методы для эпиков

    public List<Epic> getEpicList() { // получение списка эпиков
        return new ArrayList<>(epics.values());
    }

    public Epic addEpic(Epic epic) { //добавление эпика
        int id = generatorId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) { // обновление эпика
        int id = epic.getId();
        Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            return null;
        }
        savedEpic.setTitle(epic.getTitle());
        savedEpic.setDescription(epic.getDescription());
        updateEpicStatus(id);
        return savedEpic;
    }

    public void deleteEpic(int id) { // удаление эпика
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subTaskId : epic.getSubTaskId()) {
            subTasks.remove(subTaskId);
        }
    }

    public void deleteAllEpic() { // удаление всех эпиков.
        epics.clear();
        subTasks.clear(); //так как подзадачи не могут существовать без эпиков, их тоже удаляем
    }

    public Epic getEpic(int id) { // получение эпика по идентификатору.
        return epics.get(id);
    }

    // методы для подзадач

    public List<SubTask> getSubTaskList() { // получение списка подзадач
        return new ArrayList<>(subTasks.values());
    }

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

    public void deleteSubTask(int id) { // удаление подзадачи
        SubTask subTask = subTasks.remove(id);
        if (subTask == null) {
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    public void deleteSubtasks() { // удаление всех подзадач
        for (Epic epic : epics.values()) {
            epic.cleanSubTaskIds();
            updateEpicStatus(epic.getId());
        }
        subTasks.clear();
    }

    public SubTask getSubTask(int id) { //получение подзадачи по id
        return subTasks.get(id);
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
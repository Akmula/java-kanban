package ru.yandex.javacource.isaev.schedule.managers;

import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.enums.TaskType;
import ru.yandex.javacource.isaev.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Integer generatorId = 0; // идентификатор
    protected final HistoryManager historyManager = Managers.getDefaultHistory(); // менеджер истории просмотра задач
    protected final Map<Integer, Task> tasks = new HashMap<>(); // карта с задачами
    protected final Map<Integer, Epic> epics = new HashMap<>(); // карта с эпиками
    protected final Map<Integer, SubTask> subTasks = new HashMap<>(); // карта с подзадачами
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    protected void setGeneratorId(int id) {
        this.generatorId = id;
    }

    // методы для задач

    @Override
    public List<Task> getTaskList() { // получение списка всех задач.
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task addTask(Task task) { //добавление задачи
        if (isTimeIntersection(task)) {
            int id = generatorId();
            task.setId(id);
            task.setTaskType(TaskType.TASK);
            tasks.put(id, task);
            addPrioritize(task);
        } else {
            throw new TaskValidationException("Задача не добавлена, на это время назначена задача!");
        }
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
        addPrioritize(task);
    }

    @Override
    public void deleteTask(int id) { // удаление задачи
        Task task = tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(task);
    }

    @Override
    public void deleteAllTask() { // удаление всех задач
        tasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        });
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
        epic.setTaskType(TaskType.EPIC);
        if (epic.getStartTime() != null) {
            epic.setEndTime(epic.getStartTime().plusMinutes(epic.getDuration().toMinutes()));
        }
        epics.put(id, epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) { // обновление эпика
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        updateEpic(epic.getId());
        epic.setSubTaskId(savedEpic.getSubTaskIdList());
        epic.setStatus(savedEpic.getStatus());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteEpic(int id) { // удаление эпика
        historyManager.remove(id);
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        if (epic.getSubTaskIdList() == null) {
            return;
        }
        epic.getSubTaskIdList().forEach(subTaskId -> {
            historyManager.remove(subTaskId);
            prioritizedTasks.remove(subTasks.get(subTaskId));
            subTasks.remove(subTaskId);
        });
    }

    @Override
    public void deleteAllEpic() { // удаление всех эпиков.
        epics.keySet().forEach(historyManager::remove);
        subTasks.keySet().forEach(historyManager::remove);
        epics.clear();
        subTasks.clear(); //так как подзадачи не могут существовать без эпиков, их тоже удаляем
        prioritizedTasks.removeIf(task -> task.getTaskType().equals(TaskType.SUBTASK));
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
    public SubTask addSubTask(SubTask subTask) { //добавление подзадачи
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return subTask;
        }
        if (isTimeIntersection(subTask)) {
            int id = generatorId();
            subTask.setId(id);
            subTask.setTaskType(TaskType.SUBTASK);
            subTasks.put(id, subTask);
            epic.addSubTaskId(subTask.getId());
            updateEpic(epicId);
            addPrioritize(subTask);
        } else {
            throw new TaskValidationException("Задача пересекается с id=" + subTask.getId() + " c " + subTask.getStartTime() + " по " + subTask.getEndTime());
        }
        return subTask;
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
        updateEpic(epicId);
        addPrioritize(subTask);
    }

    @Override
    public void deleteSubTask(int id) { // удаление подзадачи
        historyManager.remove(id);
        SubTask subTask = subTasks.remove(id);
        if (subTask == null) {
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        epic.removeSubtask(id);
        updateEpic(epic.getId());
        prioritizedTasks.remove(subTask);
    }

    @Override
    public void deleteAllSubtask() { // удаление всех подзадач
        epics.values().forEach(epic -> {
            epic.cleanSubTaskIds();
            updateEpic(epic.getId());
        });
        subTasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(subTasks.get(id));
        });
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void addPrioritize(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        if (prioritizedTasks.isEmpty()) {
            prioritizedTasks.add(task);

        } else {
            prioritizedTasks.removeIf(deletedTask -> deletedTask.getId() == task.getId());
            prioritizedTasks.add(task);
        }
    }

    @Override
    public boolean isTimeIntersection(Task checkedTask) { // проверка на пересечение задач и подзадач
        if (checkedTask.getDuration() == null) {
            return true;
        }

        LocalDateTime taskStartTime = checkedTask.getStartTime(); // время начала задачи
        LocalDateTime taskEndTime = checkedTask.getEndTime(); // время окончания задачи

        return prioritizedTasks.stream().noneMatch(task -> taskStartTime.isAfter(task.getStartTime()) &&
                taskStartTime.isBefore(task.getEndTime()) || taskEndTime.isAfter(task.getStartTime()) &&
                taskEndTime.isBefore(task.getEndTime()) || taskStartTime.equals(task.getStartTime()));
    }

    private Integer generatorId() { // генератор идентификтора
        generatorId++;
        return generatorId;
    }

    private void updateEpicStatus(Epic updateEpic) {  // проверка статуса эпика
        if (subTasks.isEmpty()) {
            return;
        }
        if (updateEpic.getSubTaskIdList().isEmpty()) {
            return;
        }

        List<Status> status = updateEpic.getSubTaskIdList().stream().map(id -> subTasks.get(id).getStatus()).toList();

        if (status.contains(Status.NEW) && status.contains(Status.DONE) || status.contains(Status.IN_PROGRESS)) {
            updateEpic.setStatus(Status.IN_PROGRESS);
        } else if (status.contains(Status.NEW)) {
            updateEpic.setStatus(Status.NEW);
        } else {
            updateEpic.setStatus(Status.DONE);
        }
    }

    private void updateEpicTimes(Epic updateEpic) {  // проверка статуса эпика
        if (updateEpic.getSubTaskIdList().isEmpty()) {
            return;
        }

        LocalDateTime startTime = updateEpic.getStartTime();
        LocalDateTime endTime = updateEpic.getEndTime();

        for (Integer id : updateEpic.getSubTaskIdList()) {
            if (subTasks.get(id).getStartTime() == null || startTime == null) {
                return;
            }
            if (subTasks.get(id).getStartTime().isBefore(startTime)) {
                startTime = subTasks.get(id).getStartTime();
            }
            if (subTasks.get(id).getEndTime().isAfter(endTime)) {
                endTime = subTasks.get(id).getEndTime();
            }
        }
        Duration duration = Duration.ofMinutes(ChronoUnit.MINUTES.between(startTime, endTime));
        updateEpic.setStartTime(startTime);
        updateEpic.setDuration(duration);
        updateEpic.setEndTime(startTime.plusMinutes(duration.toMinutes()));
    }

    protected void updateEpic(int epicId) {
        Epic epic = epics.get(epicId);
        updateEpicStatus(epic);
        updateEpicTimes(epic);
    }
}
package ru.yandex.javacource.isaev.schedule.interfaces;

import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTaskList();

    Task addTask(Task task);

    void updateTask(Task task);

    void deleteTask(int id);

    void deleteAllTask();

    Task getTask(int id);

    List<Epic> getEpicList();

    Epic addEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpic(int id);

    void deleteAllEpic();

    Epic getEpic(int id);

    List<SubTask> getSubTaskList();

    SubTask addSubTask(SubTask subTask);

    void updateSubTask(SubTask subTask);

    void deleteSubTask(int id);

    void deleteAllSubtask();

    SubTask getSubTask(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    void addPrioritize(Task task);

    boolean isTimeIntersection(Task task);
}
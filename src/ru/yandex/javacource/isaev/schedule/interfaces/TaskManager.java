package ru.yandex.javacource.isaev.schedule.interfaces;

import ru.yandex.javacource.isaev.schedule.task.Epic;
import ru.yandex.javacource.isaev.schedule.task.SubTask;
import ru.yandex.javacource.isaev.schedule.task.Task;

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

    Epic updateEpic(Epic epic);

    void deleteEpic(int id);

    void deleteAllEpic();

    Epic getEpic(int id);

    List<SubTask> getSubTaskList();

    void addSubTask(SubTask subTask);

    void updateSubTask(SubTask subTask);

    void deleteSubTask(int id);

    void deleteSubtasks();

    SubTask getSubTask(int id);

    List<Task> getHistory();
}

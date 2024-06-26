package ru.yandex.javacource.isaev.schedule.interfaces;

import ru.yandex.javacource.isaev.schedule.task.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();
}

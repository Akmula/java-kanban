package ru.yandex.javacource.isaev.schedule.interfaces;

import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}

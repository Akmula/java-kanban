package ru.yandex.javacource.isaev.schedule.manager;

import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyTasks = new ArrayList<>(); // список истории
    public static final int MAX_SIZE = 10; // размер списка истории

    @Override
    public void add(Task task) {
        if(task == null) {
            return;
        }
        if (historyTasks.size() >= MAX_SIZE) {
            historyTasks.remove(0);
        }
        historyTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyTasks;
    }
}

package ru.yandex.javacource.isaev.schedule.managers;

import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

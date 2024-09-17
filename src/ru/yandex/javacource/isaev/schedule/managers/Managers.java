package ru.yandex.javacource.isaev.schedule.managers;

import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;

import java.io.File;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        final String PATH_TO_FILE = "src/ru/yandex/javacource/isaev/schedule/resources/data.csv";
        return FileBackedTaskManager.loadFromFile(new File(PATH_TO_FILE));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

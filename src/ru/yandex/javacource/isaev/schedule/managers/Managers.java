package ru.yandex.javacource.isaev.schedule.managers;

import ru.yandex.javacource.isaev.schedule.exceptions.ManagerLoadException;
import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;

import java.io.File;
import java.io.IOException;

public class Managers {

    private static final String PATH_TO_FILE = "src/ru/yandex/javacource/isaev/schedule/resources/data.csv";
    static File file = new File(PATH_TO_FILE);

    private Managers() {
    }

    public static TaskManager getDefault() {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка чтения файла!", e);
        }
        return FileBackedTaskManager.loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

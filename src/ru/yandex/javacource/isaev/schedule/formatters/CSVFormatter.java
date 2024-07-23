package ru.yandex.javacource.isaev.schedule.formatters;

import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

public class CSVFormatter {

    private CSVFormatter() {
    }

    public static String getHeader() {
        return "id;type;title;description;status;epicId";
    }

    public static String toString(Task task) {
        return task.getId() + "," +
                task.getClass().getSimpleName() + "," +
                task.getTitle() + "," +
                task.getDescription() + "," +
                task.getStatus() + "," +
                task.getEpicId();
    }

    public static Task fromString(String string) {
        Task task;
        String[] split = string.split(",");
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String title = split[2];
        String description = split[3];
        Status status = Status.valueOf(split[4]);
        int epicId = Integer.parseInt(split[5]);

        if (type.equals("Epic")) {
            task = new Epic(id, title, description, status);
        } else if (type.equals("SubTask")) {
            task = new SubTask(id, title, description, status, epicId);
        } else {
            task = new Task(id, title, description, status);
        }
        return task;
    }
}
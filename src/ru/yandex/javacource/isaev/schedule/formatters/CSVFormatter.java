package ru.yandex.javacource.isaev.schedule.formatters;

import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.enums.TaskType;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import static ru.yandex.javacource.isaev.schedule.enums.TaskType.*;

public class CSVFormatter {

    private CSVFormatter() {
    }

    public static String getHeader() {
        return "id;type;title;description;status;epicId";
    }

    public static String toString(Task task) {
        int epicId = 0;
        if (task.getTaskType().equals(SUBTASK)) {
            SubTask subTask = (SubTask) task;
            epicId = subTask.getEpicId();
        }
        return task.getId() + "," +
                task.getTaskType() + "," +
                task.getTitle() + "," +
                task.getDescription() + "," +
                task.getStatus() + "," +
                epicId;
    }

    public static Task fromString(String string) {
        Task task = null;
        String[] split = string.split(",");
        int id = Integer.parseInt(split[0]);
        TaskType taskType = valueOf(split[1]);
        String title = split[2];
        String description = split[3];
        Status status = Status.valueOf(split[4]);
        int epicId = Integer.parseInt(split[5]);

        if (taskType.equals(EPIC)) {
            task = new Epic(id, taskType, title, description, status);
        } else if (taskType.equals(SUBTASK)) {
            task = new SubTask(id, taskType, title, description, status, epicId);
        } else {
            task = new Task(id, taskType, title, description, status);
        }
        return task;
    }
}
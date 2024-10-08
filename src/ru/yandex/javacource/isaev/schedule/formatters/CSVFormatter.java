package ru.yandex.javacource.isaev.schedule.formatters;

import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.enums.TaskType;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static ru.yandex.javacource.isaev.schedule.enums.TaskType.*;

public class CSVFormatter {

    private CSVFormatter() {
    }

    public static String getHeader() {
        return "id,type,title,description,status,duration,startTime,epicId";
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
                task.getDuration() + "," +
                task.getStartTime() + "," +
                epicId;
    }

    public static Task fromString(String string) {
        Task task;
        String[] split = string.split(",");
        if (split.length == 0) {
            return null;
        }
        int id = Integer.parseInt(split[0]);
        TaskType taskType = valueOf(split[1]);
        String title = split[2];
        String description = split[3];
        Status status = Status.valueOf(split[4]);
        int epicId = Integer.parseInt(split[7]);

        if (split[5].equals("null") || split[6].equals("null")) {
            if (taskType.equals(EPIC)) {
                task = new Epic(id, taskType, title, description, status);
            } else if (taskType.equals(SUBTASK)) {
                task = new SubTask(id, taskType, title, description, status, epicId);
            } else {
                task = new Task(id, taskType, title, description, status);
            }
        } else {
            Duration duration = Duration.parse(Objects.requireNonNull(split[5]));
            LocalDateTime startTime = LocalDateTime.parse(split[6]);
            if (taskType.equals(EPIC)) {
                task = new Epic(id, taskType, title, description, status, duration, startTime);
            } else if (taskType.equals(SUBTASK)) {
                task = new SubTask(id, taskType, title, description, status, duration, startTime, epicId);
            } else {
                task = new Task(id, taskType, title, description, status, duration, startTime);
            }
        }
        return task;
    }
}
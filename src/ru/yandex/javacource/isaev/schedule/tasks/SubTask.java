package ru.yandex.javacource.isaev.schedule.tasks;

import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.enums.TaskType;

import java.util.Objects;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(int id, TaskType taskType, String title, String description, Status status, int epicId) {
        super(id, taskType, title, description, status);
        this.epicId = epicId;
    }

    public SubTask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return super.toString()
                + ", epicId=" + epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
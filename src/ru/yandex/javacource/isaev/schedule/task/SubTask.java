package ru.yandex.javacource.isaev.schedule.task;

import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    public SubTask(int id, int epicId, String title, String description, Status status) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public SubTask(int epicId, String title, String description, Status status) {
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
        return "SubTask{" +
                "epicId=" + epicId +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
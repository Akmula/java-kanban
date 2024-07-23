package ru.yandex.javacource.isaev.schedule.tasks;

import ru.yandex.javacource.isaev.schedule.enums.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;
    private int epicId;
    private int subTaskId;
    List<Integer> subTaskIds = new ArrayList<>();

    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getEpicId() {
        return epicId;
    }

    public int getSubTaskId() {
        return subTaskId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String setDescription(String description) {
        this.description = description;
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task - id=" + id
                + ", title=" + title
                + ", description=" + description
                + ", status=" + status
                + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }
}
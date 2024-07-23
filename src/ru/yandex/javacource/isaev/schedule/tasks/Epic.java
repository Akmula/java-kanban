package ru.yandex.javacource.isaev.schedule.tasks;

import ru.yandex.javacource.isaev.schedule.enums.Status;

import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public Epic(int id, String title, String description, Status status, List<Integer> subTaskIds) {
        super(id, title, description, status);
        this.subTaskIds = subTaskIds;
    }

    public void setSubTaskId(List<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public void addSubTaskId(int id) {
        subTaskIds.add(id);
    }

    public void removeSubtask(int id) {
        if (subTaskIds == null) {
            return;
        }
        for (int i = 0; i < subTaskIds.size(); i++) {
            if (subTaskIds.get(i) == id) {
                subTaskIds.remove(i);
            }
        }
    }

    public void cleanSubTaskIds() {
        this.subTaskIds.clear();
    }

    @Override
    public String toString() {
        return "Epic - subTaskIds="
                + subTaskIds + "," + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskIds, epic.subTaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIds);
    }
}
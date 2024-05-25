package ru.yandex.javacource.isaev.schedule.task;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds;

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public Epic(int id, ArrayList<Integer> subTaskIds, String title, String description, Status status) {
        super(id, title, description, status);
        this.subTaskIds = subTaskIds;
    }

    public ArrayList<Integer> getSubTaskId() {
        return subTaskIds;
    }

    public void setSubTaskId(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public void addSubTaskId(int id) {
        subTaskIds = new ArrayList<>();
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
        return "Epic{" +
                "subTaskIds=" + subTaskIds +
                "} " + super.toString();
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
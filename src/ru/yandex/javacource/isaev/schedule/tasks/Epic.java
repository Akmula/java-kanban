package ru.yandex.javacource.isaev.schedule.tasks;

import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.enums.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subTaskIdList = new ArrayList<>();

    public Epic(int id, TaskType taskType, String title, String description, Status status) {
        super(id, taskType, title, description, status);
    }

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public Epic(int id, String title, String description, Status status, List<Integer> subTaskIds) {
        super(id, title, description, status);
        this.subTaskIdList = subTaskIds;
    }

    public List<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void setSubTaskId(List<Integer> subTaskIds) {
        this.subTaskIdList = subTaskIds;
    }

    public void addSubTaskId(int id) {
        subTaskIdList.add(id);
    }

    public void removeSubtask(int id) {
        if (subTaskIdList == null) {
            return;
        }
        for (int i = 0; i < subTaskIdList.size(); i++) {
            if (subTaskIdList.get(i) == id) {
                subTaskIdList.remove(i);
            }
        }
    }

    public void cleanSubTaskIds() {
        this.subTaskIdList.clear();
    }

    @Override
    public String toString() {
        return super.toString()
                + ", subTaskIds=" + subTaskIdList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskIdList, epic.subTaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIdList);
    }
}
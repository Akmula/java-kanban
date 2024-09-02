package ru.yandex.javacource.isaev.schedule.tasks;

import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTaskIdList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(int id, String title, String description, Status status, List<Integer> subTaskIdList) {
        super(id, title, description, status);
        this.subTaskIdList = subTaskIdList;
    }

    public Epic(int id, TaskType taskType, String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, taskType, title, description, status, duration, startTime);
    }

    public Epic(String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(title, description, status, duration, startTime);
    }

    public List<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void setSubTaskId(List<Integer> subTaskIdList) {
        this.subTaskIdList = subTaskIdList;
    }

    public void addSubTaskId(int id) {
        subTaskIdList.add(id);
    }

    public void removeSubtask(int id) {
        if (subTaskIdList == null) {
            return;
        }
        subTaskIdList.removeIf(i -> i == id);
    }

    public void cleanSubTaskIds() {
        this.subTaskIdList.clear();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;

    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIdList=" + subTaskIdList +
                "} " + super.toString();
    }
}
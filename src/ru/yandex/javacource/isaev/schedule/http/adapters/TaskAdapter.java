package ru.yandex.javacource.isaev.schedule.http.adapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.http.HttpTaskServer;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

public class TaskAdapter extends TypeAdapter<Task> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    @Override
    public void write(final JsonWriter jsonWriter, Task task) throws IOException {
        System.out.println("Запись в строку начата");
        jsonWriter.beginObject();

        if (doesObjectContainField(task, "id")) {
            jsonWriter.name("id").value(task.getId());
        } else {
            jsonWriter.name("id").nullValue();
        }

        if (doesObjectContainField(task, "taskType")) {
            if (task.getTaskType() == null) {
                jsonWriter.name("taskType").nullValue();
            } else {
                jsonWriter.name("taskType").value(String.valueOf(task.getTaskType()));
            }
        } else {
            jsonWriter.name("taskType").nullValue();
        }

        jsonWriter.name("title").value(task.getTitle());
        jsonWriter.name("description").value(task.getDescription());
        jsonWriter.name("status").value(task.getStatus().toString());

        if (doesObjectContainField(task, "startTime")) {
            if (task.getStartTime() == null) {
                jsonWriter.name("duration").nullValue();
                jsonWriter.name("startTime").nullValue();
            } else {
                jsonWriter.name("duration").value(task.getDuration().toMinutes());
                jsonWriter.name("startTime").value(task.getStartTime().format(DATE_TIME_FORMATTER));
            }
        } else {
            jsonWriter.name("duration").nullValue();
            jsonWriter.name("startTime").nullValue();
        }


        jsonWriter.endObject();

        System.out.println("Запись в строку закончена");
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {

        System.out.println("Чтение из строки начата");
        JsonElement jsonElement = JsonParser.parseReader(jsonReader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Task task;

        String title = jsonObject.get("title").getAsString();
        String description = jsonObject.get("description").getAsString();
        Status status = Status.valueOf(jsonObject.get("status").getAsString());




        if (jsonObject.has("startTime")) {
            Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsInt());
            LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), DATE_TIME_FORMATTER);
            task = new Task(title, description, status, duration, startTime);
        } else {

            task = new Task(title, description, status);
        }

        if (jsonObject.has("id")) {
            System.out.println("Где айди?");
            task.setId(jsonObject.get("id").getAsInt());
        }
         System.out.println("ТУт задача -> " + task);
        System.out.println("Чтение из строки закончена");
        return task;

    }

    public boolean doesObjectContainField(Object object, String fieldName) {
        return Arrays.stream(object.getClass().getFields())
                .anyMatch(f -> f.getName().equals(fieldName));
    }
}
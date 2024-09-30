package ru.yandex.javacource.isaev.schedule.http.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.enums.TaskType;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class TaskAdapter extends TypeAdapter<Task> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public void write(final JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.beginObject();

        if (doesObjectContainField(task, "id")) {
            jsonWriter.name("id").nullValue();
        } else {
            if (task.toString().contains("'id='null'")) {
                jsonWriter.name("id").nullValue();
            } else {
                jsonWriter.name("id").value(task.getId());
            }
        }

        if (doesObjectContainField(task, "taskType")) {
            if (task.getTaskType() == null) {
                jsonWriter.name("taskType").nullValue();
            }
        } else {
            jsonWriter.name("taskType").value(String.valueOf(task.getTaskType()));
        }

        jsonWriter.name("title").value(task.getTitle());
        jsonWriter.name("description").value(task.getDescription());
        jsonWriter.name("status").value(task.getStatus().toString());

        if (doesObjectContainField(task, "startTime")) {
            jsonWriter.name("duration").nullValue();
            jsonWriter.name("startTime").nullValue();
        } else {
            if (task.getStartTime() == null) {
                jsonWriter.name("duration").nullValue();
                jsonWriter.name("startTime").nullValue();
            } else {
                jsonWriter.name("duration").value(task.getDuration().toMinutes());
                jsonWriter.name("startTime").value(task.getStartTime().format(DATE_TIME_FORMATTER));
            }
        }
        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) {
        JsonElement jsonElement = JsonParser.parseReader(jsonReader);
        JsonObject json = null;

        if (jsonElement.isJsonObject()) {
            json = jsonElement.getAsJsonObject();
        }
        if (jsonElement.isJsonArray()) {
            json = jsonElement.getAsJsonArray().iterator().next().getAsJsonObject();
        }

        int id = 0;

        if (json != null && json.has("id") && !json.get("id").isJsonNull()) {
            id = json.get("id").getAsInt();
        }

        String title = json != null ? json.get("title").getAsString() : null;
        String description = json != null ? json.get("description").getAsString() : null;
        Duration duration = null;
        LocalDateTime startTime = null;

        if (json != null && json.has("duration") && !json.get("duration").isJsonNull()) {
            duration = Duration.ofMinutes(json.get("duration").getAsInt());
        }

        if (json != null && json.has("startTime") && !json.get("startTime").isJsonNull()) {
            startTime = LocalDateTime.parse(json.get("startTime").getAsString(), DATE_TIME_FORMATTER);
        }

        Status status = Status.valueOf(Objects.requireNonNull(json).get("status").getAsString());

        return new Task(id, TaskType.TASK, title, description, status, duration, startTime);
    }

    public boolean doesObjectContainField(Object object, String fieldName) {
        return Arrays.stream(object.getClass().getFields())
                .anyMatch(f -> f.getName().equals(fieldName));
    }
}
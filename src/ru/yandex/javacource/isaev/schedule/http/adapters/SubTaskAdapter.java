package ru.yandex.javacource.isaev.schedule.http.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.enums.TaskType;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class SubTaskAdapter extends TypeAdapter<SubTask> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public void write(final JsonWriter jsonWriter, SubTask subTask) throws IOException {
        jsonWriter.beginObject();

        if (doesObjectContainField(subTask, "id")) {
            jsonWriter.name("id").nullValue();
        } else {
            if (subTask.toString().contains("'id='null'")) {
                jsonWriter.name("id").nullValue();
            } else {
                jsonWriter.name("id").value(subTask.getId());
            }
        }

        if (doesObjectContainField(subTask, "taskType")) {
            if (subTask.getTaskType() == null) {
                jsonWriter.name("taskType").nullValue();
            }
        } else {
            jsonWriter.name("taskType").value(String.valueOf(subTask.getTaskType()));
        }

        jsonWriter.name("title").value(subTask.getTitle());
        jsonWriter.name("description").value(subTask.getDescription());
        jsonWriter.name("status").value(subTask.getStatus().toString());

        if (doesObjectContainField(subTask, "startTime")) {
            jsonWriter.name("duration").nullValue();
            jsonWriter.name("startTime").nullValue();
        } else {
            if (subTask.getStartTime() == null) {
                jsonWriter.name("duration").nullValue();
                jsonWriter.name("startTime").nullValue();
            } else {
                jsonWriter.name("duration").value(subTask.getDuration().toMinutes());
                jsonWriter.name("startTime").value(subTask.getStartTime().format(DATE_TIME_FORMATTER));
            }
        }

        jsonWriter.name("epicId").value(subTask.getEpicId());

        jsonWriter.endObject();
    }

    @Override
    public SubTask read(JsonReader jsonReader) {
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

        int epicId = 0;

        if (json != null && json.has("duration") && !json.get("duration").isJsonNull()) {
            duration = Duration.ofMinutes(json.get("duration").getAsInt());
        }

        if (json != null && json.has("startTime") && !json.get("startTime").isJsonNull()) {
            startTime = LocalDateTime.parse(json.get("startTime").getAsString(), DATE_TIME_FORMATTER);
        }

        if (json != null && json.has("epicId") && !json.get("epicId").isJsonNull()) {
            epicId = json.get("epicId").getAsInt();
        }

        Status status = Status.valueOf(Objects.requireNonNull(json).get("status").getAsString());

        return new SubTask(id, TaskType.SUBTASK, title, description, status, duration, startTime, epicId);
    }

    public boolean doesObjectContainField(Object object, String fieldName) {
        return Arrays.stream(object.getClass().getFields())
                .anyMatch(f -> f.getName().equals(fieldName));
    }
}
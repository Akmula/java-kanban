package ru.yandex.javacource.isaev.schedule.http.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.enums.TaskType;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class EpicAdapter extends TypeAdapter<Epic> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public void write(final JsonWriter jsonWriter, Epic epic) throws IOException {
        jsonWriter.beginObject();

        if (doesObjectContainField(epic, "id")) {
            jsonWriter.name("id").nullValue();
        } else {
            if (epic.toString().contains("'id='null'")) {
                jsonWriter.name("id").nullValue();
            } else {
                jsonWriter.name("id").value(epic.getId());
            }
        }

        if (doesObjectContainField(epic, "taskType")) {
            if (epic.getTaskType() == null) {
                jsonWriter.name("taskType").nullValue();
            }
        } else {
            jsonWriter.name("taskType").value(String.valueOf(epic.getTaskType()));
        }

        jsonWriter.name("title").value(epic.getTitle());
        jsonWriter.name("description").value(epic.getDescription());
        jsonWriter.name("status").value(epic.getStatus().toString());

        if (doesObjectContainField(epic, "startTime")) {
            jsonWriter.name("duration").nullValue();
            jsonWriter.name("startTime").nullValue();
        } else {
            if (epic.getStartTime() == null) {
                jsonWriter.name("duration").nullValue();
                jsonWriter.name("startTime").nullValue();
            } else {
                jsonWriter.name("duration").value(epic.getDuration().toMinutes());
                jsonWriter.name("startTime").value(epic.getStartTime().format(DATE_TIME_FORMATTER));
            }
        }

        if (!doesObjectContainField(epic, "subTaskIdList")) {
            if (epic.getSubTaskIdList() != null || !epic.getSubTaskIdList().isEmpty()) {
                jsonWriter.name("subTaskIdList").value(String.valueOf(epic.getSubTaskIdList()));
            }
        }
        jsonWriter.endObject();
    }

    @Override
    public Epic read(JsonReader jsonReader) {
        JsonElement jsonElement = JsonParser.parseReader(jsonReader);
        JsonObject json = null;

        if (jsonElement.isJsonObject()) {
            json = jsonElement.getAsJsonObject();
        }
        if (jsonElement.isJsonArray()) {
            json = jsonElement.getAsJsonArray().iterator().next().getAsJsonObject();
        }

        int id = 0;
        if (json != null && json.has("id")) {
            if (!json.get("id").isJsonNull()) {
                id = json.get("id").getAsInt();
            }
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

        return new Epic(id, TaskType.EPIC, title, description, status, duration, startTime);
    }

    public boolean doesObjectContainField(Object object, String fieldName) {
        return Arrays.stream(object.getClass().getFields())
                .anyMatch(f -> f.getName().equals(fieldName));
    }
}
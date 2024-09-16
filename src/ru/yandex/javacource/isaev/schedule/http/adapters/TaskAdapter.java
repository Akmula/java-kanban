package ru.yandex.javacource.isaev.schedule.http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.javacource.isaev.schedule.enums.Status;

import java.io.IOException;

public class TaskAdapter extends TypeAdapter<Status> {


    @Override
    public void write(JsonWriter jsonWriter, Status status) throws IOException {
        System.out.println("status");
        jsonWriter.value(status.toString());
    }

    @Override
    public Status read(JsonReader jsonReader) throws IOException {
        System.out.println("jsonReaderstatus");
        return Status.valueOf(jsonReader.toString());
    }
}

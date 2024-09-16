package ru.yandex.javacource.isaev.schedule.http.handlers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.http.HttpTaskServer;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Integer id = getIdFromPath(exchange.getRequestURI().getPath());
        switch (exchange.getRequestMethod()) {
            case "GET":
                String response;
                Task task = null;
                if (id == null) {
                    List<Task> tasks = taskManager.getTaskList();
                    response = gson.toJson(tasks);
                } else {
                    task = taskManager.getTask(id);
                    response = gson.toJson(task);
                }
                if (task == null || response.contains("[]")) {
                    response = "Задачи отсутствуют!";
                    sendNotFound(exchange, response);
                } else {
                    sendText(exchange, response);
                }
                break;
            case "POST":


                InputStream inputStream = exchange.getRequestBody();

                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                String taskSerialized = gson.toJson(body);
                System.out.println("Serialized task: " + taskSerialized);

                Task taskDeserialized = gson.fromJson(taskSerialized, Task.class);
                System.out.println("Deserialized task: " + taskDeserialized);

                break;
            case "DELETE":
                break;
            default:
                throw new RuntimeException("Неверный метод");
        }
    }
}

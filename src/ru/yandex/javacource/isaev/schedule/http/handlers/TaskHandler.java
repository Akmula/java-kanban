package ru.yandex.javacource.isaev.schedule.http.handlers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.http.HttpTaskServer;
import ru.yandex.javacource.isaev.schedule.http.adapters.LocalDateTimeAdapter;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
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
        switch (exchange.getRequestMethod()) {
            case "GET":
                Integer id = getIdFromPath(exchange.getRequestURI().getPath());
                String response;
                Task task = null;
                if (id == null) {
                    List<Task> tasks = taskManager.getTaskList();
                    response = gson.toJson(tasks);
                } else {
                    task = taskManager.getTask(id);
                    if (task != null) {

                        response = gson.toJson(task);
                        sendText(exchange, response);
                    }
                    response = "Задача не найдена!";
                    sendNotFound(exchange, response);
                }
                if (response.contains("[]")) {
                    response = "Задачи отсутствуют!";
                    sendNotFound(exchange, response);
                } else {
                    sendText(exchange, response);
                }
                break;
            case "POST":
              //  String jsonString = "{\n\"title\": \"Task\",\n\"description\": \"Description\",\n\"status\": \"NEW\"\n}";
                Task task1 = new Task("Задача номер 1", "Создать задачу 1", Status.NEW, Duration.ofMinutes(25),
                       LocalDateTime.of(2001, 1, 1, 0, 0, 0, 0));
               // String t = gson.toJson(task1);
                     InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            //    System.out.println("body: " + body);
               //   String jsonString = "{\n\"title\": \"Task\",\n\"description\": \"Description\",\n\"status\": \"NEW\"\n}";
               // String taskSerialized = gson.toJson(body);
               // String task1 = gson.toJson(taskSerialized);

                System.out.println("Serialized task: " + body);
               // System.out.println("jsonString task: " + jsonString);

                // Task taskDeserialized = gson.fromJson(body, Task.class);
                //   System.out.println("Deserialized task: " + taskDeserialized.getTitle());
                try {
                  //  String taskSerialized = gson.toJson(task1);
                    Task taskDeserialized = gson.fromJson(body, Task.class);
                 //   Task task1 = new Task(taskDeserialized.getTitle(), taskDeserialized.getDescription(), taskDeserialized.getStatus(), taskDeserialized.getDuration(), taskDeserialized.getStartTime());
                    System.out.println("Deserialized task: " + taskDeserialized);
                    taskManager.addTask(taskDeserialized);
                   // System.out.println("Deserialized jelem: " + jelem);
                    System.out.println(taskManager.getTaskList());

                    sendText(exchange, taskDeserialized.toString());
                } catch (JsonIOException | JsonSyntaxException e) {
                    System.out.println("Ошибка десериализации: " + e.getMessage());
                }

                break;
            case "DELETE":
                break;
            default:
                throw new RuntimeException("Неверный метод");
        }
    }

    static class TaskTypeToken extends TypeToken<Task> {

    }
}

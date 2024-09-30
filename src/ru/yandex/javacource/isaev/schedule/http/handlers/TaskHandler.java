package ru.yandex.javacource.isaev.schedule.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.isaev.schedule.exceptions.NotFoundException;
import ru.yandex.javacource.isaev.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.isaev.schedule.http.HttpTaskServer;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
        Integer id;
        switch (exchange.getRequestMethod()) {
            case "GET":
                id = getIdFromPath(exchange.getRequestURI().getPath());
                String response;

                if (id == null) {
                    List<Task> tasks = taskManager.getTaskList();
                    response = gson.toJson(tasks);
                    if (response.equals("[]")) {
                        response = "Задачи отсутствуют!";
                        sendNotFound(exchange, response);
                    } else {
                        sendText(exchange, response, 200);
                    }
                } else {
                    try {
                        Task task = taskManager.getTask(id);
                        response = gson.toJson(task);
                        sendText(exchange, response, 200);
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                }
                break;
            case "POST":
                id = getIdFromPath(exchange.getRequestURI().getPath());
                InputStream inputStream = exchange.getRequestBody();
                String bodyTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task taskDeserialized = null;

                try {
                    taskDeserialized = gson.fromJson(bodyTask, Task.class);
                } catch (JsonIOException | JsonSyntaxException e) {
                    System.out.println("Ошибка десериализации: " + e.getMessage());
                }

                if (taskDeserialized != null) {
                    if (id == null) {
                        try {
                            taskManager.addTask(taskDeserialized);
                            sendText(exchange, "Задача добавлена!", 201);
                        } catch (TaskValidationException e) {
                            sendHasInteractions(exchange, e.getMessage() + " - " + taskDeserialized.getStartTime(), 406);
                        }
                    } else {
                        try {
                            taskDeserialized.setId(id);
                            taskManager.updateTask(taskDeserialized);
                            sendText(exchange, "Задача обновлена!", 201);
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        }
                    }
                } else {
                    sendHasInteractions(exchange, "", 500);
                    System.out.println("Ошибка при обработке запроса");
                }
                break;
            case "DELETE":
                id = getIdFromPath(exchange.getRequestURI().getPath());

                if (id == null) {
                    if (taskManager.getTaskList().isEmpty()) {
                        sendNotFound(exchange, "Задачи не существуют!");
                    }
                    taskManager.deleteAllTask();
                    sendText(exchange, "Задачи удалены!", 200);
                } else {
                    try {
                        taskManager.deleteTask(id);
                        sendText(exchange, "Задача удалена!", 200);
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                }
                break;
            default:
                throw new RuntimeException("Неверный метод");
        }
    }
}
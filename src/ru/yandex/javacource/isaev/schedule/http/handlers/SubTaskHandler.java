package ru.yandex.javacource.isaev.schedule.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.isaev.schedule.exceptions.NotFoundException;
import ru.yandex.javacource.isaev.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.isaev.schedule.http.HttpTaskServer;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubTaskHandler(TaskManager taskManager) {
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
                    List<SubTask> subtasks = taskManager.getSubTaskList();
                    response = gson.toJson(subtasks);
                    if (response.equals("[]")) {
                        response = "Подзадачи отсутствуют!";
                        sendNotFound(exchange, response);
                    } else {
                        sendText(exchange, response, 200);
                    }
                } else {
                    try {
                        response = gson.toJson(taskManager.getSubTask(id));
                        sendText(exchange, response, 200);
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                }
                break;
            case "POST":
                id = getIdFromPath(exchange.getRequestURI().getPath());
                InputStream inputStream = exchange.getRequestBody();
                String bodySubtask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                SubTask subtaskDeserialized = null;

                try {
                    subtaskDeserialized = gson.fromJson(bodySubtask, SubTask.class);
                } catch (JsonIOException | JsonSyntaxException e) {
                    System.out.println("Ошибка десериализации: " + e.getMessage());
                }

                if (subtaskDeserialized != null) {
                    if (id == null) {
                        try {
                            subtaskDeserialized.getEpicId();
                            taskManager.addSubTask(subtaskDeserialized);
                            sendText(exchange, "Подзадача добавлена!", 201);
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        } catch (TaskValidationException e) {
                            sendHasInteractions(exchange, e.getMessage() + " - " + subtaskDeserialized.getStartTime(), 406);
                        }
                    } else {
                        try {
                            subtaskDeserialized.setId(id);
                            taskManager.updateSubTask(subtaskDeserialized);
                            sendText(exchange, "Подзадача обновлена!", 201);
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        }
                    }
                } else {
                    sendHasInteractions(exchange, "", 500);
                }
                break;
            case "DELETE":
                id = getIdFromPath(exchange.getRequestURI().getPath());
                if (id == null) {
                    if (taskManager.getSubTaskList().isEmpty()) {
                        sendNotFound(exchange, "Подзадачи не существуют!");
                    }
                    taskManager.deleteAllSubtask();
                    sendText(exchange, "Подзадачи удалены!", 200);
                } else {
                    try {
                        taskManager.deleteSubTask(id);
                        sendText(exchange, "Подзадача удалена!", 200);
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
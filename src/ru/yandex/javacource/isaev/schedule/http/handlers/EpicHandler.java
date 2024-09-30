package ru.yandex.javacource.isaev.schedule.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.isaev.schedule.exceptions.NotFoundException;
import ru.yandex.javacource.isaev.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.isaev.schedule.http.HttpTaskServer;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Integer id;
        switch (exchange.getRequestMethod()) {
            case "GET":
                id = getIdFromPath(exchange.getRequestURI().getPath());
                String[] parts = exchange.getRequestURI().getPath().split("/");
                String response;

                if (parts.length >= 4 && parts[3].equals("subtasks")) {
                    try {
                        taskManager.getEpic(id);
                        List<SubTask> subtasks = taskManager.getSubTaskList();
                        List<SubTask> epicSubtasks = subtasks.stream()
                                .filter(subTask -> subTask.getEpicId() == id)
                                .collect(Collectors.toList());

                        if (epicSubtasks.isEmpty()) {
                            response = "Подзадачи не найдены!";
                            sendNotFound(exchange, response);
                        } else {
                            response = gson.toJson(epicSubtasks);
                            sendText(exchange, response, 200);
                        }
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                }

                if (id == null) {
                    List<Epic> epics = taskManager.getEpicList();
                    response = gson.toJson(epics);
                    if (response.equals("[]")) {
                        response = "Эпики отсутствуют!";
                        sendNotFound(exchange, response);
                    } else {
                        sendText(exchange, response, 200);
                    }
                } else {
                    try {
                        response = gson.toJson(taskManager.getEpic(id));
                        sendText(exchange, response, 200);
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                }
                break;
            case "POST":
                id = getIdFromPath(exchange.getRequestURI().getPath());
                InputStream inputStream = exchange.getRequestBody();
                String bodyEpic = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Epic epicDeserialized = null;

                try {
                    epicDeserialized = gson.fromJson(bodyEpic, Epic.class);
                } catch (JsonIOException | JsonSyntaxException e) {
                    System.out.println("Ошибка десериализации: " + e.getMessage());
                }

                if (epicDeserialized != null) {
                    if (id == null) {
                        try {
                            taskManager.addEpic(epicDeserialized);
                            sendText(exchange, "Эпик добавлен!", 201);
                        } catch (TaskValidationException e) {
                            sendHasInteractions(exchange,
                                    e.getMessage() + " - " + epicDeserialized.getStartTime(), 406);
                        }
                    } else {
                        try {
                            epicDeserialized.setId(id);
                            taskManager.updateEpic(epicDeserialized);
                            sendText(exchange, "Эпик обновлен!", 201);
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
                    if (taskManager.getEpicList().isEmpty()) {
                        sendNotFound(exchange, "Эпики не существуют!");
                    }
                    taskManager.deleteAllEpic();
                    sendText(exchange, "Эпики удалены!", 200);
                } else {
                    try {
                        taskManager.deleteEpic(id);
                        sendText(exchange, "Эпик удален!", 200);
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
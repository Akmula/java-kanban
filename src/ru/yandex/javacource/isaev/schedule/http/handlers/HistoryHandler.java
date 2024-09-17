package ru.yandex.javacource.isaev.schedule.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.isaev.schedule.http.HttpTaskServer;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            String response;
            List<Task> tasks = taskManager.getHistory();

            if (tasks == null) {
                response = "Задачи отсутствуют!";
                sendNotFound(exchange, response);
            } else {
                response = gson.toJson(tasks);
                sendText(exchange, response, 200);
            }
        } else {
            throw new RuntimeException("Неверный метод");
        }
    }
}
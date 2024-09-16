package ru.yandex.javacource.isaev.schedule.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.isaev.schedule.http.adapters.LocalDateTimeAdapter;
import ru.yandex.javacource.isaev.schedule.http.adapters.TaskAdapter;
import ru.yandex.javacource.isaev.schedule.http.handlers.TaskHandler;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.managers.Managers;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final String HOSTNAME = "localhost";
    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 0);
        this.httpServer.createContext("/tasks", new TaskHandler(taskManager));
    }

    public void serverStart() {
        httpServer.start();
        System.out.println("Сервер запущен!");
    }

    public void serverStop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен!");
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
       // gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();

    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.serverStart();

    }
}

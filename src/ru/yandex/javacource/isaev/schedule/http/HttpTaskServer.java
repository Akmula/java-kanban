package ru.yandex.javacource.isaev.schedule.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.isaev.schedule.http.adapters.EpicAdapter;
import ru.yandex.javacource.isaev.schedule.http.adapters.SubTaskAdapter;
import ru.yandex.javacource.isaev.schedule.http.adapters.TaskAdapter;
import ru.yandex.javacource.isaev.schedule.http.handlers.*;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.managers.FileBackedTaskManager;
import ru.yandex.javacource.isaev.schedule.managers.Managers;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final String PATH_TO_FILE = "src/ru/yandex/javacource/isaev/schedule/resources/data.csv";
    private static final int PORT = 8080;
    private static final String HOSTNAME = "localhost";
    private final HttpServer httpServer;
  //  private final FileBackedTaskManager fileBackedTaskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        Managers.getDefaultHistory();
      //  fileBackedTaskManager = (FileBackedTaskManager) taskManager;
      //  FileBackedTaskManager.loadFromFile(new File(PATH_TO_FILE));
        this.httpServer = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 0);
        this.httpServer.createContext("/tasks", new TaskHandler(taskManager));
        this.httpServer.createContext("/epics", new EpicHandler(taskManager));
        this.httpServer.createContext("/subtasks", new SubTaskHandler(taskManager));
        this.httpServer.createContext("/history", new HistoryHandler(taskManager));
        this.httpServer.createContext("/prioritized", new PrioritizedTaskHandler(taskManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
        gsonBuilder.registerTypeAdapter(Epic.class, new EpicAdapter());
        gsonBuilder.registerTypeAdapter(SubTask.class, new SubTaskAdapter());
        return gsonBuilder.create();

    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }
}
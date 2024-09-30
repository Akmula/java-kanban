package ru.yandex.javacource.isaev.schedule.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.exceptions.NotFoundException;
import ru.yandex.javacource.isaev.schedule.managers.InMemoryTaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    HttpTaskServer httpTaskServer;
    InMemoryTaskManager taskManager;
    Gson gson = HttpTaskServer.getGson();
    HttpResponse<String> response;
    String taskJson;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    void shutDown() {
        httpTaskServer.stop();
    }

    // тест задач
    @Test
    public void testForGettingTasksWithCode200() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskJson = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // получаем списки задач
        List<Task> tasksFromManager = taskManager.getTaskList();
        List<Task> tasksFromServer = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertNotNull(tasksFromServer, "Задачи не возвращаются");
        assertEquals(tasksFromManager, tasksFromServer, "Задачи не совпадают");
    }

    @Test
    public void testForGettingTaskByIdWithCode200() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskJson = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/1");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Task taskFromJson = gson.fromJson(response.body(), Task.class);
        Task taskFromManager = taskManager.getTask(1);

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(taskFromJson, taskFromManager, "Задачи не совпадают");
    }

    @Test
    public void testForGettingTaskByIdWithCode404() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskJson = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/2");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testForAddingTaskWithCode200() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskJson = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(201, response.statusCode());
    }

    @Test
    public void testForAddingTaskWithCode406() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskJson = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Task taskTest = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskJson = gson.toJson(taskTest);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // проверяем код ответа
        assertEquals(406, response.statusCode());
    }

    @Test
    public void taskUpdateTestWithCode201() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskJson = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // обновляем задачу
        Task upadteTask = taskManager.getTask(1);
        upadteTask.setDescription("Testing the task 1 update");
        taskJson = gson.toJson(upadteTask);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/1");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // проверяем код ответа
        assertEquals(201, response.statusCode());
        assertEquals("Testing the task 1 update",
                taskManager.getTask(1).getDescription(), "Некорректное описание задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskJson = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/1");
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что задача удалена
        Exception exception = assertThrows(NotFoundException.class, () -> taskManager.getTask(1));

        String expectedMessage = "Задача с id = 1 не найдена!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    // тесты эпиков
    @Test
    public void testForGettingEpicsWithCode200() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", Status.NEW);
        taskJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // получаем списки задач
        List<Epic> epicsFromManager = taskManager.getEpicList();
        List<Epic> epicsFromServer = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertNotNull(epicsFromServer, "Эпики не возвращаются");
        assertEquals(epicsFromManager, epicsFromServer, "Эпики не совпадают");
    }

    @Test
    public void testForGettingEpicByIdWithCode200() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", Status.NEW);
        taskJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/1");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Epic epicFromJson = gson.fromJson(response.body(), Epic.class);
        Epic epicFromManager = taskManager.getEpic(1);

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(epicFromJson, epicFromManager, "Эпики не совпадают");
    }

    @Test
    public void testForGettingEpicByIdWithCode404() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", Status.NEW);
        taskJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/2");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testForGettingEpicSubtasksByIdWithCode200() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", Status.NEW);
        taskJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        SubTask subTask = new SubTask("Test 3", "Testing subtask 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 1);
        taskJson = gson.toJson(subTask);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/1/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testForGettingEpicSubtasksByIdWithCode404() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/1/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testForAddingEpicWithCode201() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", Status.NEW);
        taskJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(201, response.statusCode());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing task 1", Status.NEW);
        taskJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/1");
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что задача удалена
        Exception exception = assertThrows(NotFoundException.class, () -> taskManager.getEpic(1));

        String expectedMessage = "Эпик с id = 1 не найден!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    // тесты подзадач
    @Test
    public void testForGettingSubtasksWithCode200() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", Status.NEW);
        taskJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        SubTask subTask = new SubTask("Test 3", "Testing subtask 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 1);
        taskJson = gson.toJson(subTask);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // получаем списки задач
        List<SubTask> subtasksFromManager = taskManager.getSubTaskList();
        List<SubTask> subtasksFromServer = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertNotNull(subtasksFromServer, "Эпики не возвращаются");
        assertEquals(subtasksFromManager, subtasksFromServer, "Эпики не совпадают");
    }

    @Test
    public void testForGettingSubtaskByIdWithCode200() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", Status.NEW);
        taskJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        SubTask subTask = new SubTask("Test 3", "Testing subtask 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 1);
        taskJson = gson.toJson(subTask);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/2");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        SubTask subtaskFromJson = gson.fromJson(response.body(), SubTask.class);
        SubTask subtaskFromManager = taskManager.getSubTask(2);

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(subtaskFromJson, subtaskFromManager, "Подзадачи не совпадают");
    }

    @Test
    public void testForGettingSubtaskByIdWithCode404() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", Status.NEW);
        taskJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/2");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing task 1", Status.NEW);
        taskJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        SubTask subTask = new SubTask("Test 3", "Testing subtask 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 1);
        taskJson = gson.toJson(subTask);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/2");
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что задача удалена
        Exception exception = assertThrows(NotFoundException.class, () -> taskManager.getSubTask(2));

        String expectedMessage = "Подзадача с id = 2 не найдена!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testForGettingHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusHours(1));
        Task task3 = new Task("Test 3", "Testing task 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusHours(2));

        taskJson = gson.toJson(task1);
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        taskJson = gson.toJson(task2);
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        taskJson = gson.toJson(task3);
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/1");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/2");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/history");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        List<Task> historyFromManager = taskManager.getHistory();
        List<Task> historyFromServer = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertNotNull(historyFromServer, "Список истории пуст");
        assertEquals(historyFromManager, historyFromServer, "Список не совпадает");
    }

    @Test
    public void testForGettingPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusHours(1));
        Task task3 = new Task("Test 3", "Testing task 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusHours(2));

        taskJson = gson.toJson(task1);
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        taskJson = gson.toJson(task2);
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        taskJson = gson.toJson(task3);
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/prioritized");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        List<Task> prioritizedFromManager = taskManager.getPrioritizedTasks();
        List<Task> prioritizedFromServer = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertNotNull(prioritizedFromServer, "Список истории пуст");
        assertEquals(prioritizedFromManager, prioritizedFromServer, "Список не совпадает");
    }
}
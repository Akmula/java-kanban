package ru.yandex.javacource.isaev.schedule.managers;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    File tempFile;

    {
        try {
            tempFile = File.createTempFile("tempData", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

    @Test
    void testSavingAndUploadingEmptyFile() {
        Path testFilePath = Paths.get(String.valueOf(tempFile));
        fileBackedTaskManager.loadFromFile();
        Task task = fileBackedTaskManager.getTask(1);
        assertTrue(testFilePath.isAbsolute(), "Файл не создан!");
        assertNull(task, "Задача существует! Файл не пустой");
    }

    @Test
    void addTasks() {
        Task task1 = new Task("Задача 1", "Тестовая задача 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Тестовая задача 2", Status.NEW);
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        Epic epic1 = new Epic("Эпик 1", "Тестовый эпик 1", Status.NEW);
        Epic epic2 = new Epic("Эпик 2", "Тестовый эпик 2", Status.NEW);
        fileBackedTaskManager.addEpic(epic1);
        fileBackedTaskManager.addEpic(epic2);
        SubTask subTask1 = new SubTask("Подзадача 1", "Тестовая подзадача 1", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Тестовая подзадача 2", Status.NEW, epic1.getId());
        fileBackedTaskManager.addSubTask(subTask1);
        fileBackedTaskManager.addSubTask(subTask2);
        assertNotNull(fileBackedTaskManager.getTask(task1.getId()), "Задача не создана!");
        assertNotNull(fileBackedTaskManager.getEpic(epic1.getId()), "Эпик не создан!");
        assertNotNull(fileBackedTaskManager.getSubTask(subTask1.getId()), "Подзадача не создана!");
    }

    @Test
    void testUpdateAllTasks() { // тест на обновление задач
        InMemoryTaskManager.setGeneratorId(0);
        Task task1 = new Task("Задача 1", "Тестовая задача 1", Status.NEW);
        fileBackedTaskManager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Тестовый эпик 1", Status.NEW);
        fileBackedTaskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Тестовая подзадача 1", Status.NEW, epic1.getId());
        fileBackedTaskManager.addSubTask(subTask1);

        task1.setTitle("Новая задача");
        fileBackedTaskManager.updateTask(task1);
        epic1.setTitle("Новый эпик");
        fileBackedTaskManager.updateEpic(epic1);
        subTask1.setTitle("Новая подзадача");
        fileBackedTaskManager.updateSubTask(subTask1);

        assertNotEquals(task1, "Новая задача", "Задача не изменилась");
        assertNotEquals(task1, "Новый эпик", "Эпик не изменился");
        assertNotEquals(task1, "Новая подзадача", "Подадача не изменилась");
    }

    @Test
    void loadFromFile() {
        InMemoryTaskManager.setGeneratorId(0);
        Task task1 = new Task("Задача 1", "Тестовая задача 1", Status.NEW);
        fileBackedTaskManager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Тестовый эпик 1", Status.NEW);
        fileBackedTaskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Тестовая подзадача 1", Status.NEW, epic1.getId());
        fileBackedTaskManager.addSubTask(subTask1);

        InMemoryTaskManager.setGeneratorId(0);
        FileBackedTaskManager fileBackedTaskManager1 = fileBackedTaskManager.loadFromFile();
        assertEquals(fileBackedTaskManager.getTaskList(), fileBackedTaskManager1.getTaskList(),
                "Задачи не загружены из файла!");
        assertEquals(fileBackedTaskManager.getEpicList(), fileBackedTaskManager1.getEpicList(),
                "Задачи не загружены из файла!");
        assertEquals(fileBackedTaskManager.getSubTaskList(), fileBackedTaskManager1.getSubTaskList(),
                "Задачи не загружены из файла!");
    }

    @Test
    void testDeletingTasks() {
        InMemoryTaskManager.setGeneratorId(0);
        Task task1 = new Task("Задача 1", "Тестовая задача 1", Status.NEW);
        fileBackedTaskManager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Тестовый эпик 1", Status.NEW);
        fileBackedTaskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Тестовая подзадача 1", Status.NEW, epic1.getId());
        fileBackedTaskManager.addSubTask(subTask1);

        fileBackedTaskManager.deleteAllTask();
        fileBackedTaskManager.deleteAllEpic();
        fileBackedTaskManager.deleteSubtasks();

        InMemoryTaskManager.setGeneratorId(0);
        FileBackedTaskManager fileBackedTaskManager1 = fileBackedTaskManager.loadFromFile();

        assertEquals(fileBackedTaskManager.getTaskList(), fileBackedTaskManager1.getTaskList(),
                "Задачи не удалены из файла!");
        assertEquals(fileBackedTaskManager.getEpicList(), fileBackedTaskManager1.getEpicList(),
                "Задачи не удалены из файла!");
        assertEquals(fileBackedTaskManager.getSubTaskList(), fileBackedTaskManager1.getSubTaskList(),
                "Задачи не удалены из файла!");
    }
}
package ru.yandex.javacource.isaev.schedule.managers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    File tempFile;
    static File tempEmptyFile;
    FileBackedTaskManager fileBackedTaskManager;
    static FileBackedTaskManager fileBackedTaskManagerForEmptyFile;

    @BeforeAll
    static void checkingSavingAndLoadingEmptyFile() {
        try {
            tempEmptyFile = File.createTempFile("tempEmptyData", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileBackedTaskManagerForEmptyFile = new FileBackedTaskManager(tempEmptyFile);

        Task task = fileBackedTaskManagerForEmptyFile.getTask(1);
        assertTrue(tempEmptyFile.isAbsolute(), "Файл не создан!");
        assertNull(task, "Задача существует! Файл не пустой");
    }

    @BeforeEach
    public void beforeEach() {
        try {
            tempFile = File.createTempFile("tempData", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Задача 1", "Тестовая задача 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Тестовая задача 2", Status.NEW);
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        Epic epic1 = new Epic("Эпик 3", "Тестовый эпик 1", Status.NEW);
        Epic epic2 = new Epic("Эпик 4", "Тестовый эпик 2", Status.NEW);
        fileBackedTaskManager.addEpic(epic1);
        fileBackedTaskManager.addEpic(epic2);
        SubTask subTask1 = new SubTask("Подзадача 5", "Тестовая подзадача 1", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача 6", "Тестовая подзадача 2", Status.NEW, epic1.getId());
        fileBackedTaskManager.addSubTask(subTask1);
        fileBackedTaskManager.addSubTask(subTask2);
    }

    @Test
    void checkingSavingOfMultipleTasks() {
        assertNotNull(fileBackedTaskManager.getTask(1), "Задача не создана!");
        assertNotNull(fileBackedTaskManager.getEpic(3), "Эпик не создан!");
        assertNotNull(fileBackedTaskManager.getSubTask(5), "Подзадача не создана!");
    }

    @Test
    void checkingTheLoadingOfMultipleTasks() {
        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile();
        assertEquals(fileBackedTaskManager.getTaskList(), fileBackedTaskManager1.getTaskList(),
                "Задачи не загружены из файла!");
        assertEquals(fileBackedTaskManager.getEpicList(), fileBackedTaskManager1.getEpicList(),
                "Эпики не загружены из файла!");
        assertEquals(fileBackedTaskManager.getSubTaskList(), fileBackedTaskManager1.getSubTaskList(),
                "Подзадачи не загружены из файла!");
    }

    @Test
    void checkingForUpdatesToMultipleTasks() { // тест на обновление задач
        Task task1 = fileBackedTaskManager.getTask(1);
        Epic epic1 = fileBackedTaskManager.getEpic(3);
        SubTask subTask1 = fileBackedTaskManager.getSubTask(5);

        task1.setTitle("Новая задача");
        fileBackedTaskManager.updateTask(task1);
        epic1.setTitle("Новый эпик");
        fileBackedTaskManager.updateEpic(epic1);
        subTask1.setTitle("Новая подзадача");
        fileBackedTaskManager.updateSubTask(subTask1);
        assertEquals(task1.getTitle(), "Новая задача", "Задача не изменилась");
        assertEquals(epic1.getTitle(), "Новый эпик", "Эпик не изменился");
        assertEquals(subTask1.getTitle(), "Новая подзадача", "Подадача не изменилась");
    }

    @Test
    void checkingTheDeletionOfAllTasks() {
        fileBackedTaskManager.deleteAllTask();
        fileBackedTaskManager.deleteAllEpic();
        fileBackedTaskManager.deleteSubtasks();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile();

        assertEquals(fileBackedTaskManager.getTaskList(), fileBackedTaskManager1.getTaskList(),
                "Задачи не удалены из файла!");
        assertEquals(fileBackedTaskManager.getEpicList(), fileBackedTaskManager1.getEpicList(),
                "Задачи не удалены из файла!");
        assertEquals(fileBackedTaskManager.getSubTaskList(), fileBackedTaskManager1.getSubTaskList(),
                "Задачи не удалены из файла!");
    }
}
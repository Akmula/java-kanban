package ru.yandex.javacource.isaev.schedule.managers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.javacource.isaev.schedule.enums.Status.NEW;

final class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    public static File tempFile;

    static FileBackedTaskManager fileBackedTaskManagerFromFile;

    @Override
    protected FileBackedTaskManager createManagers() {
        return new FileBackedTaskManager(tempFile);
    }

    @BeforeAll
    public static void checkingFileSaving() { // проверка сохранения файла
        try {
            tempFile = File.createTempFile("tempData", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(tempFile.isAbsolute(), "Файл не создан!");
    }

    @Test
    public void checkingLoadFileException() { // проверка чтения несуществующего файла
        Exception exception = assertThrows(RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(new File("tempFile")));

        String expectedMessage = "Ошибка чтения файла!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void checkingSaveFileException() { // проверка записи в несуществующий файл
        assertDoesNotThrow(() -> {
            FileBackedTaskManager fileBackedTaskManagerTest = new FileBackedTaskManager(File.createTempFile("tempData", ".csv"));
            fileBackedTaskManagerTest.addTask(new Task("Задача 3", "Описание задачи 3", NEW, Duration.ofMinutes(10),
                    LocalDateTime.of(2010, 1, 1, 0, 30, 0, 0)));
        });
    }

    @Test
    void checkingSavingAndLoadingEmptyFile() { // проверка сохранения и загрузки пустого файла
        File tempEmptyFile;
        try {
            tempEmptyFile = File.createTempFile("tempEmpty", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileBackedTaskManager fileBackedTaskManagerForEmptyFile = new FileBackedTaskManager(tempEmptyFile);
        Task task = fileBackedTaskManagerForEmptyFile.getTask(1);
        assertTrue(tempEmptyFile.isAbsolute(), "Файл не создан!");
        assertNull(task, "Задача существует! Файл не пустой");
    }

    @Test
    void checkingTheLoadingOfMultipleTasks() { // проверка загрузки всех задач
        fileBackedTaskManagerFromFile = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(taskManager.getTaskList(), fileBackedTaskManagerFromFile.getTaskList(),
                "Задачи не загружены из файла!");
        assertEquals(taskManager.getEpicList(), fileBackedTaskManagerFromFile.getEpicList(),
                "Эпики не загружены из файла!");
        assertEquals(taskManager.getSubTaskList(), fileBackedTaskManagerFromFile.getSubTaskList(),
                "Подзадачи не загружены из файла!");
    }

    @Test
    void checkingTheDeletionOfAllTasksFromTheFile() { // проверка удаления всех задач из файла
        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();
        taskManager.deleteAllSubtask();
        fileBackedTaskManagerFromFile = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(taskManager.getTaskList(), fileBackedTaskManagerFromFile.getTaskList(),
                "Задачи не удалены из файла!");
        assertEquals(taskManager.getEpicList(), fileBackedTaskManagerFromFile.getEpicList(),
                "Задачи не удалены из файла!");
        assertEquals(taskManager.getSubTaskList(), fileBackedTaskManagerFromFile.getSubTaskList(),
                "Задачи не удалены из файла!");
    }

    @Test
    void checkingForUpdatesToMultipleTasks() { // тест на обновление задач
        fileBackedTaskManagerFromFile = FileBackedTaskManager.loadFromFile(tempFile);
        Task task = fileBackedTaskManagerFromFile.getTask(1);
        Epic epic = fileBackedTaskManagerFromFile.getEpic(3);
        SubTask subTask = fileBackedTaskManagerFromFile.getSubTask(5);
        task.setTitle("Новая задача");
        fileBackedTaskManagerFromFile.updateTask(task);
        epic.setTitle("Новый эпик");
        fileBackedTaskManagerFromFile.updateEpic(epic);
        subTask.setTitle("Новая подзадача");
        fileBackedTaskManagerFromFile.updateSubTask(subTask);
        assertEquals(task.getTitle(), "Новая задача", "Задача не изменилась");
        assertEquals(epic.getTitle(), "Новый эпик", "Эпик не изменился");
        assertEquals(subTask.getTitle(), "Новая подзадача", "Подзадача не изменилась");
    }
}
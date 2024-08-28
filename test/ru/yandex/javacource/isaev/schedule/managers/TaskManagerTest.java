package ru.yandex.javacource.isaev.schedule.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.isaev.schedule.enums.TaskType;
import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.javacource.isaev.schedule.enums.Status.NEW;

abstract class TaskManagerTest<T extends TaskManager> {
    Task taskId1;
    Task taskId2;
    Epic epicId3;
    Epic epicId4;
    SubTask subTaskId5;
    SubTask subTaskId6;
    protected HistoryManager historyManager;
    protected T taskManager;

    protected abstract T createManagers();

    @BeforeEach
    void beforeEach() { // создать менеджеры, задачи и добавить их
        taskManager = createManagers();
        historyManager = Managers.getDefaultHistory();
        taskId1 = new Task("Задача 1", "Описание задачи 1", NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0));
        taskId2 = new Task(2, TaskType.TASK, "Задача 2", "Описание задачи 2", NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2000, 1, 1, 0, 15, 0, 0));
        epicId3 = new Epic("Эпик 1", "Описание эпика 1", NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2002, 1, 1, 0, 30, 0, 0));
        epicId4 = new Epic("Эпик 2", "Описание эпика 2", NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2003, 1, 1, 0, 45, 0, 0));
        subTaskId5 = new SubTask("Подзадача 1", "Описание подзадачи 1", NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2004, 1, 1, 1, 0, 0, 0), 3);
        subTaskId6 = new SubTask("Подзадача 2", "Описание подзадачи 2", NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2005, 1, 1, 0, 15, 0, 0), 4);
        taskManager.addTask(taskId1);
        taskManager.addTask(taskId2);
        taskManager.addEpic(epicId3);
        taskManager.addEpic(epicId4);
        taskManager.addSubTask(subTaskId5);
        taskManager.addSubTask(subTaskId6);
    }

    @Test
    void checkingTheAdditionOfTasks() { // проверка добавления задач
        assertNotNull(taskManager.getTask(1), "Задача не создана!");
        assertNotNull(taskManager.getEpic(3), "Эпик не создан!");
        assertNotNull(taskManager.getSubTask(5), "Подзадача не создана!");
    }

    @Test
    void checkingForTaskUpdates() { // проверка обновления задач
        Task updateTaskId1 = new Task(1, TaskType.TASK, "Задача 1", "Обновленная задача 1",
                NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0));
        Epic updateEpic = taskManager.getEpic(epicId3.getId());
        SubTask updateSubTaskId5 = new SubTask(5, TaskType.SUBTASK, "Подзадача 1",
                "Обновленная подзадача 1", NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2004, 1, 1, 0, 0, 0, 0), 3);
        updateTaskId1.setDescription("Обновленная задача 1");
        updateEpic.setDescription("Обновленный эпик 1");
        updateSubTaskId5.setDescription("Обновленная подзадача 1");
        taskManager.updateTask(updateTaskId1);
        taskManager.updateEpic(updateEpic);
        taskManager.updateSubTask(updateSubTaskId5);
        assertEquals(taskManager.getTask(1).getDescription(),
                "Обновленная задача 1", "Задача 1 не изменилась.");
        assertEquals(taskManager.getEpic(3).getDescription(),
                "Обновленный эпик 1", "Эпик 1 не изменился.");
        assertEquals(taskManager.getSubTask(5).getDescription(),
                "Обновленная подзадача 1", "Подадача 1 не изменилась.");
    }

    @Test
    void checkingTheDeletionOfTasks() { // проверка удаления задач
        taskManager.deleteTask(taskId1.getId());
        taskManager.deleteEpic(epicId3.getId());
        taskManager.deleteSubTask(subTaskId6.getId());

        assertNull(taskManager.getTask(taskId1.getId()), "Задача не удалена!");
        assertNull(taskManager.getEpic(epicId3.getId()), "Эпик не удален!");
        assertNull(taskManager.getSubTask(subTaskId6.getId()), "Подзадача не удалена!");
    }

    @Test
    void checkingTheDeletionOfAllTasks() { // проверка удаления всех задач
        taskManager.deleteAllTask();
        taskManager.deleteAllSubtask();
        taskManager.deleteAllEpic();

        assertEquals(taskManager.getTaskList().toString(), "[]", "Задачи не удалены!");
        assertEquals(taskManager.getEpicList().toString(), "[]", "Эпики не удалены!");
        assertEquals(taskManager.getSubTaskList().toString(), "[]", "Подзадачи не удалены!");
    }

    @Test
    void checkingTheAdditionOfTasksToTheSortedList() { // проверка добавления задач в сортированный список
        Set<Task> tasks = taskManager.getPrioritizedTasks();
        Task verificationTask = new Task("Проверочная задача", "Описание проверочной задачи", NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(2007, 1, 1, 0, 0, 0, 0));
        taskManager.addTask(verificationTask);

        assertEquals(tasks, taskManager.getPrioritizedTasks(), "Список не совпадает, задача добавлена!");
        assertNotEquals(taskManager.getPrioritizedTasks().contains(verificationTask),
                null, "Задача добавлена!");
    }
}
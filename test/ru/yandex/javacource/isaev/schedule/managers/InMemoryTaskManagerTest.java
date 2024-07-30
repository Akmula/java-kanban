package ru.yandex.javacource.isaev.schedule.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.javacource.isaev.schedule.enums.Status.*;

class InMemoryTaskManagerTest {
    TaskManager taskManager;
    Task task;
    Task taskId2;
    Epic epic;
    Epic epicId4;
    SubTask subTask;
    SubTask subTaskId6;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        task = new Task("Задача 1", "Тестовая задача с генерацией id №1", NEW);
        taskManager.addTask(task);
        taskId2 = new Task(2, "Задача 2", "Тестовая задача с заданной id №2", NEW);
        taskManager.addTask(taskId2);
        epic = new Epic("Эпик 1", "Id №3", NEW);
        taskManager.addEpic(epic);
        epicId4 = new Epic("Эпик 2", "Id №4", NEW);
        taskManager.addEpic(epicId4);
        subTask = new SubTask("Подзадача 1", "Тестовая подзадача Id №5", NEW, epic.getId());
        taskManager.addSubTask(subTask);
        subTaskId6 = new SubTask("Подзадача 2", "Тестовая подзадача Id №6", NEW, epicId4.getId());
        taskManager.addSubTask(subTaskId6);
    }

    // проверяем, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    void checkThatInstancesOfTheTaskClassAreEqualToEachOtherIfTheirIdIsEqual() {
        final int id = task.getId();
        final Task savedTask = taskManager.getTask(id);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    // проверяем, что наследники класса Task равны друг другу, если равен их id
    @Test
    void checkingThatTheHeirsOfTheTaskClassAreEqualToEachOtherIfTheirIdIsEqual() {
        final int id = epic.getId();
        final Epic savedEpic = taskManager.getEpic(id);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    // проверяем, что объект Epic нельзя добавить в самого себя в виде подзадачи / нельзя, потому что разные объекты
    // проверяем, что объект Subtask нельзя сделать своим же эпиком / нельзя, потому что разные объекты

    // убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    @Test
    void checkingTheUtilityClass() {
        TaskManager taskManagerTest = Managers.getDefault();
        HistoryManager historyManagerTest = Managers.getDefaultHistory();

        assertInstanceOf(TaskManager.class, taskManagerTest, "Класс не инициализирован!");
        assertInstanceOf(HistoryManager.class, historyManagerTest, "Класс не инициализирован!");
    }

    //  проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    @Test
    void checkingThatInMemoryTaskManagerAddsTasksOfDifferentTypesAndCanFindThemById() {
        assertNotNull(taskManager.getTask(task.getId()), "Задача не добавлена");
        assertNotNull(taskManager.getEpic(epic.getId()), "Эпик не добавлен");
        assertNotNull(taskManager.getSubTask(subTask.getId()), "Подзадача не добавлена");
    }

    // проверяем, что задачи с заданным и сгенерированным id не конфликтуют внутри менеджера
    @Test
    void weCheckThatTasksWithSpecifiedAndGeneratedIdNotConflict() {
        Task savedTask1 = taskManager.getTask(1);
        Task savedTask2 = taskManager.getTask(2);

        assertEquals(savedTask1, task, "Задачи 1 не равны");
        assertEquals(savedTask2, taskId2, "Задачи 2 не равны");
    }

    //  создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер

    // удаление всех типов задач
    @Test
    void checkDeletingAllTypesOfTasks() {
        taskManager.deleteTask(task.getId());
        taskManager.deleteSubTask(subTask.getId());
        taskManager.deleteEpic(epic.getId());
        assertNull(taskManager.getTask(task.getId()), "Задача не удалена");
        assertNull(taskManager.getEpic(epic.getId()), "Эпик не удален");
        assertNull(taskManager.getSubTask(subTask.getId()), "Подзадача не удалена");
    }

    // удаление списка всех задач
    @Test
    void deletingAllTasksList() {
        taskManager.deleteAllTask();
        taskManager.deleteSubtasks();
        taskManager.deleteAllEpic();
        assertNotNull(taskManager.getTaskList(), "Список не пуст");
        assertNotNull(taskManager.getEpicList(), "Список не пуст");
        assertNotNull(taskManager.getSubTaskList(), "Список не пуст");
    }

    // проверка обновления статуса эпика
    @Test
    void checkingForEpicStatusUpdate() {
        final int id = epicId4.getId();
        epicId4.setDescription("Удаленный эпик");
        taskManager.deleteEpic(id);
        taskManager.updateEpic(epicId4);
        Epic deletedEpic = taskManager.getEpic(id);
        taskManager.deleteEpic(id);
        SubTask subTask1 = new SubTask("Подзадача 1", "Тестовая подзадача 1", NEW, epic.getId());
        taskManager.addSubTask(subTask1);
        Epic updateEpic = new Epic(epic.getId(),
                "Эпик 1", "Тестовый эпик 1", DONE, epic.getSubTaskIdList());
        taskManager.updateEpic(updateEpic);

        Status status = taskManager.getEpic(epic.getId()).getStatus();
        assertEquals(NEW, status, "Статус эпика нельзя менять вручную.");
        SubTask updateSubTask1 = new SubTask(subTask1.getId(), "Подзадача 1",
                "Тестовая подзадача 1", IN_PROGRESS, subTask1.getEpicId());
        taskManager.updateSubTask(updateSubTask1);

        Epic statusEpic = taskManager.getEpic(updateEpic.getId());
        Status status2 = statusEpic.getStatus();

        assertEquals(IN_PROGRESS, status2, "Статус эпика не поменялся.");

        assertNull(deletedEpic, "Эпик не удален и обновлен.");
    }

    // добавление задач в историю просмотров
    @Test
    void checkingTheAdditionOfTasksToTheBrowsingHistory() {
        taskManager.getTask(task.getId());
        taskManager.getTask(taskId2.getId());
        final Task savedTask = taskManager.getTask(taskId2.getId());
        taskManager.getTask(task.getId());
        List<Task> history = taskManager.getHistory();
        final Task historyTask = history.getFirst();
        assertNotNull(historyTask, "Задача не найдена.");
        assertEquals(savedTask, historyTask, "Задачи не совпадают.");
    }

    // удаление задачи из истории
    @Test
    void checkingRemoveTaskOfHistory() {
        Task firstHistory = taskManager.getTask(task.getId());
        Task lastHistory = taskManager.getEpic(epicId4.getId());
        taskManager.getTask(taskId2.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubTask(subTask.getId());
        taskManager.deleteTask(task.getId());
        taskManager.getEpic(epicId4.getId());
        taskManager.deleteTask(epicId4.getId());
        final List<Task> history = taskManager.getHistory();
        Task deleteFirstHistory = history.getFirst();
        Task deleteLastHistory = history.getLast();
        assertNotEquals(firstHistory, deleteFirstHistory, "Задачи совпадают. Задача 1 не удалена!");
        assertNotEquals(lastHistory, deleteLastHistory, "Задачи совпадают. Задача 2 не удалена!");
    }

    // проверка обновления задачи
    @Test
    void checkingUpdateTask() {
        task.setDescription("Обновленная задача 1");
        taskId2.setDescription("Обновленная задача 2");
        taskManager.deleteTask(taskId2.getId());
        taskManager.updateTask(task);
        taskManager.updateTask(taskId2);
        Task updateTask = taskManager.getTask(taskId2.getId());
        assertEquals(task.getDescription(), "Обновленная задача 1", "Задачи не изменилась.");
        assertNull(updateTask, "Задача не удалена и обновлена.");
    }

    @Test
    void checkingDeleteAllSubTasks() { // тест удаления всех подзадач
        taskManager.deleteSubtasks(); // удаляем все подзадачи
        final List<Integer> subTasks = epic.getSubTaskIdList();
        assertEquals(subTasks.size(), 0, "Подзадачи не удалены!");
    }
}
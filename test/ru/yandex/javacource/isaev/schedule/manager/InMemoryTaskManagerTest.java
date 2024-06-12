package ru.yandex.javacource.isaev.schedule.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.task.Epic;
import ru.yandex.javacource.isaev.schedule.task.Status;
import ru.yandex.javacource.isaev.schedule.task.SubTask;
import ru.yandex.javacource.isaev.schedule.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.javacource.isaev.schedule.task.Status.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager = Managers.getDefault();

    // проверяем, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    void addNewTask() {
        Task task = new Task("Тест добавления задачи", "Описание", NEW);
        taskManager.addTask(task);
        final int id = task.getId();
        final Task savedTask = taskManager.getTask(id);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    // проверяем, что наследники класса Task равны друг другу, если равен их id
    @Test
    void addNewEpic() {
        Epic epic = new Epic("Тест добавления эпика", "Описание", NEW);
        taskManager.addEpic(epic);
        final int id = epic.getId();
        final Epic savedEpic = taskManager.getEpic(id);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
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
    void testInMemoryTaskManager() {
        taskManager.addTask(new Task("Задача", "Тестовая задача", NEW));
        Epic epic = new Epic("Эпик", "Тестовый эпик", NEW);
        taskManager.addEpic(epic);
        taskManager.addSubTask(new SubTask(epic.getId(), "Подзадача", "Тестовая подзадача", NEW));

        assertNotNull(taskManager.getTask(1), "Задача не добавлена");
        assertNotNull(taskManager.getEpic(2), "Эпик не добавлен");
        assertNotNull(taskManager.getSubTask(3), "Подзадача не добавлена");
    }

    // проверяем, что задачи с заданным и сгенерированным id не конфликтуют внутри менеджера
    @Test
    void weCheckThatTasksWithSpecifiedAndGeneratedIdNotConflict() {
        Task task1 = new Task("Задача 1", "Тестовая задача с генерацией id", NEW);
        Task task2 = new Task(2, "Задача 2", "Тестовая задача с заданной id", NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Task savedTask1 = taskManager.getTask(1);
        Task savedTask2 = taskManager.getTask(2);

        assertEquals(savedTask1, task1, "Задачи 1 не равны");
        assertEquals(savedTask2, task2, "Задачи 2 не равны");
    }

    //  создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    //  убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных

    @Test
    void tasksThatAreAddedToHistoryKeepPreviousVersion() {
        Task task1 = new Task("Задача 1", "Тестовая задача 1", NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Задача 2", "Тестовая задача 2", NEW);
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Эпик 1", "Тестовый эпик 1", NEW);
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Подзадача 1", "Тестовая подзадача 1", NEW);
        taskManager.addSubTask(subTask1);

        taskManager.getTask(task1.getId()); // 0 = задача уйдет из истории
        taskManager.getTask(task2.getId()); // 1 = 0
        taskManager.getEpic(epic1.getId()); // 2 = 1
        taskManager.getSubTask(subTask1.getId()); // 3 = 2

        Task editTask2 = new Task(task2.getId(), "Задача 2", "Измененная задача 2", NEW);
        taskManager.updateTask(editTask2);
        Epic editEpic1 = new Epic(epic1.getId(), epic1.getSubTaskId(), "Эпик 1",
                "Измененный эпик 1", NEW);
        taskManager.updateEpic(editEpic1);
        SubTask editSubTask1 = new SubTask(subTask1.getId(), subTask1.getEpicId(),
                "Подзадача 1", "Измененная подзадача 1", NEW);
        taskManager.updateSubTask(editSubTask1);

        taskManager.getTask(editTask2.getId()); // 4 = 3
        taskManager.getEpic(editEpic1.getId()); // 5 = 4
        taskManager.getSubTask(editSubTask1.getId()); // 6 = 5
        taskManager.getTask(editTask2.getId()); // 7 = 6
        taskManager.getEpic(editEpic1.getId()); // 8 = 7
        taskManager.getSubTask(editSubTask1.getId()); // 9 = 8
        taskManager.getTask(editTask2.getId()); // 10 = 9

        List<Task> history = taskManager.getHistory();
        assertNotEquals(editTask2, history.get(0), "Задачи совпадают!");
        assertNotEquals(editEpic1, history.get(1), "Задачи совпадают!");
        assertNotEquals(epic1, history.get(6), "Задачи совпадают!");
        assertNotEquals(task2, history.get(5), "Задачи совпадают!");
        assertNotEquals(11, history.size(), "В списке 11 задач");
    }

    // удаление всех типов задач
    @Test
    void deletingAllTypesOfTasks() {
        Task task1 = new Task("Задача 1", "Тестовая задача 1", NEW);
        taskManager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Тестовый эпик 1", NEW);
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Подзадача 2", "Тестовая подзадача 1", NEW);
        taskManager.addSubTask(subTask1);

        taskManager.deleteTask(task1.getId());
        taskManager.deleteSubTask(subTask1.getId());
        taskManager.deleteEpic(epic1.getId());

        assertNotNull(taskManager.getTaskList(), "Список не пуст");
        assertNotNull(taskManager.getEpicList(), "Список не пуст");
        assertNotNull(taskManager.getSubTaskList(), "Список не пуст");
    }

    // удаление списка всех задач
    @Test
    void deletingAllTasksList() {
        Task task1 = new Task("Задача 1", "Тестовая задача 1", NEW);
        taskManager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Тестовый эпик 1", NEW);
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Подзадача 1", "Тестовая подзадача 1", NEW);
        taskManager.addSubTask(subTask1);

        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();
        taskManager.deleteSubtasks();
        assertNotNull(taskManager.getTaskList(), "Список не пуст");
        assertNotNull(taskManager.getEpicList(), "Список не пуст");
        assertNotNull(taskManager.getSubTaskList(), "Список не пуст");
    }

    // проверка обновления статуса эпика
    @Test
    void checkingForEpicStatusUpdate() {
        Epic epic1 = new Epic("Эпик 1", "Тестовый эпик 1", NEW);
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Подзадача 1", "Тестовая подзадача 1", NEW);
        taskManager.addSubTask(subTask1);
        Epic updateEpic = new Epic(epic1.getId(), epic1.getSubTaskId(),
                "Эпик 1", "Тестовый эпик 1", DONE);
        taskManager.updateEpic(updateEpic);

        Status status = taskManager.getEpic(epic1.getId()).getStatus();
        assertEquals(NEW, status, "Статус эпика нельзя менять вручную.");
        SubTask updateSubTask1 = new SubTask(subTask1.getId(), subTask1.getEpicId(), "Подзадача 1",
                "Тестовая подзадача 1", IN_PROGRESS);
        taskManager.updateSubTask(updateSubTask1);

        Epic statusEpic = taskManager.getEpic(updateEpic.getId());
        Status status2 = statusEpic.getStatus();

        assertEquals(IN_PROGRESS, status2, "Статус эпика не поменялся.");
    }
}
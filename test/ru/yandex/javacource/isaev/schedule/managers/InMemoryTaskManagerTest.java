package ru.yandex.javacource.isaev.schedule.managers;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.javacource.isaev.schedule.enums.Status.*;

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
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
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
    void testInMemoryTaskManager() {
        Task task = new Task("Задача", "Тестовая задача", NEW);
        taskManager.addTask(task);
        Epic epic = new Epic("Эпик", "Тестовый эпик", NEW);
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Подзадача", "Тестовая подзадача", NEW, epic.getId());
        taskManager.addSubTask(subTask);

        assertNotNull(taskManager.getTask(task.getId()), "Задача не добавлена");
        assertNotNull(taskManager.getEpic(epic.getId()), "Эпик не добавлен");
        assertNotNull(taskManager.getSubTask(subTask.getId()), "Подзадача не добавлена");
    }

    // проверяем, что задачи с заданным и сгенерированным id не конфликтуют внутри менеджера
    @Test
    void weCheckThatTasksWithSpecifiedAndGeneratedIdNotConflict() {
        FileBackedTaskManager.setGeneratorId(0);
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

    // удаление всех типов задач
    @Test
    void deletingAllTypesOfTasks() {
        Task task1 = new Task("Задача 1", "Тестовая задача 1", NEW);
        taskManager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Тестовый эпик 1", NEW);
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Тестовая подзадача 1", NEW, epic1.getId());
        taskManager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Подзадача 2", "Тестовая подзадача 2", NEW, epic1.getId());
        taskManager.addSubTask(subTask2);
        SubTask subTask3 = new SubTask("Подзадача 3", "Тестовая подзадача 3", NEW, epic1.getId());
        taskManager.addSubTask(subTask3);

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
        SubTask subTask1 = new SubTask("Подзадача 1", "Тестовая подзадача 1", NEW, epic1.getId());
        taskManager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Подзадача 1", "Тестовая подзадача 1", NEW, epic1.getId());
        taskManager.addSubTask(subTask2);

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
        Epic epic2 = new Epic("Эпик 2", "Тестовый эпик 2", NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        int id = epic2.getId();
        epic2.setDescription("Удаленный эпик");
        taskManager.deleteEpic(id);
        taskManager.updateEpic(epic2);
        Epic deletedEpic = taskManager.getEpic(id);
        taskManager.deleteEpic(id);
        SubTask subTask1 = new SubTask("Подзадача 1", "Тестовая подзадача 1", NEW, epic1.getId());
        taskManager.addSubTask(subTask1);
        Epic updateEpic = new Epic(epic1.getId(),
                "Эпик 1", "Тестовый эпик 1", DONE, epic1.getSubTaskIdList());
        taskManager.updateEpic(updateEpic);

        Status status = taskManager.getEpic(epic1.getId()).getStatus();
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
        Task task = new Task("Задача 1", "Задача 1", NEW);
        Task task1 = new Task("Задача 2", "Задача 2", NEW);
        Task task2 = new Task("Задача 3", "Задача 3", NEW);
        taskManager.addTask(task);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTask(task.getId());
        taskManager.getTask(task1.getId());
        final Task savedTask = taskManager.getTask(task2.getId());
        taskManager.getTask(task1.getId());
        List<Task> history = taskManager.getHistory();
        final Task historyTask = history.get(1);
        assertNotNull(historyTask, "Задача не найдена.");
        assertEquals(savedTask, historyTask, "Задачи не совпадают.");
    }

    // удаление задачи из истории
    @Test
    void checkingRemoveTaskOfHistory() {
        FileBackedTaskManager.setGeneratorId(0);
        Task firstHistory = taskManager.addTask(new Task("Задача 1", "Задача 1", NEW));
        Task lastHistory = taskManager.addTask(new Task("Задача 2", "Задача 2", NEW));
        taskManager.addTask(new Task("Задача 3", "Задача 3", NEW));
        taskManager.addEpic(new Epic("Задача 4", "Задача 4", NEW));
        taskManager.addSubTask(new SubTask("Задача 5", "Задача 5", NEW, 4));
        taskManager.getTask(1);
        taskManager.getTask(3);
        taskManager.getTask(2);
        taskManager.getEpic(4);
        taskManager.getSubTask(5);
        taskManager.deleteTask(1);
        taskManager.getTask(3);
        taskManager.deleteTask(3);
        List<Task> history = taskManager.getHistory();
        Task deleteFirstHistory = history.getFirst();
        Task deleteLastHistory = history.getLast();
        assertNotEquals(firstHistory, deleteFirstHistory, "Задачи совпадают. Задача 1 не удалена!");
        assertNotEquals(lastHistory, deleteLastHistory, "Задачи совпадают. Задача 2 не удалена!");
    }

    // проверка обновления задачи
    @Test
    void checkingUpdateTask() {
        Task task = new Task("Задача 1", "Задача 1", NEW);
        Task task2 = new Task("Задача 2", "Задача 2", NEW);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        task.setDescription("Обновленная задача 1");
        task2.setDescription("Обновленная задача 2");
        taskManager.deleteTask(task2.getId());
        taskManager.updateTask(task);
        taskManager.updateTask(task2);
        Task updateTask = taskManager.getTask(task2.getId());
        assertEquals(task.getDescription(), "Обновленная задача 1", "Задачи не изменилась.");
        assertNull(updateTask, "Задача не удалена и обновлена.");
    }

    @Test
    void checkingDeleteAllSubTasks() { // тест удаления всех подзадач
        Epic epic = new Epic("Эпик", "Удаление подзадач эпика", NEW);
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Подзадача 1", "Тест удаления подзадач", NEW, epic.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Тест удаления подзадач", NEW, epic.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.deleteSubtasks(); // удаляем все подзадачи
        List<Integer> subTasks = new ArrayList<>(epic.getSubTaskIdList());
        assertNotEquals(subTasks, "[]", "Подзадачи не удалены!");
    }
}
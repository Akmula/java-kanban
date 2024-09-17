package ru.yandex.javacource.isaev.schedule.managers;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.exceptions.NotFoundException;
import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.javacource.isaev.schedule.enums.Status.*;

final class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManagers() {
        return new InMemoryTaskManager();
    }

    // проверяем, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    void checkThatInstancesOfTheTaskClassAreEqualToEachOtherIfTheirIdIsEqual() {
        final int id = taskId1.getId();
        final Task savedTask = taskManager.getTask(id);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(taskId1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(taskId1, tasks.getFirst(), "Задачи не совпадают.");
    }

    // проверяем, что наследники класса Task равны друг другу, если равен их id
    @Test
    void checkingThatTheHeirsOfTheTaskClassAreEqualToEachOtherIfTheirIdIsEqual() {
        final int id = epicId4.getId();
        final Epic savedEpic = taskManager.getEpic(id);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epicId4, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpicList();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(epicId4, epics.get(1), "Эпики не совпадают.");
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

    // проверяем, что задачи с заданным и сгенерированным id не конфликтуют внутри менеджера
    @Test
    void weCheckThatTasksWithSpecifiedAndGeneratedIdNotConflict() {
        Task savedTask1 = taskManager.getTask(1);
        Task savedTask2 = taskManager.getTask(2);

        assertEquals(savedTask1, taskId1, "Задачи 1 не равны");
        assertEquals(savedTask2, taskId2, "Задачи 2 не равны");
    }

    // проверка обновления статуса эпика
    @Test
    void checkingForEpicStatusUpdate() {
        final int id = epicId4.getId();
        epicId4.setDescription("Удаленный эпик");
        taskManager.deleteEpic(id);

        SubTask subTask1 = new SubTask("Подзадача 1", "Тестовая подзадача 1", NEW, epicId3.getId());
        taskManager.addSubTask(subTask1);
        Epic updateEpic = new Epic(epicId3.getId(),
                "Эпик 1", "Тестовый эпик 1", DONE, epicId3.getSubTaskIdList());
        taskManager.updateEpic(updateEpic);

        Status status = taskManager.getEpic(epicId3.getId()).getStatus();
        assertEquals(NEW, status, "Статус эпика нельзя менять вручную.");
        SubTask updateSubTask1 = new SubTask(subTask1.getId(), "Подзадача 1",
                "Тестовая подзадача 1", IN_PROGRESS, subTask1.getEpicId());
        taskManager.updateSubTask(updateSubTask1);

        Epic statusEpic = taskManager.getEpic(updateEpic.getId());
        Status status2 = statusEpic.getStatus();

        assertThrows(NotFoundException.class, () -> taskManager.updateEpic(epicId4));
        assertThrows(NotFoundException.class, () ->  taskManager.deleteEpic(id));
        assertEquals(IN_PROGRESS, status2, "Статус эпика не поменялся.");
    }

    // добавление задач в историю просмотров
    @Test
    void checkingTheAdditionOfTasksToTheBrowsingHistory() {
        taskManager.getTask(taskId1.getId());
        taskManager.getTask(taskId2.getId());
        final Task savedTask = taskManager.getTask(taskId2.getId());
        taskManager.getTask(taskId1.getId());
        List<Task> history = taskManager.getHistory();
        final Task historyTask = history.getFirst();

        assertNotNull(historyTask, "Задача не найдена.");
        assertEquals(savedTask, historyTask, "Задачи не совпадают.");
    }

    // удаление задачи из истории
    @Test
    void checkingRemoveTaskOfHistory() {
        Task firstHistory = taskManager.getTask(taskId1.getId());
        Task lastHistory = taskManager.getEpic(epicId4.getId());
        taskManager.getTask(taskId2.getId());
        taskManager.getEpic(epicId3.getId());
        taskManager.getSubTask(subTaskId5.getId());
        taskManager.deleteTask(taskId1.getId());
        taskManager.getEpic(epicId4.getId());
        taskManager.deleteEpic(epicId4.getId());
        final List<Task> history = taskManager.getHistory();
        Task deleteFirstHistory = history.getFirst();
        Task deleteLastHistory = history.getLast();

        assertNotEquals(firstHistory, deleteFirstHistory, "Задачи совпадают. Задача 1 не удалена!");
        assertNotEquals(lastHistory, deleteLastHistory, "Задачи совпадают. Задача 2 не удалена!");
    }

    @Test
    public void checkingThatTheStartOfTheTaskIsEarlierThanTheEnd() { // проверка, что начало задачи раньше окончания
        LocalDateTime startTime = taskId1.getStartTime();
        LocalDateTime endTime = taskId1.getEndTime();

        assertTrue(startTime.isBefore(endTime), "Время начала не может быть позже окончания!");
    }

    // проверка, что время между началом и окончанием задачи равно продолжительности задачи
    @Test
    public void checkingThatTheTimeBetweenTheStartAndEndOfTheTaskIsEqualToTheDurationOfTheTask() {
        LocalDateTime startTime = taskId1.getStartTime();
        LocalDateTime endTime = taskId1.getEndTime();
        Duration duration = taskId1.getDuration();

        assertEquals(Duration.between(startTime, endTime), duration, "Продолжительность задачи не совпадает!");
    }

    // проверка статусов эпика - все подзадачи со статусом NEW
    @Test
    public void checkingTheEpicStatusesOfAllSubtasksWithTheNEWStatus() {
        taskManager.addSubTask(new SubTask("Подзадача 3", "Описание подзадачи 3", NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2006, 1, 1, 0, 0, 0, 0), 4));
        taskManager.addSubTask(new SubTask("Подзадача 4", "Описание подзадачи 4", NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2007, 1, 1, 0, 0, 0, 0), 4));

        assertEquals(epicId4.getStatus(), NEW, "Статус эпика должен быть NEW");
    }

    // проверка статусов эпика - все подзадачи со статусом DONE
    @Test
    public void checkingTheEpicStatusesOfAllSubtasksWithTheDONEStatus() {
        subTaskId6.setStatus(DONE);
        taskManager.addSubTask(new SubTask("Подзадача 3", "Описание подзадачи 3", DONE, Duration.ofMinutes(15),
                LocalDateTime.of(2006, 1, 1, 0, 0, 0, 0), 4));
        taskManager.addSubTask(new SubTask("Подзадача 4", "Описание подзадачи 4", DONE, Duration.ofMinutes(15),
                LocalDateTime.of(2007, 1, 1, 0, 0, 0, 0), 4));

        assertEquals(epicId4.getStatus(), DONE, "Статус эпика должен быть DONE");
    }

    // проверка статусов эпика - подзадачи со статусом NEW и DONE
    @Test
    public void checkingTheStatusOfEpicSubtasksWithTheStatusNEWAndDONE() {
        taskManager.addSubTask(new SubTask("Подзадача 3", "Описание подзадачи 3", DONE, Duration.ofMinutes(15),
                LocalDateTime.of(2006, 1, 1, 0, 0, 0, 0), 4));
        taskManager.addSubTask(new SubTask("Подзадача 4", "Описание подзадачи 4", DONE, Duration.ofMinutes(15),
                LocalDateTime.of(2007, 1, 1, 0, 0, 0, 0), 4));

        assertEquals(epicId4.getStatus(), IN_PROGRESS, "Статус эпика должен быть NEW");
    }

    // проверка статусов эпика - подзадачи со статусом IN_PROGRESS
    @Test
    public void checkingTheStatusOfEpicSubtasksWithTheStatusIN_PROGRESS() {
        subTaskId6.setStatus(IN_PROGRESS);
        taskManager.addSubTask(new SubTask("Подзадача 3", "Описание подзадачи 3", IN_PROGRESS, Duration.ofMinutes(15),
                LocalDateTime.of(2006, 1, 1, 0, 0, 0, 0), 4));
        taskManager.addSubTask(new SubTask("Подзадача 4", "Описание подзадачи 4", IN_PROGRESS, Duration.ofMinutes(15),
                LocalDateTime.of(2007, 1, 1, 0, 0, 0, 0), 4));

        assertEquals(epicId4.getStatus(), IN_PROGRESS, "Статус эпика должен быть NEW");
    }

    // проверка подзадач на наличие эпика
    @Test
    public void checkingSubtasksForThePresenceOfAnEpic() {
        final Integer epicId3 = subTaskId5.getEpicId();
        final Integer epicId4 = subTaskId6.getEpicId();

        assertNotNull(epicId3, "У эпика должен быть эпик ID 3");
        assertNotNull(epicId4, "У эпика должен быть эпик ID 4");
    }

    // проверка корректности расчёта пересечения интервалов
    @Test
    public void checkingTheCorrectnessOfTheCalculationOfTheIntersectionOfIntervals() {
        Task task = new Task("Задача 3", "Описание задачи 3", NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2000, 1, 1, 0, 30, 0, 0));
        taskManager.addTask(task);

        assertNotNull(taskManager.getTask(task.getId()), "Задача 3 не добавлена!");

        taskManager.deleteTask(task.getId());
        taskId2.setDuration(Duration.ofMinutes(16));

        Exception exception = assertThrows(RuntimeException.class, () -> taskManager.addTask(task));

        String expectedMessage = "Задача не добавлена, на это время назначена задача!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        task.setStartTime(LocalDateTime.of(2000, 1, 1, 0, 31, 0, 0));
        taskManager.addTask(task);

        assertNotNull(taskManager.getTask(task.getId()), "Задача 3 не добавлена!");
    }

    @Test
    public void checkingAnEmptyTaskHistory() { // проверка пустой истории задач
        assertNull(historyManager.getHistory(), " Список истории должен быть пустым");
    }

    @Test
    public void checkingTheHistoryOfDuplicateTasks() { //проверка истории задач на дублирование
        historyManager.add(taskId1);
        historyManager.add(taskId1);
        assertEquals(historyManager.getHistory().size(), 1, "В истории должна быть одна задача!");
    }

    @Test
    public void checkingForDeletionFromTheTaskBrowsingHistory() { // проверка удаления из истории просмотра задач
        historyManager.add(epicId3);
        historyManager.add(subTaskId5);
        historyManager.add(epicId4);
        historyManager.add(taskId1);
        historyManager.add(taskId2);
        historyManager.add(subTaskId6);

        historyManager.remove(3);
        historyManager.remove(1);
        historyManager.remove(6);

        assertTrue(historyManager.getHistory().stream().noneMatch(task -> task.getId() == taskId1.getId() ||
                task.getId() == epicId3.getId() || task.getId() == subTaskId6.getId()));
    }
}
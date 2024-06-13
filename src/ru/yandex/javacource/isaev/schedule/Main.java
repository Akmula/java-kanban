package ru.yandex.javacource.isaev.schedule;

import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.manager.InMemoryHistoryManager;
import ru.yandex.javacource.isaev.schedule.manager.Managers;
import ru.yandex.javacource.isaev.schedule.task.Epic;
import ru.yandex.javacource.isaev.schedule.task.Status;
import ru.yandex.javacource.isaev.schedule.task.SubTask;
import ru.yandex.javacource.isaev.schedule.task.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // добавление задач
        System.out.println();
        System.out.println("Добавление задач!");
        Task task1 = new Task("Задача номер 1", "Создать задачу 1", Status.NEW);
        System.out.print("Добавлена задача = " + taskManager.addTask(task1));
        Task task2 = new Task("Задача номер 2", "Создать задачу 2", Status.NEW);
        System.out.println("Добавлена задача = " + taskManager.addTask(task2));
        taskManager.getTask(task1.getId()); // добавляем в историю просмотра

        // добавление эпиков
        System.out.println("Добавление эпиков!");
        Epic epic1 = new Epic("Эпик 1", "Создать эпик 1 с одной подзадачей", Status.NEW);
        System.out.print("Добавлен эпик = " + taskManager.addEpic(epic1));
        Epic epic2 = new Epic("Эпик 2", "Создать эпик 2 с двумя подзадачами", Status.NEW);
        System.out.println("Добавлен эпик = " + taskManager.addEpic(epic2));
        taskManager.getEpic(epic1.getId()); // добавляем в историю просмотра

        // добавление подзадач
        System.out.println("Добавление подзадач!");
        SubTask subTask1 = new SubTask(epic1.getId(), "Подзадача 1", "Создать подзадачу 1", Status.NEW);
        taskManager.addSubTask(subTask1);
        System.out.print("Добавлена подзадача = " + subTask1);
        SubTask subTask2 = new SubTask(epic2.getId(), "Подзадача 2", "Создать подзадачу 2", Status.NEW);
        taskManager.addSubTask(subTask2);
        System.out.print("Добавлена подзадача = " + subTask2);
        SubTask subTask3 = new SubTask(epic2.getId(), "Подзадача 3", "Создать подзадачу 3", Status.NEW);
        taskManager.addSubTask(subTask3);
        System.out.println("Добавлена подзадача = " + subTask3);
        taskManager.getSubTask(subTask2.getId()); // добавляем в историю просмотра

        // просмотр всех задач
        System.out.println("Текущие задачи: \n" + taskManager.getTaskList());
        System.out.println("Текущие эпики: \n" + taskManager.getEpicList());
        System.out.println("Текущие подзадачи: \n" + taskManager.getSubTaskList());

        //изменение задач
        System.out.println("Изменение задач!");
        Task task1update = new Task(task1.getId(), task1.getTitle(),
                task1.setDescription("Изменение описания задачи"), task1.getStatus());
        taskManager.updateTask(task1update);
        System.out.println("Задача изменена = " + task1update);
        taskManager.getTask(task1update.getId()); // добавляем в историю просмотра

        // изменение эпиков
        System.out.println("Изменение эпиков!");
        epic1.setDescription("Изменение описания эпика!");
        System.out.println("Эпик изменен = " + epic1);
        taskManager.getEpic(epic1.getId()); // добавляем в историю просмотра

        // изменение подзадач
        System.out.println("Изменение подзадач!");
        subTask2.setDescription("Изменение описания подзадачи!");
        System.out.println("Подзадача изменена = " + subTask2);
        taskManager.getSubTask(subTask2.getId()); // добавляем в историю просмотра

        // изменение статусов
        System.out.println("Изменение статусов!");
        Task task1Update = new Task(task1.getId(), task1.getTitle(), task1.getDescription(), Status.DONE);
        Epic epic1Update = new Epic(epic1.getId(), epic1.getSubTaskId(),
                epic1.getTitle(), epic1.getDescription(), Status.DONE);
        SubTask subTask2Update = new SubTask(subTask2.getId(), subTask2.getEpicId(),
                subTask2.getTitle(), subTask2.getDescription(), Status.DONE);
        taskManager.updateTask(task1Update);
        System.out.println("Статус задачи изменен на DONE: " + task1Update);
        taskManager.updateEpic(epic1Update);
        System.out.println("Статус эпика изменен на DONE: " + epic1Update +
                "! Статус должен быть - NEW.");
        taskManager.updateSubTask(subTask2Update);
        System.out.println("Статус подзадачи изменен на DONE: " + subTask2Update);
        taskManager.getEpic(epic1Update.getId()); // добавляем в историю просмотра

        // удаление задач
        System.out.println("Удаление задач!");
        taskManager.deleteTask(task1.getId());
        System.out.println("Задача удалена = " + task1);

        // удаление эпиков
        System.out.println("Удаление эпиков!");
        taskManager.deleteEpic(epic1.getId());
        System.out.println("Эпик удален = " + epic1);

        // удаление подзадач
        System.out.println("Удаление подзадач!");
        taskManager.deleteSubTask(subTask3.getId());
        System.out.println("Подзадача удалена = " + subTask3);

        // просмотр всех задач
        System.out.println("Текущие задачи: \n" + taskManager.getTaskList());
        System.out.println("Текущие эпики: \n" + taskManager.getEpicList());
        System.out.println("Текущие подзадачи: \n" + taskManager.getSubTaskList());

        // просмотр истории
        System.out.println("Просмотренные задачи: \n" + taskManager.getHistory());
    }
}
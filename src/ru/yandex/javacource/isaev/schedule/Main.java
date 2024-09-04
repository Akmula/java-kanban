package ru.yandex.javacource.isaev.schedule;

import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
import ru.yandex.javacource.isaev.schedule.managers.Managers;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // добавление задач
        System.out.println();
        System.out.println("Добавление задач!");
        Task task1 = new Task("Задача номер 1", "Создать задачу 1", Status.NEW, Duration.ofMinutes(25),
                LocalDateTime.of(2001, 1, 1, 0, 0, 0, 0));
        System.out.print("Добавлена задача = " + taskManager.addTask(task1));
        Task task2 = new Task("Задача номер 2", "Создать задачу 2", Status.NEW, Duration.ofMinutes(25),
                LocalDateTime.of(2002, 1, 1, 0, 0, 0, 0));
        System.out.println("Добавлена задача = " + taskManager.addTask(task2));

        // добавление эпиков
        System.out.println("Добавление эпиков!");
        Epic epic1 = new Epic("Эпик 1", "Создать эпик 1 с одной подзадачей", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2003, 1, 1, 0, 0, 0, 0));
        System.out.print("Добавлен эпик = " + taskManager.addEpic(epic1));
        Epic epic2 = new Epic("Эпик 2", "Создать эпик 2 с двумя подзадачами", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2004, 1, 1, 0, 0, 0, 0));
        System.out.println("Добавлен эпик = " + taskManager.addEpic(epic2));

        // добавление подзадач
        System.out.println("Добавление подзадач!");
        SubTask subTask1 = new SubTask("Подзадача 1", "Создать подзадачу 1", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2005, 1, 1, 0, 0, 0, 0), epic1.getId());
        taskManager.addSubTask(subTask1);
        System.out.print("Добавлена подзадача = " + subTask1);
        SubTask subTask2 = new SubTask("Подзадача 2", "Создать подзадачу 2", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2006, 1, 1, 0, 0, 0, 0), epic2.getId());
        taskManager.addSubTask(subTask2);
        System.out.print("Добавлена подзадача = " + subTask2);
        SubTask subTask3 = new SubTask("Подзадача 3", "Создать подзадачу 3", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2007, 1, 1, 0, 0, 0, 0), epic2.getId());
        taskManager.addSubTask(subTask3);
        System.out.println("Добавлена подзадача = " + subTask3);

        // просмотр всех задач
        System.out.println("Текущие задачи: \n" + taskManager.getTaskList());
        System.out.println("Текущие эпики: \n" + taskManager.getEpicList());
        System.out.println("Текущие подзадачи: \n" + taskManager.getSubTaskList());

        //изменение задач
        System.out.println("Изменение задач!");
        task1.setDescription("Изменение описания задачи");
        System.out.println("Задача изменена = " + task1);

        // изменение эпиков
        System.out.println("Изменение эпиков!");
        epic1.setDescription("Изменение описания эпика!");
        System.out.println("Эпик изменен = " + epic1);

        // изменение подзадач
        System.out.println("Изменение подзадач!");
        subTask2.setDescription("Изменение описания подзадачи!");
        System.out.println("Подзадача изменена = " + subTask2);

        // изменение статусов
        System.out.println("Изменение статусов!");
        task1.setStatus(Status.DONE);
        System.out.println("Статус задачи изменен на DONE: " + task1);
        epic1.setStatus(Status.DONE);
        taskManager.updateEpic(epic1);
        System.out.println("Статус эпика изменен на DONE: " + epic1 + "! Статус должен быть - NEW.");
        subTask1.setStatus(Status.DONE);
        System.out.println("Статус подзадачи изменен на DONE: " + subTask1);

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
        taskManager.getEpic(4); // перезаписывается
        taskManager.getTask(2); // перезаписывается
        taskManager.getSubTask(6); // перезаписывается
        taskManager.getEpic(4); // перезаписывается
        taskManager.getSubTask(6); // добавляем в историю просмотра
        taskManager.getEpic(4); // перезаписывается
        taskManager.getTask(2); // перезаписывается
        taskManager.getEpic(4); // добавляем в историю просмотра
        taskManager.getTask(2); // добавляем в историю просмотра
        System.out.println("Просмотренные задачи: \n" + taskManager.getHistory());

        //дополнительное задание
        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();

        Task id8 = new Task("Задача номер 1", "дополнительное задание", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2008, 1, 1, 0, 0, 0, 0));
        Task id9 = new Task("Задача номер 2", "дополнительное задание", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2009, 1, 1, 0, 0, 0, 0));
        Epic id10 = new Epic("Эпик 1", "дополнительное задание", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2010, 1, 1, 0, 0, 0, 0));
        Epic id11 = new Epic("Эпик 2", "дополнительное задание", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2011, 1, 1, 0, 0, 0, 0));
        SubTask id12 = new SubTask("Подзадача 1", "дополнительное задание", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2012, 1, 1, 0, 0, 0, 0), 10);
        SubTask id13 = new SubTask("Подзадача 2", "дополнительное задание", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2013, 1, 1, 0, 0, 0, 0), 10);
        SubTask id14 = new SubTask("Подзадача 3", "дополнительное задание", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2014, 1, 1, 0, 0, 0, 0), 10);
        taskManager.addTask(id8);
        taskManager.addTask(id9);
        taskManager.addEpic(id10);
        taskManager.addEpic(id11);
        taskManager.addSubTask(id12);
        taskManager.addSubTask(id13);
        taskManager.addSubTask(id14);
        taskManager.getTask(8);
        taskManager.getTask(9);
        taskManager.getEpic(10);
        taskManager.getEpic(11);
        taskManager.getSubTask(12);
        taskManager.getSubTask(13);
        taskManager.getSubTask(14);

        // первоначальная история
        System.out.println("Изначальная история: \n" + taskManager.getHistory());
        taskManager.getSubTask(13);
        System.out.println("Подзадача 2 выведется последней.");
        System.out.println(taskManager.getHistory());
        taskManager.getSubTask(12);
        System.out.println("Подзадача 1 выведется последней.");
        System.out.println(taskManager.getHistory());
        taskManager.getEpic(11);
        System.out.println("Эпик 2 выведется последним.");
        System.out.println(taskManager.getHistory());
        taskManager.getTask(8);
        System.out.println("Задача 1 выведется последней.");
        System.out.println(taskManager.getHistory());
        taskManager.getSubTask(14);
        System.out.println("Подзадача 3 выведется последней.");
        System.out.println(taskManager.getHistory());
        taskManager.deleteTask(9);
        System.out.println("Задача 2 удалена.");
        System.out.println(taskManager.getHistory());
        taskManager.deleteEpic(10);
        System.out.println("Эпик 1 удален. Его подзадачи тоже.");

        System.out.println("History");
        System.out.println(taskManager.getHistory());

        System.out.println("PrioritizedTasks");
        System.out.println(taskManager.getPrioritizedTasks());
    }
}
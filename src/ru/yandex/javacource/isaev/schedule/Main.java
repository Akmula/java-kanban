package ru.yandex.javacource.isaev.schedule;

import ru.yandex.javacource.isaev.schedule.interfaces.TaskManager;
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

        // добавление эпиков
        System.out.println("Добавление эпиков!");
        Epic epic1 = new Epic("Эпик 1", "Создать эпик 1 с одной подзадачей", Status.NEW);
        System.out.print("Добавлен эпик = " + taskManager.addEpic(epic1));
        Epic epic2 = new Epic("Эпик 2", "Создать эпик 2 с двумя подзадачами", Status.NEW);
        System.out.println("Добавлен эпик = " + taskManager.addEpic(epic2));

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

        Task id8 = new Task("Задача номер 1", "дополнительное задание", Status.NEW);
        Task id9 = new Task("Задача номер 2", "дополнительное задание", Status.NEW);
        Epic id10 = new Epic("Эпик 1", "дополнительное задание", Status.NEW);
        Epic id11 = new Epic("Эпик 2", "дополнительное задание", Status.NEW);
        SubTask id12 = new SubTask(10, "Подзадача 1", "дополнительное задание", Status.NEW);
        SubTask id13 = new SubTask(10, "Подзадача 2", "дополнительное задание", Status.NEW);
        SubTask id14 = new SubTask(10, "Подзадача 3", "дополнительное задание", Status.NEW);
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
        System.out.println(taskManager.getHistory());
    }
}
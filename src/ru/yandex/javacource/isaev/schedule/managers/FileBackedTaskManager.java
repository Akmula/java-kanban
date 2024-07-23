package ru.yandex.javacource.isaev.schedule.managers;

import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.exceptions.ManagerFileException;
import ru.yandex.javacource.isaev.schedule.formatters.CSVFormatter;
import ru.yandex.javacource.isaev.schedule.tasks.Epic;
import ru.yandex.javacource.isaev.schedule.tasks.SubTask;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static File file;
    private static Path path;

    public FileBackedTaskManager(File file) {
        FileBackedTaskManager.file = file;
        path = Paths.get(String.valueOf(file));
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public FileBackedTaskManager loadFromFile() {
        int maxId = 0;
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                Task task = CSVFormatter.fromString(lines.get(i));
                if (maxId < task.getId()) {
                    maxId = task.getId();
                }
                if (task instanceof Epic) {
                    fileBackedTaskManager.addEpic((Epic) task);
                } else if (task instanceof SubTask) {
                    fileBackedTaskManager.addSubTask((SubTask) task);
                } else {
                    fileBackedTaskManager.addTask(task);
                }
            }
        } catch (IOException e) {
            throw ManagerFileException.loadExceptions(e);
        }
        InMemoryTaskManager.setGeneratorId(maxId);
        return fileBackedTaskManager;
    }

    private void save() { // сохранение задачи
        List<Task> tasks = getTaskList();
        tasks.addAll(getEpicList());
        tasks.addAll(getSubTaskList());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(String.valueOf(path), StandardCharsets.UTF_8))) {

            bw.write(CSVFormatter.getHeader());
            bw.newLine();

            for (Task task : tasks) {
                bw.write(CSVFormatter.toString(task));
                bw.newLine();
            }
        } catch (IOException e) {
            throw ManagerFileException.saveExceptions(e);
        }
    }

    @Override
    public Task addTask(Task task) {
        Task createdTask = super.addTask(task);
        save();
        return createdTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic createdEpic = super.addEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        SubTask createdSubTask = super.addSubTask(subTask);
        save();
        return createdSubTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTask() { // удаление всех задач
        super.deleteAllTask();
        save();
    }

    @Override
    public void updateEpic(Epic epic) { // обновление эпика
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) { // удаление эпика
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpic() { // удаление всех эпиков.
        super.deleteAllEpic();
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) { // обновление подзадачи
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteSubTask(int id) { // удаление подзадачи
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteSubtasks() { // удаление всех подзадач
        super.deleteSubtasks();
        save();
    }

    public static void main(String[] args) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new File("data.csv"));

        fileBackedTaskManager.addTask(new Task("Задача 1", "Тестовая задача 1", Status.NEW));
        fileBackedTaskManager.addEpic(new Epic("Эпик 1", "Тестовый эпик 1", Status.NEW));
        fileBackedTaskManager.addSubTask(new SubTask("Подзадача 1", "Тестовая подзадача 1", Status.NEW, 2));
        fileBackedTaskManager.addSubTask(new SubTask("Подзадача 2", "Тестовая подзадача 2", Status.NEW, 3));

        setGeneratorId(0);
        FileBackedTaskManager fileBackedTaskManager1 = fileBackedTaskManager.loadFromFile();

        System.out.println("Задача из менеджера - " + fileBackedTaskManager.getTaskList());
        System.out.println("Задача из файла - " + fileBackedTaskManager1.getTaskList());
        System.out.println("Задача из менеджера - " + fileBackedTaskManager.getEpicList());
        System.out.println("Задача из файла - " + fileBackedTaskManager1.getEpicList());
        System.out.println("Задача из менеджера - " + fileBackedTaskManager.getSubTaskList());
        System.out.println("Задача из файла - " + fileBackedTaskManager1.getSubTaskList());
    }
}
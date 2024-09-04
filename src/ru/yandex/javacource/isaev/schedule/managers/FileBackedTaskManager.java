package ru.yandex.javacource.isaev.schedule.managers;

import ru.yandex.javacource.isaev.schedule.enums.Status;
import ru.yandex.javacource.isaev.schedule.exceptions.ManagerLoadException;
import ru.yandex.javacource.isaev.schedule.exceptions.ManagerSaveException;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String PATH_TO_FILE = "src/ru/yandex/javacource/isaev/schedule/resources/data.csv";
    private final File file;

    FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        int maxId = 0;
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                Task task = CSVFormatter.fromString(lines.get(i));
                if (maxId < task.getId()) {
                    maxId = task.getId();
                }
                fileBackedTaskManager.addAnyTask(task);
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка чтения файла!", e);
        }
        fileBackedTaskManager.setGeneratorId(maxId);
        return fileBackedTaskManager;
    }

    private void save() { // сохранение задачи
        List<Task> tasks = getTaskList();
        if (getEpicList() != null) {
            tasks.addAll(getEpicList());
        }
        if (getSubTaskList() != null) {
            tasks.addAll(getSubTaskList());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(String.valueOf(file), StandardCharsets.UTF_8))) {
            bw.write(CSVFormatter.getHeader());
            bw.newLine();
            for (Task task : tasks) {
                bw.write(CSVFormatter.toString(task));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла", e);
        }
    }

    protected void addAnyTask(Task task) {
        final int id = task.getId();
        switch (task.getTaskType()) {
            case TASK:
                tasks.put(id, task);
                prioritizedTasks.add(task);
                break;
            case SUBTASK:
                subTasks.put(id, (SubTask) task);
                break;
            case EPIC:
                epics.put(id, (Epic) task);
                prioritizedTasks.add(task);
                break;
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
    public void deleteAllSubtask() { // удаление всех подзадач
        super.deleteAllSubtask();
        save();
    }

    public static void main(String[] args) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new File(PATH_TO_FILE));

        fileBackedTaskManager.addTask(new Task("Задача 1", "Тестовая задача 1", Status.NEW, Duration.ofMinutes(25),
                LocalDateTime.of(2025, 1, 1, 1, 0, 0, 0)));
        fileBackedTaskManager.addEpic(new Epic("Эпик 1", "Тестовый эпик 1", Status.NEW, Duration.ofMinutes(25),
                LocalDateTime.of(2025, 5, 2, 2, 0, 0, 0)));
        fileBackedTaskManager.addSubTask(new SubTask("Подзадача 1", "Тестовая подзадача 1", Status.NEW, Duration.ofMinutes(25),
                LocalDateTime.of(2025, 3, 3, 3, 0, 0, 0), 2));
        fileBackedTaskManager.addSubTask(new SubTask("Подзадача 2", "Тестовая подзадача 2", Status.NEW, Duration.ofMinutes(25),
                LocalDateTime.of(2025, 4, 4, 4, 0, 0, 0), 2));
        fileBackedTaskManager.addTask(new Task("Задача 2", "Тестовая задача 2", Status.NEW, Duration.ofMinutes(25),
                LocalDateTime.of(2025, 5, 1, 1, 0, 0, 0)));

        FileBackedTaskManager fileBackedTaskManagerFromFile = loadFromFile(new File(PATH_TO_FILE));

        System.out.println("Задача из менеджера - " + fileBackedTaskManager.getTaskList());
        System.out.println("Задача из файла - " + fileBackedTaskManagerFromFile.getTaskList());
        System.out.println("Эпик из менеджера - " + fileBackedTaskManager.getEpicList());
        System.out.println("Эпик из файла - " + fileBackedTaskManagerFromFile.getEpicList());
        System.out.println("Подзадача из менеджера - " + fileBackedTaskManager.getSubTaskList());
        System.out.println("Подзадача из файла - " + fileBackedTaskManagerFromFile.getSubTaskList());

        System.out.println("Sorted tasks");
        System.out.println(fileBackedTaskManager.getPrioritizedTasks());
    }
}
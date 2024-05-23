public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // добавление задач
        System.out.println();
        System.out.println("Добавление задач!");
        Task task1 = new Task(taskManager.setId(), "Задача номер 1", "Создать задачу 1", Status.NEW);
        System.out.print("Добавлена задача = " + taskManager.addTask(task1));
        Task task2 = new Task(taskManager.setId(), "Задача номер 2", "Создать задачу 2", Status.NEW);
        System.out.println("Добавлена задача = " + taskManager.addTask(task2));

        // добавление эпиков
        System.out.println("Добавление эпиков!");
        Epic epic1 = new Epic(taskManager.setId(), "Эпик 1", "Создать эпик 1 с одной подзадачей", Status.NEW);
        System.out.print("Добавлен эпик = " + taskManager.addEpic(epic1));
        Epic epic2 = new Epic(taskManager.setId(), "Эпик 2", "Создать эпик 2 с двумя подзадачами", Status.NEW);
        System.out.println("Добавлен эпик = " + taskManager.addEpic(epic2));

        // добавление подзадач
        System.out.println("Добавление подзадач!");
        SubTask subTask1 = new SubTask(taskManager.setId(), epic1.getEpicId(), "Подзадача эпика 1", "Создать подзадачу 1", Status.NEW);
        System.out.print("Добавлена подзадача = " + taskManager.addSubTask(subTask1));
        SubTask subTask2 = new SubTask(taskManager.setId(), epic2.getEpicId(), "Подзадача эпика 2", "Создать подзадачу 2", Status.NEW);
        System.out.print("Добавлена подзадача = " + taskManager.addSubTask(subTask2));
        SubTask subTask3 = new SubTask(taskManager.setId(), epic2.getEpicId(), "Подзадача эпика 2", "Создать подзадачу 3", Status.NEW);
        System.out.println("Добавлена подзадача = " + taskManager.addSubTask(subTask3));

        // просмотр всех задач
        System.out.println("Текущие задачи: \n" + taskManager.getTaskList());
        System.out.println("Текущие эпики: \n" + taskManager.getEpicList());
        System.out.println("Текущие подзадачи: \n" + taskManager.getSubTaskList());

        //изменение задач
        System.out.println("Изменение задач!");
        Task task1update = new Task(task1.getId(), task1.getTitle(), task1.setDescription("Изменение задач!"), task1.getStatus());
        System.out.println("Задача изменена = " + taskManager.updateTask(task1update));

        // изменение эпиков
        System.out.println("Изменение эпиков!");
        epic1.setDescription("Изменение эпиков!");
        System.out.println("Эпик изменен = " + epic1);

        // изменение подзадач
        System.out.println("Изменение подзадач!");
        subTask2.setDescription("Изменение подзадач!");
        System.out.println("Подзадача изменена = " + subTask2);

        // изменение статусов
        System.out.println("Изменение статусов!");
        Task task1Update = new Task(task1.getId(), task1.getTitle(), task1.getDescription(), Status.DONE);
        Epic epic1Update = new Epic(epic1.getId(), epic1.getSubTaskId(), epic1.getTitle(), epic1.getDescription(), Status.DONE);
        SubTask subTask2Update = new SubTask(subTask2.getId(), subTask2.getEpicId(), subTask2.getTitle(), subTask2.getDescription(), Status.DONE);
        System.out.println("Статус задачи изменен на: " + taskManager.updateTask(task1Update));
        System.out.println("Статус эпика изменен на: " + taskManager.updateEpic(epic1Update) + "! Статус должен быть - NEW.");
        System.out.println("Статус подзадачи изменен на: " + taskManager.updateSubTask(subTask2Update));

        // удаление задач
        System.out.println("Удаление задач!");
        System.out.println("Задача удалена = " + taskManager.deleteTask(task1.getId()));

        // удаление эпиков
        System.out.println("Удаление эпиков!");
        System.out.println("Эпик удален = " + taskManager.deleteEpic(epic1.getId()));

        // удаление подзадач
        System.out.println("Удаление подзадач!");
        System.out.println("Подзадача удалена = " + taskManager.deleteSubTask(subTask3.getId()));

        // просмотр всех задач
        System.out.println("Текущие задачи: \n" + taskManager.getTaskList());
        System.out.println("Текущие эпики: \n" + taskManager.getEpicList());
        System.out.println("Текущие подзадачи: \n" + taskManager.getSubTaskList());
    }
}

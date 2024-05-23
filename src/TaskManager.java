import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private static int taskId = 0; // идентификатор
    private final Map<Integer, Task> tasks = new HashMap<>(); // карта с задачами
    private final Map<Integer, Epic> epics = new HashMap<>(); // карта с эпиками
    private final Map<Integer, SubTask> subTasks = new HashMap<>(); // карта с эпиками

    public Integer setId() { // генератор идентификтора
        taskId++;
        return taskId;
    }

    // методы для задач

    public List<Task> getTaskList() { // получение списка всех задач.
        return new ArrayList<>(tasks.values());
    }

    public Task addTask(Task task) { //добавление задачи
        tasks.put(task.getId(), task);
        return task;
    }

    public Task updateTask(Task task) { // обновление задачи
        tasks.put(task.getId(), task);
        return task;
    }

    public Task deleteTask(int id) { // удаление задачи
        Task task = tasks.get(id);
        tasks.remove(id);
        return task;
    }

    public void deleteAllTask() { // удаление всех задач
        tasks.clear();
    }

    public Task getTask(int id) { // получение задачи по идентификатору
        return tasks.get(id);
    }

    // методы для эпиков

    public List<Epic> getEpicList() { // получение списка эпиков
        return new ArrayList<>(epics.values());
    }

    public Epic addEpic(Epic epic) { //добавление эпика
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) { // обновление эпика
        checkStatus(epic);
        epics.put(epic.getEpicId(), epic);
        return epic;
    }

    public Epic deleteEpic(int id) { // удаление эпика
        Epic epic = epics.get(id);
        ArrayList<Integer> subTaskIds = epic.getSubTaskId();
        for (Integer subTaskId : subTaskIds) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
        return epic;
    }

    public void checkStatus(Epic epic) {  // проверка статуса эпика
        ArrayList<Status> status = new ArrayList<>();
        for (int subTaskId : epic.getSubTaskId()) {
            status.add(subTasks.get(subTaskId).getStatus());
        }
        if (status.contains(Status.NEW) && status.contains(Status.DONE) || status.contains(Status.IN_PROGRESS)) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (status.contains(Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.DONE);
        }
    }

    public ArrayList<Integer> getSubTaskIdsForEpic(int id) { // получение списка всех подзадач определённого эпика
        ArrayList<Integer> subTaskIds = new ArrayList<>();
        for (SubTask subtask : subTasks.values()) {
            if (subtask.getEpicId() == id) {
                subTaskIds.add(subtask.getId());
            }
        }
        return subTaskIds;
    }

    public void deleteAllEpic() { // удаление всех эпиков.
        epics.clear();
        subTasks.clear(); //так как подзадачи не могут существовать без эпиков, их тоже удаляем
    }

    public Epic getEpic(int id) { // получение эпика по идентификатору.
        return epics.get(id);
    }

    // методы для подзадач

    public List<SubTask> getSubTaskList() { // получение списка подзадач
        return new ArrayList<>(subTasks.values());
    }

    public SubTask addSubTask(SubTask subTask) { //добавление подзадачи
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.setSubTaskId(getSubTaskIdsForEpic(subTask.getEpicId()));
        updateEpic(epic);
        return subTask;
    }

    public SubTask updateSubTask(SubTask subTask) { // обновление подзадачи
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.setSubTaskId(getSubTaskIdsForEpic(subTask.getEpicId()));
        updateEpic(epic);
        return subTask;
    }

    public SubTask deleteSubTask(int id) { // удаление подзадачи
        SubTask subTask = subTasks.get(id);
        subTasks.remove(id);
        Epic epic = getEpic(subTask.getEpicId());
        epic.setSubTaskId(getSubTaskIdsForEpic(subTask.getEpicId()));
        updateEpic(epic);
        return subTask;
    }

    public void deleteAllSubTask() { // удаление всех подзадач
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() != 0) {
                Epic epic = getEpic(subTask.getEpicId());
                epic.setSubTaskId(getSubTaskIdsForEpic(subTask.getEpicId()));
                updateEpic(epic);
            }
        }
        subTasks.clear();
    }

    public SubTask getSubTask(int id) { //получение подзадачи по id
        return subTasks.get(id);
    }
}
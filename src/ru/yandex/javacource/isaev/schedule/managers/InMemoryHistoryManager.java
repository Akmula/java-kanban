package ru.yandex.javacource.isaev.schedule.managers;

import ru.yandex.javacource.isaev.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.isaev.schedule.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> history = new HashMap<>(); // список истории

    static class Node {
        Node prev;
        Node next;
        Task task;

        Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.next = next;
            this.task = task;
        }
    }

    @Override
    public void add(Task task) { // добавляем задачи в историю
        if (task == null) {
            return;
        }
        int id = task.getId();
        remove(id);
        linkLast(task);
        history.put(id, tail);
    }

    @Override
    public void remove(int id) { // удаляем задачу из истории
        Node node = history.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() { //получаем список из истории
        List<Task> history = new ArrayList<>();
        Node node = head;
        if (head == null) {
            return history;
        }
        history.add(node.task);
        while (node.next != null) {
            node = node.next;
            history.add(node.task);
        }
        return history;
    }

    private void removeNode(Node node) { // удаление ноды из двусвязного списка
        if (head == null || node == null) {
            return;
        }
        if (head == node) {
            head = node.next;
        }
        if (tail == node) {
            tail = node.prev;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }
    }

    private void linkLast(Task task) { // добавление ноды в конец списка
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }
}
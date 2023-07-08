package service;

import models.Task;
import models.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList historyOfViewedTasks = new CustomLinkedList();
    private final Map<Integer, Node> historyMap = new HashMap<>();


    @Override
    public void add(Task task) {
        if (task != null) {
            int id = task.getId();
            if (historyMap.containsKey(id)) {
                historyOfViewedTasks.removeNode(historyMap.get(id));
            }

            Node node = historyOfViewedTasks.linkLast(task);
            historyMap.put(id, node);
        }
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.get(id);
        historyOfViewedTasks.removeNode(node);
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyOfViewedTasks.getTasks();
    }

    private static class CustomLinkedList {
        private Node head;
        private Node tail;

        public List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            Node node = head;
                while (node != null) {
                    tasks.add(node.data);
                    node = node.next;
                }
            return tasks;
        }

        public Node linkLast(Task task) {
            Node oldTail = tail;
            Node newNode = new Node(oldTail, task, null);
            tail = newNode;

            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }

            return newNode;
        }

        public void removeNode(Node node) {
            if (node != null) {

                if (head == node) {
                    head = head.next;
                    if (head != null) {
                        head.prev = null;
                    }
                }
                if (tail == node) {
                    tail = tail.prev;
                    if (tail != null) {
                        tail.next = null;
                    }
                }

                if (node.prev != null) {
                    node.prev.next = node.next;
                }
                if (node.next != null) {
                    node.next.prev = node.prev;
                }
            }
        }
    }

}

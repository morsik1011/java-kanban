package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> idToNote;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        this.idToNote = new HashMap<>();
    }

    private static class Node {
        public Task task;
        public Node next;
        public Node previous;

        public Node(Node previous, Task task, Node next) {
            this.previous = previous;
            this.task = task;
            this.next = next;
        }
    }

    @Override
    public void remove(int id) {
        removeNode(idToNote.get(id));
    }

    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTask();
    }

    private void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        idToNote.put(task.getId(), newNode);
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
    }

    private List<Task> getTask() {
        List<Task> tasks = new ArrayList<>();
        Node node = first;
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {

        if (node != null) {
            if (node == first) {
                final Node next = node.next;
                node.task = null;
                node.next = null;
                first = next;
                if (next == null)
                    last = null;
                else
                    next.previous = null;
            } else if (node == last) {


                final Node previous = node.previous;
                node.task = null;
                node.previous = null;
                last = previous;
                if (previous == null)
                    first = null;
                else
                    previous.next = null;
            } else {

                final Node next = node.next;
                final Node prev = node.previous;

                if (prev == null) {
                    first = next;
                } else {
                    prev.next = next;
                    node.previous = null;
                }

                if (next == null) {
                    last = prev;
                } else {
                    next.previous = prev;
                    node.next = null;
                }

                node.task = null;
            }
        }

    }
}

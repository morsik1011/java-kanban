package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Task;
import tasks.Subtask;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest  {


    private HistoryManager historyManager;

    @BeforeEach
    public void init() {
        historyManager = Managers.getDefaultHistory();
    }


    @Test
    void removeFirst() {
        Task task1 = new Task(1,"Задача1", "Описание1", Status.NEW, Duration.ofMinutes(1), Instant.now());
        Task task2 = new Task(2,"Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(5)));
        Task task3 = new Task(3,"Задача3", "Описание3", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(10)));
        Task task4 = new Task(4,"Задача4", "Описание4", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(15)));
        Task task5 = new Task(5,"Задача5", "Описание5", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(20)));
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);
        historyManager.addToHistory(task4);
        historyManager.addToHistory(task5);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(4, history.size());
    }

    @Test
    void removeLast() {
        Task task1 = new Task(1,"Задача1", "Описание1", Status.NEW, Duration.ofMinutes(1), Instant.now());
        Task task2 = new Task(2,"Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(5)));
        Task task3 = new Task(3,"Задача3", "Описание3", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(10)));
        Task task4 = new Task(4,"Задача4", "Описание4", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(15)));
        Task task5 = new Task(5,"Задача5", "Описание5", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(20)));
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);
        historyManager.addToHistory(task4);
        historyManager.addToHistory(task5);

        historyManager.remove(task5.getId());

        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(4, history.size());
    }

    @Test
    void removeMiddle() {
        Task task1 = new Task(1,"Задача1", "Описание1", Status.NEW, Duration.ofMinutes(1), Instant.now());
        Task task2 = new Task(2,"Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(5)));
        Task task3 = new Task(3,"Задача3", "Описание3", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(10)));
        Task task4 = new Task(4,"Задача4", "Описание4", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(15)));
        Task task5 = new Task(5,"Задача5", "Описание5", Status.NEW, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(20)));
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);
        historyManager.addToHistory(task4);
        historyManager.addToHistory(task5);

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(4, history.size());
    }

    @Test
    void AddNullTasksInHistory() {
        Task task = null;
        Epic epic = null;
        Subtask subTask = null;

        historyManager.addToHistory(task);
        historyManager.addToHistory(epic);
        historyManager.addToHistory(subTask);

        Assertions.assertTrue(historyManager.getHistory().isEmpty());
    }


    @Test
    void addToHistory() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW, Duration.ofMinutes(1), Instant.now());

        historyManager.addToHistory(task1);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @Test
    void getHistory() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW,Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        historyManager.addToHistory(task1);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals("[Task{taskId=0, taskName='Задача1', taskDescription='Описание1', status=NEW, duration=1, startTime=2025-01-01T00:00:00Z, endTime=2025-01-01T00:01:00Z}]", history.toString());
    }
}


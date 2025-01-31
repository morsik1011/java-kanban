package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void init(){
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void remove() {
        Task task1 = new Task ("Задача1", "Описание1", Status.NEW);
        historyManager.addToHistory(task1);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(0, history.size());
    }

    @Test
    void addToHistory() {
        Task task1 = new Task ("Задача1", "Описание1", Status.NEW);

        historyManager.addToHistory(task1);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @Test
    void getHistory() {
        Task task1 = new Task ("Задача1", "Описание1", Status.NEW);
        historyManager.addToHistory(task1);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals("[Task{taskId=0, taskName='Задача1', taskDescription='Описание1', status=NEW}]", history.toString());
    }
}

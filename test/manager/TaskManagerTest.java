package manager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {
    protected TaskManager taskManager;

    abstract T getTaskManager();

    @BeforeEach
    public void init() {
        taskManager = Managers.getDefault();
    }

    @Test
    void checkingSubtaskForAvailabilityEpic() {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);

        int epicId = subtask1.getEpicId();

        Assertions.assertEquals(epicId, epic1.getId());
    }

    @Test
    void createEpicStatus() {
        Epic epic1 = new Epic("Эпик1", "Все подзадачи со статусом NEW");
        Epic epic2 = new Epic("Эпик2", "Все подзадачи со статусом DONE");
        Epic epic3 = new Epic("Эпик2", "Подзадачи со статусами NEW и DONE");
        Epic epic4 = new Epic("Эпик2", "Подзадачи со статусом IN_PROGRESS");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic4);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Подзадача эпика 1 со статусом NEW", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Подзадача эпика 1 со статусом NEW", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Подзадача эпика 2 со статусом DONE", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T02:00:00Z"));
        Subtask subtask4 = new Subtask(epic2, "Задача4", "Подзадача эпика 2 со статусом DONE", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T03:00:00Z"));
        Subtask subtask5 = new Subtask(epic3, "Задача5", "Подзадача эпика 3 со статусом NEW", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T04:00:00Z"));
        Subtask subtask6 = new Subtask(epic3, "Задача6", "Подзадача эпика 3 со статусом DONE", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T05:00:00Z"));
        Subtask subtask7 = new Subtask(epic4, "Задача7", "Подзадача эпика 4 со статусом IN_PROGRESS", Status.IN_PROGRESS, Duration.ofMinutes(1), Instant.parse("2025-01-01T06:00:00Z"));
        Subtask subtask8 = new Subtask(epic4, "Задача8", "Подзадача эпика 4 со статусом IN_PROGRESS", Status.IN_PROGRESS, Duration.ofMinutes(1), Instant.parse("2025-01-01T07:00:00Z"));
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);
        taskManager.createSubTask(subtask4);
        taskManager.createSubTask(subtask5);
        taskManager.createSubTask(subtask6);
        taskManager.createSubTask(subtask7);
        taskManager.createSubTask(subtask8);

        Status status1 = epic1.getStatus();
        Status status2 = epic2.getStatus();
        Status status3 = epic3.getStatus();
        Status status4 = epic4.getStatus();

        Assertions.assertEquals(status1, Status.NEW);
        Assertions.assertEquals(status2, Status.DONE);
        Assertions.assertEquals(status3, Status.IN_PROGRESS);
        Assertions.assertEquals(status4, Status.IN_PROGRESS);

    }

    @Test
    void getPrioritizedTasks() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW, Duration.ofMinutes(10), Instant.parse("2025-01-01T01:00:00Z"));
        Task task2 = new Task("Задача2", "Описание2", Status.DONE, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        Task firstTask = prioritizedTasks.stream().findFirst().orElseThrow();
        int firstTaskId = firstTask.getId();

        Assertions.assertEquals(task2.getId(), firstTaskId);
    }

    @Test
    void getHistory() {

        Task task1 = new Task("Задача1", "Описание1", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Epic epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createTask(task1);
        taskManager.createEpic(epic1);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpickById(epic1.getId());

        List<Task> history = taskManager.getHistory();
        assertNotNull(history);
        assertEquals(2, history.size());
    }

    @Test
    void createTask() {
        String name = "Задача";
        String decriptions = "Описание";
        Duration duration = Duration.ofMinutes(1);
        Task task = new Task("Задача", "Описание", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));

        taskManager.createTask(task);

        Task actualTask = taskManager.getTaskById(task.getId());
        Assertions.assertEquals(actualTask.getDescription(), decriptions);
        Assertions.assertEquals(actualTask.getName(), name);
        Assertions.assertEquals(actualTask.getStatus(), Status.NEW);
        Assertions.assertEquals(actualTask.getDuration(), duration);

    }

    @Test
    void updateTask() {
        String name = "Обновленная задача";
        String decriptions = "Обновленный статус";
        Task task = new Task("Задача", "Описание", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        taskManager.createTask(task);
        task.setName("Обновленная задача");
        task.setDescription("Обновленный статус");
        task.setStatus(Status.DONE);

        taskManager.updateTask(task);


        Assertions.assertEquals(task.getDescription(), decriptions);
        Assertions.assertEquals(task.getName(), name);
        Assertions.assertEquals(task.getStatus(), Status.DONE);
    }

    @Test
    void removeAllTask() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Task task2 = new Task("Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.removeAllTask();

        Assertions.assertTrue(taskManager.getTaskList().isEmpty());
    }

    @Test
    void removeTaskById() {
        Task task = new Task("Задача", "Описание", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Task task1 = new Task("Задача1", "Описание1", Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T10:00:00Z"));
        taskManager.createTask(task);
        taskManager.createTask(task1);

        taskManager.removeTaskById(task.getId());

        List<Task> taskList = taskManager.getTaskList();
        assertEquals(1, taskList.size());
    }

    @Test
    void getTaskList() {
        String name1 = "Задача1";
        String decription1 = "Описание1";
        Duration duration1 = Duration.ofMinutes(1);
        String name2 = "Задача2";
        String decription2 = "Описание2";
        Duration duration2 = Duration.ofMinutes(1);
        Task task1 = new Task("Задача1", "Описание1", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Task task2 = new Task("Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        ArrayList<Task> taskList = taskManager.getTaskList();

        Assertions.assertEquals(taskList.getFirst().getName(), name1);
        Assertions.assertEquals(taskList.getFirst().getDescription(), decription1);
        Assertions.assertEquals(taskList.getFirst().getStatus(), Status.NEW);
        Assertions.assertEquals(taskList.get(0).getDuration(), duration1);
        Assertions.assertEquals(taskList.get(1).getName(), name2);
        Assertions.assertEquals(taskList.get(1).getDescription(), decription2);
        Assertions.assertEquals(taskList.get(1).getStatus(), Status.NEW);
        Assertions.assertEquals(taskList.get(1).getDuration(), duration2);

    }

    @Test
    void getTaskById() {
        String name = "Задача2";
        String decription = "Описание2";
        Duration duration = Duration.ofMinutes(5);
        Task task1 = new Task("Задача1", "Описание1", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Task task2 = new Task("Задача2", "Описание2", Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T10:00:00Z"));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Task actualTask = taskManager.getTaskById(task2.getId());

        Assertions.assertEquals(actualTask.getName(), name);
        Assertions.assertEquals(actualTask.getDescription(), decription);
        Assertions.assertEquals(actualTask.getStatus(), Status.NEW);
        Assertions.assertEquals(actualTask.getDuration(), duration);
    }

    @Test
    void createEpic() {
        String name = "Эпик";
        String decription = "Описание";
        Epic epic = new Epic("Эпик", "Описание");

        taskManager.createEpic(epic);

        Epic actualEpic = taskManager.getEpickById(epic.getId());
        Assertions.assertEquals(actualEpic.getDescription(), decription);
        Assertions.assertEquals(actualEpic.getName(), name);
        Assertions.assertEquals(actualEpic.getStatus(), Status.NEW);
    }

    @Test
    void updateEpic() {
        String name = "Новый эпик";
        String decription = "Новое описание";
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);
        epic.setName("Новый эпик");
        epic.setDescription("Новое описание");

        taskManager.updateEpic(epic);

        Assertions.assertEquals(epic.getName(), name);
        Assertions.assertEquals(epic.getDescription(), decription);
    }

    @Test
    void removeAllEpic() {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Описание3", Status.IN_PROGRESS, Duration.ofMinutes(1), Instant.parse("2025-01-01T02:00:00Z"));
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        taskManager.removeAllEpic();

        Assertions.assertTrue(taskManager.getEpicList().isEmpty());
        Assertions.assertTrue(taskManager.getSubtaskList().isEmpty());
    }

    @Test
    void removeEpicById() {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic2);
        Subtask subtask21 = new Subtask(epic2, "Задача1", "Описание1", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T05:00:00Z"));
        Subtask subtask22 = new Subtask(epic2, "Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T07:00:00Z"));
        taskManager.createSubTask(subtask21);
        taskManager.createSubTask(subtask22);

        taskManager.removeEpicById(epic2.getId());

        List<Epic> epicList = taskManager.getEpicList();
        List<Subtask> subtaskList = taskManager.getSubtaskList();
        assertEquals(1, epicList.size());
        assertEquals(2, subtaskList.size());
    }


    @Test
    void getEpickById() {
        String name1 = "Эпик1";
        String decription1 = "Описание1";
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Описание3", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T02:00:00Z"));
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        Epic actualEpic = taskManager.getEpickById(epic1.getId());

        Assertions.assertEquals(actualEpic.getName(), name1);
        Assertions.assertEquals(actualEpic.getDescription(), decription1);
        Assertions.assertEquals(actualEpic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void createSubTask() {
        String name = "Задача1";
        String decription = "Описание1";
        Epic epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));

        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);

        Subtask actualSubtask = taskManager.getSubtaskById(subtask1.getId());
        Assertions.assertEquals(actualSubtask.getDescription(), decription);
        Assertions.assertEquals(actualSubtask.getName(), name);
        Assertions.assertEquals(actualSubtask.getStatus(), Status.DONE);
        Assertions.assertEquals(epic1.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void updateSubtask() {
        String name = "Новая задача1";
        String decription = "Новое описание1";
        Epic epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);

        subtask1.setStatus(Status.DONE);
        subtask1.setName("Новая задача1");
        subtask1.setDescription("Новое описание1");
        taskManager.updateSubtask(subtask1);

        Assertions.assertEquals(subtask1.getDescription(), decription);
        Assertions.assertEquals(subtask1.getName(), name);
        Assertions.assertEquals(subtask1.getStatus(), Status.DONE);
        Assertions.assertEquals(epic1.getStatus(), Status.DONE);
    }

    @Test
    void removeAllSubtask() {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Описание3", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T02:00:00Z"));
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        taskManager.removeAllSubtask();

        Assertions.assertTrue(taskManager.getSubtaskList().isEmpty());
    }

    @Test
    void removeSubtaskById() {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Описание3", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T02:00:00Z"));
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        taskManager.removeSubtaskById(subtask3.getId());

        ArrayList<Subtask> subtaskList = taskManager.getSubtaskList();
        Assertions.assertEquals(subtaskList.size(), 2);
    }


    @Test
    void getSubtaskById() {
        String name1 = "Задача2";
        String decription1 = "Описание2";
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T01:00:00Z"));
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Описание3", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T02:00:00Z"));
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        Subtask actualSubtask = taskManager.getSubtaskById(subtask2.getId());

        Assertions.assertEquals(actualSubtask.getName(), name1);
        Assertions.assertEquals(actualSubtask.getDescription(), decription1);
        Assertions.assertEquals(actualSubtask.getStatus(), Status.NEW);
    }


    @Test
    void getEpicList() {
        String name1 = "Эпик1";
        String decription1 = "Описание1";
        String name2 = "Эпик2";
        String decription2 = "Описание2";
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        ArrayList<Epic> epicList = taskManager.getEpicList();

        Assertions.assertEquals(epicList.getFirst().getName(), name1);
        Assertions.assertEquals(epicList.getFirst().getDescription(), decription1);
        Assertions.assertEquals(epicList.getFirst().getStatus(), Status.NEW);
        Assertions.assertEquals(epicList.get(1).getName(), name2);
        Assertions.assertEquals(epicList.get(1).getDescription(), decription2);
        Assertions.assertEquals(epicList.get(1).getStatus(), Status.NEW);

    }
}



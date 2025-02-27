package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File file = new File("test.csv");

    @Override
    FileBackedTaskManager getTaskManager() {
        return new FileBackedTaskManager(file);
    }

    @Test
    void loadFromFile() {

        TaskManager taskManager = getTaskManager();

        Task task1 = new Task("Задача1", "Описание1", Status.NEW, Duration.ofMinutes(1), Instant.parse("2025-01-01T00:00:00Z"));
        Task task2 = new Task("Задача2", "Описание2", Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T01:00:00Z"));
        Task task3 = new Task("Задача3", "Описание3", Status.NEW, Duration.ofMinutes(10), Instant.parse("2025-01-01T02:00:00Z"));
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask11 = new Subtask(epic1, "подзадача1", "Описание", Status.IN_PROGRESS, Duration.ofMinutes(5), Instant.parse("2025-01-01T03:00:00Z"));
        Subtask subtask21 = new Subtask(epic2, "подзадача2", "Описание", Status.DONE, Duration.ofMinutes(1), Instant.parse("2025-01-01T04:00:00Z"));
        Subtask subtask31 = new Subtask(epic1, "подзадача3", "Описание", Status.NEW, Duration.ofMinutes(4), Instant.parse("2025-01-01T05:00:00Z"));
        taskManager.createSubTask(subtask11);
        taskManager.createSubTask(subtask21);
        taskManager.createSubTask(subtask31);


        TaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(file);

        String expectedTasks = taskManager.getTaskList().toString();
        String actuallyTasks = taskManagerFromFile.getTaskList().toString();
        String expectedEpics = taskManager.getEpicList().toString();
        String actuallyEpics = taskManagerFromFile.getEpicList().toString();
        String expectedSubtasks = taskManager.getSubtaskList().toString();
        String actuallySubtasks = taskManagerFromFile.getSubtaskList().toString();
        Assertions.assertEquals(expectedTasks, actuallyTasks);
        Assertions.assertEquals(expectedEpics, actuallyEpics);
        Assertions.assertEquals(expectedSubtasks, actuallySubtasks);
    }

}

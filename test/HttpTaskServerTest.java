import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager, "localhost", 8080);
    Gson gson = HttpTaskServer.getGson();

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTask();
        manager.removeAllSubtask();
        manager.removeAllEpic();
        taskServer.start();
    }

    @AfterEach

    public void shutDown() {
        taskServer.stop(1);
    }

    //Тесты на ошибки 400,404

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createTask(task1);
        Task task2 = new Task("Task 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T01:00:00Z"));
        manager.createTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> historyTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(historyTasks, "Список задач не должен быть null");
        Task firstTaskInHistory = historyTasks.getFirst();
        String actually = firstTaskInHistory.getName();
        String expected = "Task 1";
        Assertions.assertEquals(expected, actually, "Некорректное имя задачи");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T01:00:00Z"));
        manager.createTask(task1);
        Task task2 = new Task("Task 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> prioritisedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(prioritisedTasks, "Список задач не должен быть null");
        Task firstTaskInHistory = prioritisedTasks.getFirst();
        String actually = firstTaskInHistory.getName();
        String expected = "Task 2";
        Assertions.assertEquals(expected, actually, "Некорректное имя задачи");
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = manager.getTaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createTask(task1);
        int taskId = task1.getId();

        HttpClient client = HttpClient.newHttpClient();
        Task task2 = new Task(taskId, "Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T01:00:00Z"));
        String task2Json = gson.toJson(task2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = manager.getTaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(tasks, "Список задач не должен быть null");
        Task getedTask = tasks.getFirst();
        String actually = getedTask.getName();
        String expected = "Test 1";
        Assertions.assertEquals(expected, actually, "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        Task task2 = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T01:00:00Z"));
        manager.createTask(task);
        manager.createTask(task2);
        int task2Id = task2.getId();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task2Id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task getedTask = gson.fromJson(response.body(), Task.class);
        String actually = getedTask.getName();
        String expected = "Test 2";
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), Instant.now());
        manager.createTask(task);
        int taskId = task.getId();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = manager.getTaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");

    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Epic> tasksFromManager = manager.getEpicList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertNotNull(epics, "Список задач не должен быть null");
        Epic getedEpic = epics.getFirst();
        String actually = getedEpic.getName();
        String expected = "Epic 1";
        Assertions.assertEquals(expected, actually, "Некорректное имя задачи");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        Epic epic2 = new Epic("Epic 2", "Testing epic 2");
        manager.createEpic(epic);
        manager.createEpic(epic2);
        int taskId = epic2.getId();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + taskId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic getedEpic = gson.fromJson(response.body(), Epic.class);
        String actually = getedEpic.getName();
        String expected = "Epic 2";
        Assertions.assertEquals(expected, actually, "Некорректное имя задачи");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.createEpic(epic);
        int taskId = epic.getId();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + taskId))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Epic> tasksFromManager = manager.getEpicList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");

    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask(epic, "Subtask 1", "Testing subtask 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createSubTask(subtask1);
        Subtask subtask2 = new Subtask(epic, "Subtask 2", "Testing subtask 2",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T02:00:00Z"));
        manager.createSubTask(subtask2);
        int epicIdId = epic.getId();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicIdId + "/subtask"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        ArrayList<Integer> subtaskList = gson.fromJson(response.body(), new TypeToken<List<Integer>>() {
        }.getType());
        assertNotNull(subtaskList, "Список задач не должен быть null");
        String expected = subtask1.getId() + "," + subtask2.getId();
        String actually = subtaskList.getFirst() + "," + subtaskList.get(1);
        Assertions.assertEquals(expected, actually, "Некорректное имя задачи");

    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(epic, "Subtask 1", "Testing subtask 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Subtask> tasksFromManager = manager.getSubtaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.createEpic(epic);
        int epicId = epic.getId();
        Subtask subtask1 = new Subtask(epic, "Subtask 1", "Testing subtask 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createSubTask(subtask1);
        int subtaskId = subtask1.getId();

        Subtask subtask2 = new Subtask(subtaskId, epicId, "Subtask 2", "Testing subtask 2",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:10:00Z"));
        String subtask2Json = gson.toJson(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtask2Json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Subtask> tasksFromManager = manager.getSubtaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(epic, "Subtask 1", "Testing subtask 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createSubTask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertNotNull(subtasks, "Список задач не должен быть null");
        Subtask getedSubtask = subtasks.getFirst();
        String actually = getedSubtask.getName();
        String expected = "Subtask 1";
        Assertions.assertEquals(expected, actually, "Некорректное имя задачи");
    }


    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(epic, "Subtask 1", "Testing subtask 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createSubTask(subtask);
        int subtaskId = subtask.getId();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        String expected = "Subtask 1";
        String actually = gson.fromJson(response.body(), Subtask.class).getName();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(epic, "Subtask 1", "Testing subtask 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createSubTask(subtask);
        int subtaskId = subtask.getId();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Subtask> tasksFromManager = manager.getSubtaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");

    }

    @Test
    public void testTaskNotFoundCode404() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + 888))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

    }

    @Test
    public void testTasksIntersectCode406() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5000), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createTask(task);
        Task task1 = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:01:00Z"));
        String taskJson = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    public void testEpickNotFoundCode404() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + 888))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

    }

    @Test
    public void testSubtaskNotFoundCode404() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(epic, "Subtask 1", "Testing subtask 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createSubTask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + 888))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

    }

    @Test
    public void testSubtasksIntersectCode406() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(epic, "Subtask 1", "Testing subtask 1",
                Status.NEW, Duration.ofMinutes(50000), Instant.parse("2025-01-01T00:00:00Z"));
        manager.createSubTask(subtask);

        Subtask subtask1 = new Subtask(epic, "Subtask 2", "Testing subtask 1",
                Status.NEW, Duration.ofMinutes(5), Instant.parse("2025-01-01T00:00:10Z"));
        String subtaskJson = gson.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }
}
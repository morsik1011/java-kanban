import adapter.DurationAdapter;
import adapter.InstantAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import manager.handler.*;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

public class HttpTaskServer {

    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager, String hostname, Integer port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        httpServer = HttpServer.create(address, 0);

        Gson jsonMapper = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        httpServer.createContext("/tasks", new HttpTaskHandler(manager, jsonMapper));
        httpServer.createContext("/epics", new HttpEpicHandler(manager, jsonMapper));
        httpServer.createContext("/subtasks", new HttpSubtaskHandler(manager, jsonMapper));
        httpServer.createContext("/history", new HttpHistoryHandler(manager, jsonMapper));
        httpServer.createContext("/prioritized", new HttpPrioritizedHandler(manager, jsonMapper));
    }

    public void start() {
        httpServer.start();
    }

    public void stop(int delay) {
        httpServer.stop(delay);
    }

    public static Gson getGson() {
        Gson jsonMapper = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        return jsonMapper;
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager, "localhost", 8080);

        manager.createTask(new Task("Task1", "Description", Status.NEW, Duration.ofMinutes(1), Instant.now()));
        manager.createTask(new Task("Task1", "Description", Status.NEW, Duration.ofMinutes(2), Instant.now().plus(Duration.ofMinutes(10))));
        Epic epic1 = manager.createEpic(new Epic("Epic1", "Description"));
        manager.createEpic(new Epic("Epic2", "Description"));
        manager.createSubTask(new Subtask(epic1, "Subtask1", "Description", Status.NEW, Duration.ofMinutes(2), Instant.now().plus(Duration.ofMinutes(20))));
        manager.createSubTask(new Subtask(epic1, "Subtask2", "Description", Status.NEW, Duration.ofMinutes(2), Instant.now().plus(Duration.ofMinutes(30))));

        httpTaskServer.start();
    }
}
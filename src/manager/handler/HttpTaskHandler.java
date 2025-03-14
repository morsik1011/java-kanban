package manager.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ErrorResponse;
import exception.TaskInteractionException;
import exception.TaskNotFoundException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson jsonMapper;

    public HttpTaskHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    ErrorResponse errorResponse = new ErrorResponse(String.format("Обработка метода %s не предусмотрена", method), 405, exchange.getRequestURI().getPath());
                    String jsonText = jsonMapper.toJson(errorResponse);
                    sendText(exchange, jsonText, 405);
                    break;
            }
        } catch (TaskNotFoundException exception) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 404, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponse);
            sendText(exchange, jsonText, errorResponse.getErrorCode());
        } catch (TaskInteractionException exception) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 406, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponse);
            sendText(exchange, jsonText, errorResponse.getErrorCode());
        } catch (Exception exception) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 500, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponse);
            sendText(exchange, jsonText, errorResponse.getErrorCode());
        } finally {
            exchange.close();
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
        String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
        Task task = jsonMapper.fromJson(bodyString, Task.class);

        if (task.getId() == null) {
            Task createdTask = taskManager.createTask(task);
            if (!taskManager.hasInteraction(createdTask)) {
                throw new TaskInteractionException("Новая задача с id- " + createdTask.getId() + " пересекается с существующими задачами.");
            }
            String json = jsonMapper.toJson(createdTask);
            sendText(exchange, json, 200);
        } else {
            Task updatedTask = taskManager.updateTask(task);
            if (!taskManager.hasInteraction(updatedTask)) {
                throw new TaskInteractionException("Задача с id- " + updatedTask.getId() + " пересекается с существующими задачами.");
            }
            String json = jsonMapper.toJson(updatedTask);
            sendText(exchange, json, 201);

        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlPaths = path.split("/");
        if (urlPaths.length == 3) {
            int id = Integer.parseInt(urlPaths[2]);
            if (taskManager.getTaskById(id) == null) {
                throw new TaskNotFoundException("Задача с id=" + id + " не найдена");
            } else {
                Task deletedTask = taskManager.removeTaskById(id);
                String json = jsonMapper.toJson(deletedTask);
                sendText(exchange, json, 200);
            }
        }
        if (urlPaths.length == 2) {
            throw new TaskNotFoundException("id удаляемой задачи не указан");
        }
    }


    private void handleGet(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlPaths = path.split("/");
        if (urlPaths.length == 3) {
            int id = Integer.parseInt(urlPaths[2]);
            Task taskById = taskManager.getTaskById(id);
            if (taskManager.getTaskById(id) == null) {
                throw new TaskNotFoundException("Задача с id=" + id + " не найдена");
            }
            String json = jsonMapper.toJson(taskById);
            sendText(exchange, json, 200);

        }
        if (urlPaths.length == 2) {
            List<Task> allTasks = taskManager.getTaskList();
            String json = jsonMapper.toJson(allTasks);
            sendText(exchange, json, 200);
        }
    }
}


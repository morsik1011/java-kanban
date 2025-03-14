package manager.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ErrorResponse;
import exception.TaskInteractionException;
import exception.TaskNotFoundException;
import manager.TaskManager;
import tasks.Subtask;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpSubtaskHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson jsonMapper;

    public HttpSubtaskHandler(TaskManager taskManager, Gson jsonMapper) {
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
        Subtask subtask = jsonMapper.fromJson(bodyString, Subtask.class);

        if (subtask.getId() == null) {

            Subtask createdSubtask = taskManager.createSubTask(subtask);
            if (!taskManager.hasInteraction(createdSubtask)) {
                throw new TaskInteractionException("Новая задача с id- " + createdSubtask.getId() + " пересекается с существующими задачами.");
            }
            String json = jsonMapper.toJson(createdSubtask);
            sendText(exchange, json, 201);
        } else {

            Subtask updatedSubtask = taskManager.updateSubtask(subtask);
            if (!taskManager.hasInteraction(updatedSubtask)) {
                throw new TaskInteractionException("Задача с id- " + updatedSubtask.getId() + " пересекается с существующими задачами.");
            }
            String json = jsonMapper.toJson(updatedSubtask);
            sendText(exchange, json, 201);

        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlPaths = path.split("/");
        if (urlPaths.length == 3) {
            int id = Integer.parseInt(urlPaths[2]);
            if (taskManager.getSubtaskById(id) == null) {
                throw new TaskNotFoundException("Задача с id=" + id + " не найдена");
            } else {
                Subtask deletedSubtask = taskManager.removeSubtaskById(id);
                String json = jsonMapper.toJson(deletedSubtask);
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
            Subtask subtaskById = taskManager.getSubtaskById(id);
            if (taskManager.getSubtaskById(id) == null) {
                throw new TaskNotFoundException("Задача с id=" + id + " не найдена");
            }
            String json = jsonMapper.toJson(subtaskById);
            sendText(exchange, json, 200);

        }
        if (urlPaths.length == 2) {
            List<Subtask> allSubtasks = taskManager.getSubtaskList();
            String json = jsonMapper.toJson(allSubtasks);
            sendText(exchange, json, 200);
        }
    }
}




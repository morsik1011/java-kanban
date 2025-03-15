package manager.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ErrorResponse;
import exception.TaskNotFoundException;
import manager.TaskManager;
import tasks.Epic;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HttpEpicHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson jsonMapper;

    public HttpEpicHandler(TaskManager taskManager, Gson jsonMapper) {
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
        Epic epic = jsonMapper.fromJson(bodyString, Epic.class);
        Epic createdEpic = taskManager.createEpic(epic);
        String json = jsonMapper.toJson(createdEpic);
        sendText(exchange, json, 201);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlPaths = path.split("/");
        if (urlPaths.length == 3) {
            int id = Integer.parseInt(urlPaths[2]);
            if (taskManager.getEpickById(id) == null) {
                throw new TaskNotFoundException("Эпик с id=" + id + " не найдена");
            } else {
                Epic deletedEpic = taskManager.removeEpicById(id);
                String json = jsonMapper.toJson(deletedEpic);
                sendText(exchange, json, 200);
            }
        }
        if (urlPaths.length == 2) {
            throw new TaskNotFoundException("id удаляемого эпика не указан");
        }
    }


    private void handleGet(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlPaths = path.split("/");
        if (urlPaths.length == 4) {
            int id = Integer.parseInt(urlPaths[2]);
            Epic epicById = taskManager.getEpickById(id);
            if (taskManager.getEpickById(id) == null) {
                throw new TaskNotFoundException("Эпик с id=" + id + " не найден");
            }
            String command = urlPaths[3];
            if (!Objects.equals(command, "subtask")) {
                throw new TaskNotFoundException("Обработка метода " + command + " не предусмотрена");
            }
            ArrayList<Integer> subtaskList = epicById.getSubTaskIdList();
            String json = jsonMapper.toJson(subtaskList);
            sendText(exchange, json, 200);
        }
        if (urlPaths.length == 3) {
            int id = Integer.parseInt(urlPaths[2]);
            Epic epicById = taskManager.getEpickById(id);
            if (taskManager.getEpickById(id) == null) {
                throw new TaskNotFoundException("Эпик с id=" + id + " не найден");
            }
            String json = jsonMapper.toJson(epicById);
            sendText(exchange, json, 200);

        }
        if (urlPaths.length == 2) {
            List<Epic> allEpics = taskManager.getEpicList();
            String json = jsonMapper.toJson(allEpics);
            sendText(exchange, json, 200);
        }
    }
}


package manager.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ErrorResponse;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HttpHistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson jsonMapper;

    public HttpHistoryHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            if (method.equals("GET")) {
                List<Task> history = taskManager.getHistory();
                String json = jsonMapper.toJson(history);
                sendText(exchange, json, 200);
            } else {
                ErrorResponse errorResponse = new ErrorResponse(String.format("Обработка метода %s не предусмотрена", method), 405, exchange.getRequestURI().getPath());
                String jsonText = jsonMapper.toJson(errorResponse);
                sendText(exchange, jsonText, 405);
            }
        } catch (Exception exception) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 500, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponse);
            sendText(exchange, jsonText, errorResponse.getErrorCode());
        } finally {
            exchange.close();
        }
    }
}


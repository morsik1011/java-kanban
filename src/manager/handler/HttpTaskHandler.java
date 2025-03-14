package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
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
         //  sendText(exchange, "Такого эндпоинта не существует", 404);
            break;
    }
} catch (Exception exception){
    System.out.println("Ошибка обработки запроса: "+ exception.getMessage());
} finally {
    exchange.close();
}
          }

    private void handlePost(HttpExchange exchange) throws IOException{
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
    }


    private void handleGet(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlPaths = path.split("/");
        if (urlPaths.length == 3) {
            //проверка, что 2 элемент целое число
            int id = Integer.parseInt(urlPaths[2]);
            Task taskById = taskManager.getTaskById(id);
            String json = jsonMapper.toJson(taskById);
            sendText(exchange, json);

        }
        if (urlPaths.length == 2) {
            List<Task> allTasks = taskManager.getTaskList();
            String json = jsonMapper.toJson(allTasks);
            sendText(exchange, json);
        }
    }
}

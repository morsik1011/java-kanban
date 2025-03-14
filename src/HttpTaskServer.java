import adapter.DurationAdapter;
import adapter.InstantAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.HttpTaskHandler;
import manager.Managers;
import manager.TaskManager;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import handler.HttpTaskHandler;
public class HttpTaskServer  {

   public static void main (String[] args) throws IOException {
       InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8080);
       HttpServer httpServer = HttpServer.create(address, 0);

       TaskManager manager = Managers.getDefault();
       manager.createTask(new Task("Task1","Description1", Status.NEW, Duration.ofMinutes(1), Instant.now()));
       manager.createTask(new Task("Task2","Description2", Status.NEW, Duration.ofMinutes(2), Instant.now().plus(Duration.ofMinutes(10))));

       Gson jsonMapper = new GsonBuilder()
               .registerTypeAdapter(Duration.class, new DurationAdapter())
               .registerTypeAdapter(Instant.class, new InstantAdapter())
               .create();
       httpServer.createContext("/tasks", new HttpTaskHandler(manager,jsonMapper));
      // httpServer.createContext("/epics", new HttpEpicHandler());
      // httpServer.createContext("/subtasks", new HttpSubtaskHandler());
       // httpServer.createContext("/history", new HttpHistoryHandler());
       // httpServer.createContext("/prioritized", new HttpPrioritizedHandler());
       httpServer.start();
   }

}

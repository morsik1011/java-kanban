import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {

        File file = new File("data.csv");
        TaskManager taskManager = Managers.getFileBackTaskManager(file);


        Task task1 = new Task("Задача1", "Описание1", Status.NEW, Duration.ofMinutes(1), Instant.now());
        Task task2 = new Task("Задача2", "Описание2", Status.NEW, Duration.ofMinutes(5), Instant.now().plus(Duration.ofMinutes(10)));
        Task task3 = new Task("Задача3", "Описание3", Status.NEW, Duration.ofMinutes(10), Instant.now().plus(Duration.ofMinutes(20)));
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask11 = new Subtask(epic1, "подзадача1", "Описание", Status.IN_PROGRESS, Duration.ofMinutes(5), Instant.now().plus(Duration.ofMinutes(30)));
        Subtask subtask21 = new Subtask(epic2, "подзадача2", "Описание", Status.DONE, Duration.ofMinutes(1), Instant.now().plus(Duration.ofMinutes(40)));
        Subtask subtask31 = new Subtask(epic1, "подзадача3", "Описание", Status.NEW, Duration.ofMinutes(4), Instant.now().plus(Duration.ofMinutes(50)));
        taskManager.createSubTask(subtask11);
        taskManager.createSubTask(subtask21);
        taskManager.createSubTask(subtask31);


        TaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(file);
        printAllTasks(taskManagerFromFile);
    }

    private static void printAllTasks(TaskManager manager) {

        System.out.println("-------");
        System.out.println("Задачи:");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpicList()) {
            System.out.println(epic);
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtaskList()) {
            System.out.println(subtask);
        }
        System.out.println("-------");
    }

}






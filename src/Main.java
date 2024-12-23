import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        Epic epic1 = new Epic("Эпик1", "Эпик с 2 подзадачами");
        Epic epic2 = new Epic("Эпик2", "Эпик с 1 подзадачей");


        taskManager.createTask(task1);
        taskManager.createTask(task2);

        System.out.printf("Печать списка задач: %s%n", taskManager.getTaskList());
        System.out.printf("Печать списка задач по id: %s%n", taskManager.getTaskById(1));
        task2.setStatus(Status.DONE);
        task2.setName("Новая задача2");
        taskManager.updateTask(task2);
        System.out.printf("Обновление статуса и данных задачи2: %s%n", taskManager.getTaskById(task2.getId()));
        taskManager.removeTaskById(task2.getId());
        System.out.printf("Печать списка задач после удаления задачи2: %s%n", taskManager.getTaskList());


        printAllTasks(taskManager);



        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask(epic1, "подзадача1", "Описание подзадачи 1", Status.IN_PROGRESS);
        Subtask subtask2 = new Subtask(epic1, "подзадача2", "Описание подзадачи 2", Status.DONE);
        Subtask subtask3 = new Subtask(epic2, "подзадача3", "Описание подзадачи 3", Status.NEW);

        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);



        System.out.printf("Список эпиков:%s%n", taskManager.getEpicList());
        System.out.printf("Список подзадач: %s%n",taskManager.getSubtaskList());

        System.out.printf("Подзадача1: %s%n", taskManager.getSubtaskById(subtask1.getId()));


        System.out.printf("Эпик1: %s%n",taskManager.getEpickById(epic1.getId()));

        epic1.setName("Новый эпик1");
        subtask1.setStatus(Status.DONE);

        taskManager.updateSubtask(subtask1);
        taskManager.updateEpic(epic1);
        System.out.printf("Список эпиков после изменения эпика1: %s%n", taskManager.getEpicList());
        printAllTasks(taskManager);

        taskManager.removeSubtaskById(subtask1.getId());
        System.out.printf("Cписок подзадач после удаления подзадачи1: %s%n", taskManager.getSubtaskList());

        taskManager.removeEpicById(epic1.getId());
        System.out.printf("Список эпиков после удаления эпика1: %s%n",taskManager.getEpicList());

        System.out.printf("список подзадач: %s%n", taskManager.getSubtaskList());

        taskManager.removeAllEpic();
        System.out.printf("Список после удаления всех эпиков: %s%n",taskManager.getEpicList());

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

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-------");
    }
}


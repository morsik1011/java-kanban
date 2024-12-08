import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        Epic epic1 = new Epic("Эпик1", "Эпик с 2 подзадачами");
        Epic epic2 = new Epic("Эпик2", "Эпик с 1 подзадачей");


        taskManager.createTask(task1);
        taskManager.createTask(task2);

        System.out.println("Печать списка задач:" + taskManager.getTaskList());
        System.out.println("Печать списка задач по id:" + taskManager.getTaskById(1));
        task2.setStatus(Status.DONE);
        task2.setName("Новая задача2");
        taskManager.updateTask(task2);
        System.out.println("Обновление статуса и данных задачи2:" + taskManager.getTaskById(task2.getId()));
        taskManager.removeTaskById(task2.getId());
        System.out.println("Печать списка задач после удаления задачи2:" + taskManager.getTaskList());
        taskManager.removeAllTask();
        System.out.println("Печать пустого списка задач:" + taskManager.getTaskList());

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask(epic1, "подзадача1", "Описание подзадачи 1", Status.IN_PROGRESS);
        Subtask subtask2 = new Subtask(epic1, "подзадача2", "Описание подзадачи 2", Status.DONE);
        Subtask subtask3 = new Subtask(epic2, "подзадача3", "Описание подзадачи 3", Status.NEW);

        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        System.out.println("Список эпиков:" + taskManager.getEpicList());
        System.out.println("Список подзадач:" + taskManager.getSubtaskList());

        System.out.println("Подзадача1:" + taskManager.getSubtaskById(subtask1.getId()));


        System.out.println("Эпик1 " + taskManager.getEpickById(epic1.getId()));

        epic1.setName("Новый эпик1");
        subtask1.setStatus(Status.DONE);

        taskManager.updateSubtask(subtask1);
        taskManager.updateEpic(epic1);
        System.out.println("Список эпиков после изменения эпика1:" + taskManager.getEpicList());


        taskManager.removeSubtaskById(subtask1.getId());
        System.out.println("Cписок подзадач после удаления подзадачи1 " + taskManager.getSubtaskList());

        taskManager.removeEpicById(epic1.getId());
        System.out.println("Список эпиков после удаления эпика1:" + taskManager.getEpicList()
                + "список подзадач " + taskManager.getSubtaskList());

        taskManager.removeAllEpic();
        System.out.println("Список после удаления всех эпиков:" + taskManager.getEpicList());

    }
}


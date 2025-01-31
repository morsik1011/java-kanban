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

        taskManager.removeAllSubtask();
        taskManager.removeAllTask();
        taskManager.removeAllEpic();

        //Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач.
        //Запросите созданные задачи несколько раз в разном порядке.
        //После каждого запроса выведите историю и убедитесь, что в ней нет повторов.
        Task task11 = new Task("Задача11", "Описание11", Status.NEW);
        Task task21 = new Task("Задача21", "Описание21", Status.NEW);
        Epic epic11 = new Epic("Эпик11", "Эпик с 3 подзадачами");
        Epic epic21 = new Epic("Эпик21", "Эпик без подзадач");
        taskManager.createEpic(epic11);
        taskManager.createEpic(epic21);
        Subtask subtask11 = new Subtask(epic11, "подзадача11", "Описание подзадачи 11", Status.IN_PROGRESS);
        Subtask subtask21 = new Subtask(epic11, "подзадача21", "Описание подзадачи 21", Status.DONE);
        Subtask subtask31 = new Subtask(epic11, "подзадача31", "Описание подзадачи 31", Status.NEW);

        taskManager.createTask(task11);
        taskManager.createTask(task21);
        taskManager.createSubTask(subtask11);
        taskManager.createSubTask(subtask21);
        taskManager.createSubTask(subtask31);

        taskManager.getTaskById(task21.getId());
        taskManager.getEpickById(epic21.getId());
        taskManager.getTaskById(task21.getId());
        taskManager.getTaskById(task11.getId());
        taskManager.getEpickById(epic11.getId());
        taskManager.getSubtaskById(subtask11.getId());
        taskManager.getSubtaskById(subtask31.getId());

        System.out.println("!История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-------");

        //Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться.
        taskManager.removeTaskById(task11.getId());
        System.out.println("!История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-------");
        //Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
        taskManager.removeEpicById(epic11.getId());
        System.out.println("!История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-------");
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


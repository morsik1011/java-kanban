package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void init() {
        taskManager = Managers.getDefault();
    }


    @Test
    void getHistory() {

        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        Epic epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createTask(task1);
        taskManager.createEpic(epic1);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpickById(epic1.getId());

        List<Task> history = taskManager.getHistory();
        assertNotNull(history);
        assertEquals(2, history.size());
    }

    @Test
    void createTask() {
        String name = "Задача";
        String decriptions = "Описание";
        Task task = new Task("Задача", "Описание", Status.NEW);

        taskManager.createTask(task);

        Task actualTask = taskManager.getTaskById(task.getId());
        Assertions.assertEquals(actualTask.getDescription(), decriptions);
        Assertions.assertEquals(actualTask.getName(), name);
        Assertions.assertEquals(actualTask.getStatus(), Status.NEW);

    }

    @Test
    void updateTask() {
        String name = "Обновленная задача";
        String decriptions = "Обновленный статус";
        Task task = new Task("Задача", "Описание", Status.NEW);
        taskManager.createTask(task);
        task.setName("Обновленная задача");
        task.setDescription("Обновленный статус");
        task.setStatus(Status.DONE);

        taskManager.updateTask(task);


        Assertions.assertEquals(task.getDescription(), decriptions);
        Assertions.assertEquals(task.getName(), name);
        Assertions.assertEquals(task.getStatus(), Status.DONE);
    }

    @Test
    void removeAllTask() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.removeAllTask();

        Assertions.assertTrue(taskManager.getTaskList().isEmpty());
    }

    @Test
    void removeTaskById() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        taskManager.createTask(task);

        taskManager.removeTaskById(0);

        Assertions.assertTrue(taskManager.getTaskList().isEmpty());
    }

    @Test
    void getTaskList() {
        String name1 = "Задача1";
        String decription1 = "Описание1";
        String name2 = "Задача2";
        String decription2 = "Описание2";
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        ArrayList<Task> taskList = taskManager.getTaskList();

        Assertions.assertEquals(taskList.getFirst().getName(), name1);
        Assertions.assertEquals(taskList.getFirst().getDescription(), decription1);
        Assertions.assertEquals(taskList.getFirst().getStatus(), Status.NEW);
        Assertions.assertEquals(taskList.get(1).getName(), name2);
        Assertions.assertEquals(taskList.get(1).getDescription(), decription2);
        Assertions.assertEquals(taskList.get(1).getStatus(), Status.NEW);
    }


    @Test
    void getTaskById() {
        String name = "Задача2";
        String decription = "Описание2";
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Task actualTask = taskManager.getTaskById(task2.getId());

        Assertions.assertEquals(actualTask.getName(), name);
        Assertions.assertEquals(actualTask.getDescription(), decription);
        Assertions.assertEquals(actualTask.getStatus(), Status.NEW);
    }

    @Test
    void createEpic() {
        String name = "Эпик";
        String decription = "Описание";
        Epic epic = new Epic("Эпик", "Описание");

        taskManager.createEpic(epic);

        Epic actualEpic = taskManager.getEpickById(epic.getId());
        Assertions.assertEquals(actualEpic.getDescription(), decription);
        Assertions.assertEquals(actualEpic.getName(), name);
        Assertions.assertEquals(actualEpic.getStatus(), Status.NEW);
    }

    @Test
    void updateEpic() {
        String name = "Новый эпик";
        String decription = "Новое описание";
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);
        epic.setName("Новый эпик");
        epic.setDescription("Новое описание");

        taskManager.updateEpic(epic);

        Assertions.assertEquals(epic.getName(), name);
        Assertions.assertEquals(epic.getDescription(), decription);
    }

    @Test
    void removeAllEpic() {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE);
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW);
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Описание3", Status.IN_PROGRESS);
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        taskManager.removeAllEpic();

        Assertions.assertTrue(taskManager.getEpicList().isEmpty());
        Assertions.assertTrue(taskManager.getSubtaskList().isEmpty());
    }

    @Test
    void removeEpicById() {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE);
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW);
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);

        taskManager.removeEpicById(epic1.getId());

        Assertions.assertTrue(taskManager.getEpicList().isEmpty());
        Assertions.assertTrue(taskManager.getSubtaskList().isEmpty());
    }

    @Test
    void getEpicList() {
        String name1 = "Эпик1";
        String decription1 = "Описание1";
        String name2 = "Эпик2";
        String decription2 = "Описание2";
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE);
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW);
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Описание3", Status.NEW);
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        ArrayList<Epic> list = taskManager.getEpicList();

        Assertions.assertEquals(list.get(0).getName(), name1);
        Assertions.assertEquals(list.get(0).getDescription(), decription1);
        Assertions.assertEquals(list.get(0).getStatus(), Status.IN_PROGRESS);
        Assertions.assertEquals(list.get(1).getName(), name2);
        Assertions.assertEquals(list.get(1).getDescription(), decription2);
        Assertions.assertEquals(list.get(1).getStatus(), Status.NEW);
    }

    @Test
    void getEpickById() {
        String name1 = "Эпик1";
        String decription1 = "Описание1";
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE);
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW);
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Описание3", Status.NEW);
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        Epic actualEpic = taskManager.getEpickById(epic1.getId());

        Assertions.assertEquals(actualEpic.getName(), name1);
        Assertions.assertEquals(actualEpic.getDescription(), decription1);
        Assertions.assertEquals(actualEpic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void createSubTask() {
        String name = "Задача1";
        String decription = "Описание1";
        Epic epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE);
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW);

        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);

        Subtask actualSubtask = taskManager.getSubtaskById(subtask1.getId());
        Assertions.assertEquals(actualSubtask.getDescription(), decription);
        Assertions.assertEquals(actualSubtask.getName(), name);
        Assertions.assertEquals(actualSubtask.getStatus(), Status.DONE);
        Assertions.assertEquals(epic1.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void updateSubtask() {
        String name = "Новая задача1";
        String decription = "Новое описание1";
        Epic epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.NEW);
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.DONE);
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);

        subtask1.setStatus(Status.DONE);
        subtask1.setName("Новая задача1");
        subtask1.setDescription("Новое описание1");
        taskManager.updateSubtask(subtask1);

        Assertions.assertEquals(subtask1.getDescription(), decription);
        Assertions.assertEquals(subtask1.getName(), name);
        Assertions.assertEquals(subtask1.getStatus(), Status.DONE);
        Assertions.assertEquals(epic1.getStatus(), Status.DONE);
    }

    @Test
    void removeAllSubtask() {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE);
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW);
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Описание3", Status.NEW);
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        taskManager.removeAllSubtask();

        Assertions.assertTrue(taskManager.getSubtaskList().isEmpty());
    }

    @Test
    void removeSubtaskById() {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE);
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW);
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Описание3", Status.NEW);
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        taskManager.removeSubtaskById(subtask3.getId());

        ArrayList<Task> subtaskList = taskManager.getSubtaskList();
        Assertions.assertEquals(subtaskList.size(), 2);
    }

    @Test
    void getSubtaskList() {
        String name1 = "Задача1";
        String decription1 = "Описание1";
        String name2 = "Задача2";
        String decription2 = "Описание2";
        Epic epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE);
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW);
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);


        ArrayList<Task> list = taskManager.getSubtaskList();

        Assertions.assertEquals(list.getFirst().getName(), name1);
        Assertions.assertEquals(list.get(0).getDescription(), decription1);
        Assertions.assertEquals(list.get(0).getStatus(), Status.DONE);
        Assertions.assertEquals(list.get(1).getName(), name2);
        Assertions.assertEquals(list.get(1).getDescription(), decription2);
        Assertions.assertEquals(list.get(1).getStatus(), Status.NEW);

    }

    @Test
    void getSubtaskById() {
        String name1 = "Задача2";
        String decription1 = "Описание2";
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(epic1, "Задача1", "Описание1", Status.DONE);
        Subtask subtask2 = new Subtask(epic1, "Задача2", "Описание2", Status.NEW);
        Subtask subtask3 = new Subtask(epic2, "Задача3", "Описание3", Status.NEW);
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        Subtask actualSubtask = taskManager.getSubtaskById(subtask2.getId());

        Assertions.assertEquals(actualSubtask.getName(), name1);
        Assertions.assertEquals(actualSubtask.getDescription(), decription1);
        Assertions.assertEquals(actualSubtask.getStatus(), Status.NEW);
    }
}


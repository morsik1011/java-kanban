package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;


import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    Task createTask(Task newTask);

    Task updateTask(Task newTask);

    void removeAllTask();

    Task removeTaskById(Integer id);

    ArrayList<Task> getTaskList();

    Task getTaskById(int id);

    Epic createEpic(Epic newEpic);

    Epic updateEpic(Epic newEpic);

    void removeAllEpic();

    Epic removeEpicById(Integer id);

    ArrayList<Epic> getEpicList();

    Epic getEpickById(Integer id);

    Subtask createSubTask(Subtask newSubtask);

    Subtask updateSubtask(Subtask newSubtask);

    void removeAllSubtask();

    Subtask removeSubtaskById(Integer id);

    ArrayList<Task> getSubtaskList();

    Subtask getSubtaskById(Integer id);
}


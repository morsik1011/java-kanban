package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    Set<Task> getPrioritizedTasks();

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

    ArrayList<Subtask> getSubtaskList();

    Subtask getSubtaskById(Integer id);
}


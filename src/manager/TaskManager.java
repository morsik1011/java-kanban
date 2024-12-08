package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {

    private final HashMap<Integer, Task> task = new HashMap<>();
    private final HashMap<Integer, Epic> epic = new HashMap<>();
    private final HashMap<Integer, Subtask> subtask = new HashMap<>();
    private static int id = 0;

    private int nextId() {
        return id++;
    }

    //tasks.Task

    public Task createTask(Task newTask) {
        int newId = nextId();
        newTask.setId(newId);
        task.put(newTask.getId(), newTask);
        return newTask;
    }

    public Task updateTask(Task newTask) {
        if (task.containsKey(newTask.getId())) {
            Task existingTask = task.get(newTask.getId());
            existingTask.setName(newTask.getName());
            existingTask.setDescription(newTask.getDescription());
            existingTask.setStatus(newTask.getStatus());
            return newTask;
        }
        return null;
    }

    public void removeAllTask() {
        task.clear();
    }

    public Task removeTaskById(Integer id) {
        return task.remove(id);
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(task.values());
    }

    public Task getTaskById(int id) {
        return task.get(id);
    }

//tasks.Epic


    public Epic createEpic(Epic newEpic) {
        int newId = nextId();
        newEpic.setId(newId);
        epic.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public Epic updateEpic(Epic newEpic) {
        if (epic.containsKey(newEpic.getId())) {
            Epic existingEpic = epic.get(newEpic.getId());
            existingEpic.setName(newEpic.getName());
            existingEpic.setDescription(newEpic.getDescription());

            return newEpic;
        }
        return null;
    }

    public void removeAllEpic() {
        subtask.clear();
        epic.clear();
    }

    private void removeSubTaskIdList(ArrayList<Integer> subtaskId) {
        for (Integer id : subtaskId) {
            subtask.remove(id);
        }
    }

    public Epic removeEpicById(Integer id) {
        Epic deleteEpic = epic.get(id);
        removeSubTaskIdList(deleteEpic.getSubTaskIdList());
        return epic.remove(id);
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epic.values());
    }

    public Epic getEpickById(Integer id) {
        return epic.get(id);
    }


//tasks.Subtask


    public Subtask createSubTask(Subtask newSubtask) {
        if (epic.containsKey(newSubtask.getEpicId())) {
            int newId = nextId();
            newSubtask.setId(newId);
            subtask.put(newSubtask.getId(), newSubtask);
            Epic existingEpic = epic.get(newSubtask.getEpicId());
            existingEpic.addSubtaskId(newSubtask);
            updateEpicStatus(epic.get(newSubtask.getEpicId()));
            return newSubtask;
        }
        return null;
    }


    private ArrayList<Subtask> getListSubTaskByEpicId(ArrayList<Integer> subTaskId) {
        ArrayList<Subtask> subTasks = new ArrayList<>();
        for (Integer id : subTaskId) {
            subTasks.add(subtask.get(id));
        }
        return subTasks;
    }


    private void updateEpicStatus(Epic epic) {
        if (epic.getSubTaskIdList().isEmpty()) {
            epic.setStatus(Status.NEW);

        } else {
            boolean allSubTasksIsNew = true;
            boolean allSubTasksIsDone = true;
            ArrayList<Subtask> epicSubTasks = getListSubTaskByEpicId(epic.getSubTaskIdList());
            for (Subtask subTask : epicSubTasks) {
                if (subTask.getStatus() != Status.NEW) {
                    allSubTasksIsNew = false;
                }
                if (subTask.getStatus() != Status.DONE) {
                    allSubTasksIsDone = false;
                }
            }
            if (allSubTasksIsDone) {
                epic.setStatus(Status.DONE);
            } else if (allSubTasksIsNew) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }


    public Subtask updateSubtask(Subtask newSubtask) {
        if (subtask.containsKey(newSubtask.getId())) {
            Subtask existingSubtask = subtask.get(newSubtask.getId());
            existingSubtask.setName(newSubtask.getName());
            existingSubtask.setDescription(newSubtask.getDescription());
            existingSubtask.setStatus(newSubtask.getStatus());
            updateEpicStatus(epic.get(newSubtask.getEpicId()));
        }
        return newSubtask;
    }

    public void removeAllSubtask() {
        subtask.clear();
    }

    public Subtask removeSubtaskById(Integer id) {
        return subtask.remove(id);
    }

    public ArrayList<Task> getSubtaskList() {
        return new ArrayList<>(subtask.values());
    }

    public Subtask getSubtaskById(Integer id) {
        return subtask.get(id);
    }
}


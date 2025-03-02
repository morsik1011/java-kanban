package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> task = new HashMap<>();
    protected HashMap<Integer, Epic> epic = new HashMap<>();
    protected HashMap<Integer, Subtask> subtask = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private static int id = 0;


    private int nextId() {
        return id++;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new LinkedHashSet<>(prioritizedTasks);
    }

    private boolean hasInteraction(Task task) {
        return prioritizedTasks.stream()
                .filter(otherTask -> (
                        (otherTask.getStartTime().isBefore(task.getStartTime()) && (otherTask.getEndTime().isAfter(task.getStartTime())))) ||
                        (otherTask.getStartTime().isBefore(task.getEndTime()) && (otherTask.getEndTime().isAfter(task.getEndTime()))) ||
                        (otherTask.getStartTime().isBefore(task.getStartTime()) && (otherTask.getEndTime().isAfter(task.getEndTime()))) ||
                        (otherTask.getStartTime().isAfter(task.getStartTime()) && (otherTask.getEndTime().isBefore(task.getEndTime())))).findFirst().isEmpty();

    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    //tasks.Task

    @Override
    public Task createTask(Task newTask) {
        int newId = nextId();
        newTask.setId(newId);
        if (!hasInteraction(newTask)) {
            throw new RuntimeException("Новая задача с id- " + newTask.getId() + " пересекается с существующими задачами.");
        } else {
            task.put(newTask.getId(), newTask);
            prioritizedTasks.add(newTask);
            return newTask;
        }
    }

    @Override
    public Task updateTask(Task newTask) {
        if (!hasInteraction(newTask)) {
            throw new RuntimeException("Pадача с id- " + newTask.getId() + " пересекается с существующими задачами.");
        } else {
            if (task.containsKey(newTask.getId())) {
                Task existingTask = task.get(newTask.getId());
                existingTask.setName(newTask.getName());
                existingTask.setDescription(newTask.getDescription());
                existingTask.setStatus(newTask.getStatus());
                return newTask;
            }
            return null;
        }
    }

    @Override
    public void removeAllTask() {
        task.values().forEach(prioritizedTasks::remove);
        task.clear();
    }

    @Override
    public Task removeTaskById(Integer id) {

        historyManager.remove(id);
        prioritizedTasks.remove(task.get(id));
        return task.remove(id);
    }

    @Override
    public ArrayList<Task> getTaskList() {

        return new ArrayList<>(task.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task tasks = task.get(id);
        historyManager.addToHistory(tasks);
        return task.get(id);
    }

//tasks.Epic


    @Override
    public Epic createEpic(Epic newEpic) {
        int newId = nextId();
        newEpic.setId(newId);
        epic.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        if (epic.containsKey(newEpic.getId())) {
            Epic existingEpic = epic.get(newEpic.getId());
            existingEpic.setName(newEpic.getName());
            existingEpic.setDescription(newEpic.getDescription());

            return newEpic;
        }
        return null;
    }

    @Override
    public void removeAllEpic() {
        subtask.values().forEach(prioritizedTasks::remove);
        subtask.clear();
        epic.clear();
    }

    private void removeSubTaskIdList(ArrayList<Integer> subtaskId) {
        subtaskId.forEach(subtask::remove);
    }

    @Override
    public Epic removeEpicById(Integer id) {
        Epic deleteEpic = epic.get(id);
        removeSubTaskIdList(deleteEpic.getSubTaskIdList());
        historyManager.remove(id);
        deleteEpic.getSubTaskIdList().forEach(i -> {
            historyManager.remove(i);
        });

        return epic.remove(id);
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epic.values());
    }

    @Override
    public Epic getEpickById(Integer id) {
        Epic epics = epic.get(id);
        historyManager.addToHistory(epics);
        return epic.get(id);
    }


//tasks.Subtask


    @Override
    public Subtask createSubTask(Subtask newSubtask) {
        if (!hasInteraction(newSubtask)) {
            throw new RuntimeException("Новая подзадача с id- " + newSubtask.getId() + " пересекается с существующими задачами.");
        } else {
            if (epic.containsKey(newSubtask.getEpicId())) {
                int newId = nextId();
                newSubtask.setId(newId);
                subtask.put(newSubtask.getId(), newSubtask);
                prioritizedTasks.add(newSubtask);
                Epic existingEpic = epic.get(newSubtask.getEpicId());
                existingEpic.addSubtaskId(newSubtask);
                updateEpicStatus(epic.get(newSubtask.getEpicId()));
                return newSubtask;
            }
            return null;
        }
    }


    private ArrayList<Subtask> getListSubTaskByEpicId(ArrayList<Integer> subTaskId) {
        return subTaskId.stream().map(subtask::get).collect(Collectors.toCollection(ArrayList::new));


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


    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        if (!hasInteraction(newSubtask)) {
            throw new RuntimeException("Подзадача с id- " + newSubtask.getId() + " пересекается с существующими задачами.");
        } else {
            if (subtask.containsKey(newSubtask.getId())) {
                Subtask existingSubtask = subtask.get(newSubtask.getId());
                existingSubtask.setName(newSubtask.getName());
                existingSubtask.setDescription(newSubtask.getDescription());
                existingSubtask.setStatus(newSubtask.getStatus());
                updateEpicStatus(epic.get(newSubtask.getEpicId()));
            }
            return newSubtask;
        }
    }

    @Override
    public void removeAllSubtask() {
        subtask.values().forEach(prioritizedTasks::remove);
        subtask.clear();
    }

    @Override
    public Subtask removeSubtaskById(Integer id) {
        prioritizedTasks.remove(subtask.get(id));
        historyManager.remove(id);
        return subtask.remove(id);
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtask.values());
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtasks = subtask.get(id);
        historyManager.addToHistory(subtasks);
        return subtask.get(id);
    }
}






package manager;

import exception.FileManagerFileInitialisationException;
import exception.FileManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File data;

    public FileBackedTaskManager(File file) {
        this.data = file;
    }

    //метод восстанавливает данные менеджера из файла при запуске программы
    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            List<String> allLines = Files.readAllLines(file.toPath());
            allLines.removeFirst();
            allLines.stream().map(line -> line.trim().split(","))
                    .forEach(values -> {
                        switch (values[1]) {
                            case "TASK" -> {
                                Task task = taskFromString(values);
                                int taskId = Integer.parseInt(values[0]);
                                task.setId(taskId);
                                fileBackedTaskManager.task.put(taskId, task);
                            }
                            case "EPIC" -> {
                                Epic epic = epicFromString(values);
                                int epicId = Integer.parseInt(values[0]);
                                epic.setId(epicId);
                                fileBackedTaskManager.epic.put(epicId, epic);
                            }
                            case "SUBTASK" -> {
                                Subtask subtask = subtaskFromString(values);
                                int subtaskId = Integer.parseInt(values[0]);
                                subtask.setId(subtaskId);
                                fileBackedTaskManager.subtask.put(subtaskId, subtask);
                            }
                        }

                    });

            return fileBackedTaskManager;

        } catch (IOException exception) {

            String errorMessage = "Ошибка добавления в файл: " + exception.getMessage();
            System.out.println(errorMessage);
            throw new FileManagerFileInitialisationException(errorMessage);
        }

    }

    //метод сохраняет текущее состояние менеджера в указанный файл
    private void save() {
        List<String> lines = Stream.concat(
                Stream.of("id,type,name,status,description,epic,duration,startTime,endTime\n"),
                Stream.of(getTaskList().stream().map(this::taskToString),
                        getEpicList().stream().map(this::epicToString),
                        getSubtaskList().stream().map(this::subtaskToString)
                ).flatMap(Function.identity())
        ).collect(Collectors.toList());

        writeStringToFile(lines);
    }


    private void writeStringToFile(List<String> lines) {
        try (FileWriter fileWriter = new FileWriter(data)) {
            for (String line : lines) {
                fileWriter.write(line);
            }

        } catch (IOException exception) {
            String errorMessage = "Ошибка при сохранении в файл: " + exception.getMessage();
            System.out.println(errorMessage);
            throw new FileManagerSaveException(errorMessage);
        }

    }

    //метод сохранения задачи в строку
    private String taskToString(Task task) {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", task.getId(), "TASK", task.getName(),
                task.getStatus(), task.getDescription(), task.getDuration().toMinutes(), task.getStartTime(), task.getEndTime());
    }

    private String epicToString(Epic epic) {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", epic.getId(), "EPIC", epic.getName(),
                epic.getStatus(), epic.getDescription(), epic.getDuration().toMinutes(), epic.getStartTime(), epic.getEndTime());
    }

    private String subtaskToString(Subtask subtask) {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n", subtask.getId(), "SUBTASK", subtask.getName(),
                subtask.getStatus(), subtask.getDescription(), subtask.getEpicId(), subtask.getDuration().toMinutes(),
                subtask.getStartTime(), subtask.getEndTime());
    }

    //метод создания задачи из строки
    private static Task taskFromString(String[] values) {
        int taskId = Integer.parseInt(values[0]);
        return new Task(taskId,
                values[2],
                values[4],
                statusToString(values[3]),
                Duration.ofMinutes(Long.parseLong(values[5])),
                Instant.parse(values[6]));
    }

    private static Epic epicFromString(String[] values) {
        int epicId = Integer.parseInt(values[0]);
        return new Epic(epicId,
                values[2],
                values[4],
                statusToString(values[3]),
                Duration.ofMinutes(Long.parseLong(values[5])),
                Instant.parse(values[6]));
    }

    private static Subtask subtaskFromString(String[] values) {
        int subtaskId = Integer.parseInt(values[0]);
        int epicId = Integer.parseInt(values[5]);
        return new Subtask(subtaskId,
                epicId,
                values[2],
                values[4],
                statusToString(values[3]),
                Duration.ofMinutes(Long.parseLong(values[6])),
                Instant.parse(values[7]));
    }

    private static Status statusToString(String line) {
        return switch (line) {
            case "NEW" -> Status.NEW;
            case "IN_PROGRESS" -> Status.IN_PROGRESS;
            case "DONE" -> Status.DONE;
            default -> throw new IllegalStateException(String.format("%s,%s", "Неизвестное значение статуса: ", line));
        };
    }

    //Создание, обновление и удаление
    @Override
    public Task createTask(Task newTask) {
        Task createdTask = super.createTask(newTask);
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException(String.format("%s,%s", "Ошибка сохранения задачи", newTask.getName()));
        }
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic newEpic) {
        Epic createdEpic = super.createEpic(newEpic);
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException(String.format("%s,%s", "Ошибка сохранения эпика", newEpic.getName()));
        }
        return createdEpic;
    }

    @Override
    public Subtask createSubTask(Subtask newSubtask) {
        Subtask createdSubtask = super.createSubTask(newSubtask);
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException(String.format("%s,%s", "Ошибка сохранения подзадачи", newSubtask.getName()));
        }
        return createdSubtask;
    }

    @Override
    public Task updateTask(Task newTask) {
        Task updatedTask = super.updateTask(newTask);
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException(String.format("%s,%s", "Ошибка обновления задачи", newTask.getName()));
        }
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Epic updatedEpic = super.updateEpic(newEpic);
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException(String.format("%s,%s", "Ошибка обновления эпика", newEpic.getName()));
        }
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        Subtask updatedSubtask = super.updateSubtask(newSubtask);
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException(String.format("%s,%s", "Ошибка обновления подзадачи", newSubtask.getName()));
        }
        return updatedSubtask;
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException("Ошибка сохранения задачи в файл при удалении");
        }
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException("Ошибка сохранения эпика в файл при удалении");
        }
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException("Ошибка сохранения подзадачи в файл при удалении");
        }
    }

    @Override
    public Task removeTaskById(Integer id) {
        Task removedTask = super.removeTaskById(id);
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException(String.format("%s,%d", "Ошибка сохранения задачи в файл при удалении по id", id));
        }
        return removedTask;
    }

    @Override
    public Epic removeEpicById(Integer id) {
        Epic removedEpic = super.removeEpicById(id);
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException(String.format("%s,%d", "Ошибка сохранения эпика в файл при удалении по id", id));
        }
        return removedEpic;
    }

    @Override
    public Subtask removeSubtaskById(Integer id) {
        Subtask removedSubtask = super.removeSubtaskById(id);
        try {
            save();
        } catch (FileManagerSaveException exception) {
            throw new RuntimeException(String.format("%s,%d", "Ошибка сохранения подзадачи в файл при удалении по id", id));
        }
        return removedSubtask;
    }
}




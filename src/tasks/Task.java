package tasks;


import java.util.Objects;

public class Task {

    private int id;
    private String taskName;
    private String taskDescription;
    private Status taskStatus;

    public Task(String taskName, String taskDescription, Status status) {

        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskName, task.taskName) && Objects.equals(taskDescription, task.taskDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, taskDescription);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + id +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", status=" + taskStatus +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return taskName;
    }

    public void setName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return taskDescription;
    }

    public void setDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Status getStatus() {
        return taskStatus;
    }

    public void setStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

}



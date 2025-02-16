package tasks;

public class Subtask extends Task {

    private int epicId;

    public Subtask(Epic epic, String taskName, String taskDescription, Status taskStatus) {
        super(taskName, taskDescription, taskStatus);
        this.epicId = epic.getId();
    }

    public Subtask(int id, int epicId, String taskName, String taskDescription, Status taskStatus) {
        super(id, taskName, taskDescription, taskStatus);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                " id=" + getId() +
                ", name=" + getName() +
                ", epicId=" + epicId +
                ", status=" + getStatus() +
                '}';
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }


}


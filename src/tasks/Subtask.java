package tasks;

public class Subtask extends Task {

    private int epicId;

    public Subtask(Epic epic, String taskName, String taskDescription, Status taskStatus){
         super (taskName,taskDescription, taskStatus);
         this.epicId=epic.getId();
     }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
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

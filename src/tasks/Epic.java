package tasks;


import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, Status.NEW, Duration.ofMinutes(0), Instant.now());

    }

    public Epic(int id, String taskName, String taskDescription, Status status, Duration duration, Instant startTime) {
        super(id, taskName, taskDescription, status, duration, startTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                " epicid=" + getId() +
                ", name=" + getName() +
                ", status=" + getStatus() +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void setSubTaskIdList(ArrayList<Integer> subTaskIdList) {
        this.subTaskIdList = subTaskIdList;
    }

    public void addSubtaskId(Subtask subtask) {
        subTaskIdList.add(subtask.getId());
    }

}





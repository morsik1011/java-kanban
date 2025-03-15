package tasks;

import java.time.Duration;
import java.time.Instant;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(Epic epic, String taskName, String taskDescription, Status taskStatus, Duration duration, Instant startTime) {
        super(taskName, taskDescription, taskStatus, duration, startTime);
        this.epicId = epic.getId();
    }

    public Subtask(Integer id, Integer epicId, String taskName, String taskDescription, Status taskStatus, Duration duration, Instant startTime) {
        super(id, taskName, taskDescription, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                " id=" + getId() +
                ", name=" + getName() +
                ", epicId=" + epicId +
                ", status=" + getStatus() +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }


}


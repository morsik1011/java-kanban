package tasks;


import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, Status.NEW);

    }

    @Override
    public String toString() {
        return "Epic{" +
                " epicid=" + getId() +
                ", name=" + getName() +
                ", subTaskIdList=" + subTaskIdList +
                ", status=" + getStatus() +
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



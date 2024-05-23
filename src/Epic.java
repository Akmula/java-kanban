import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private int epicId;
    private ArrayList<Integer> subTaskId;

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.epicId = id;
    }

    public Epic(int id, ArrayList<Integer> subTaskId, String title, String description, Status status) {
        super(id, title, description, status);
        this.epicId = id;
        this.subTaskId = subTaskId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public ArrayList<Integer> getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(ArrayList<Integer> subTaskId) {
        this.subTaskId = subTaskId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "epicId=" + epicId +
                ", subTaskId=" + subTaskId +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return epicId == epic.epicId && Objects.equals(subTaskId, epic.subTaskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId, subTaskId);
    }
}
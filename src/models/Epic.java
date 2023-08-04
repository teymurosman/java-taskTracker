package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        super.setType(TaskType.EPIC);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        super.setType(TaskType.EPIC);
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskIds(int id) {
        subtaskIds.add(id);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public void removeSubtaskById(int id) {
        subtaskIds.remove(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + id + "'" +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", status='" + status + "'" +
                ", subtaskIds=" + subtaskIds +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }
}

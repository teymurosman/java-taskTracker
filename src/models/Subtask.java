package models;

public class Subtask extends Task {
    private Epic epicOfSubtask;

    public Subtask(String name, String description, Epic epicOfSubtask) {
        super(name, description);
        this.epicOfSubtask = epicOfSubtask;
        putSubtaskToEpic();
    }

    public Subtask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic getEpicOfSubtask() {
        return epicOfSubtask;
    }

    public void setEpicOfSubtask(Epic epicOfSubtask) {
        this.epicOfSubtask = epicOfSubtask;
    }

    private void putSubtaskToEpic() {
        epicOfSubtask.getSubtasks().put(getId(), this);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id='" + getId() + "'" +
                ", name='" + getName() + "'" +
                ", description='" + getDescription() + "'" +
                ", status=" + getStatus() +
                "}";
    }
}

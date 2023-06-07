package service;

import models.*;

import java.util.HashMap;
public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

//    Взаимодействие с Task
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task createTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

//    Взаимодействие с Epic

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public void deleteAllEpics() {
        epics.clear();
    }

    public Epic getEpicById (int id) {
        return epics.get(id);
    }

    public Epic createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void updateEpic(Epic updatedEpic) {
        Epic epic = epics.get(updatedEpic.getId());
        updatedEpic.setSubtasks(epic.getSubtasks());
        epics.put(updatedEpic.getId(), updatedEpic);
    }

    public void deleteEpicById (int id) {
        epics.get(id).getSubtasks().clear();
        epics.remove(id);
    }

//    Взаимодействие с Subtask

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.setStatus(Status.NEW);
        }
    }

    public Subtask createSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        Status oldStatus = subtasks.get(id).getStatus();
        Epic epic = subtasks.get(id).getEpicOfSubtask();
        subtask.setEpicOfSubtask(epic);
        epic.getSubtasks().put(id, subtask);
        subtasks.put(id, subtask);

        if (oldStatus != subtask.getStatus()) {
            int numOfNewSubtasks = 0;
            int numOfDoneSubTasks = 0;
            for (Subtask epicSubtask : epic.getSubtasks().values()) {
                Status status = epicSubtask.getStatus();
                if (status == Status.NEW) {
                    numOfNewSubtasks++;
                }
                if (status == Status.DONE) {
                    numOfDoneSubTasks++;
                }
            }

            if (numOfNewSubtasks == epic.getSubtasks().size() || epic.getSubtasks().size() == 0) {
                epic.setStatus(Status.NEW);
            } else if (numOfDoneSubTasks == epic.getSubtasks().size()) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public void deleteSubtaskById(int id) {
        Epic epic = subtasks.get(id).getEpicOfSubtask();
        epic.getSubtasks().remove(id);
        if (epic.getSubtasks().size() == 0) {
            epic.setStatus(Status.NEW);
        }
        subtasks.remove(id);
    }

    public HashMap<Integer, Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }
}

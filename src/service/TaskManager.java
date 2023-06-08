package service;

import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class TaskManager {
    private int id;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int getIdManager() {
        return ++id;
    }

//    Взаимодействие с Task
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task createTask(Task task) {
        id = getIdManager();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

//    Взаимодействие с Epic

    private void calculateEpicStatus(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean onlyNewSubtasks = true;
        boolean onlyDoneSubtasks = true;

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Status status = subtasks.get(subtaskId).getStatus();
            if (status != Status.NEW) {
                onlyNewSubtasks = false;
            }
            if (status != Status.DONE) {
                onlyDoneSubtasks = false;
            }
        }

        if (onlyNewSubtasks) {
            epic.setStatus(Status.NEW);
        } else if (onlyDoneSubtasks) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public Epic getEpicById (int id) {
        return epics.get(id);
    }

    public Epic createEpic(Epic epic) {
        id = getIdManager();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epic.setSubtaskIds(epics.get(epic.getId()).getSubtaskIds());
            epics.put(epic.getId(), epic);
        }
    }

    public void deleteEpicById (int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

//    Взаимодействие с Subtask

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(Status.NEW);
        }
    }

    public Subtask createSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            return null;
        }
        id = getIdManager();
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskIds(subtask.getId());
        calculateEpicStatus(epic);
        return subtask;
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())
                && subtask.getEpicId() == subtasks.get(subtask.getId()).getEpicId()) {
            subtasks.put(subtask.getId(), subtask);
            calculateEpicStatus(epics.get(subtask.getEpicId()));
        }
    }

    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            epic.getSubtaskIds().remove((Integer) id);
            calculateEpicStatus(epic);
            subtasks.remove(id);
        }
    }

    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasksByEpic = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasksByEpic.add(subtasks.get(subtaskId));
        }
        return subtasksByEpic;
    }
}

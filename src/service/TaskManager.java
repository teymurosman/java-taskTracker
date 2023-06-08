package service;

import models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public int setIdManager() {
        return ++id;
    }

//    Взаимодействие с Task
    public List<Task> getAllTasks() {
        List<Task> tasksList = new ArrayList<>();
        for (Task task : tasks.values()) {
            tasksList.add(task);
        }
        return tasksList;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task createTask(Task task) {
        id = setIdManager();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        tasks.remove(id);
    }

//    Взаимодействие с Epic

    public void calculateEpicStatus(Epic epic) {
        if (epic.getSubtaskIds() == null || epic.getSubtaskIds().size() == 0) {
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

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public Epic getEpicById (int id) {
        return epics.get(id);
    }

    public Epic createEpic(Epic epic) {
        id = setIdManager();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    public void updateEpic(Epic epic) {
        epic.setSubtaskIds(epics.get(epic.getId()).getSubtaskIds());
        epics.put(epic.getId(), epic);
        calculateEpicStatus(epic);
    }

    public void deleteEpicById (int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        epics.get(id).getSubtaskIds().clear();
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

//    Взаимодействие с Subtask

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            subtasksList.add(subtask);
        }
        return subtasksList;
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
        id = setIdManager();
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskIds(subtask.getId());
        calculateEpicStatus(epic);
        return subtask;
    }

    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())
                || subtask.getEpicId() != subtasks.get(subtask.getId()).getEpicId()) {
            return;
        }
        int id = subtask.getId();
        subtask.setEpicId(subtasks.get(id).getEpicId());
        subtasks.put(id, subtask);
        calculateEpicStatus(epics.get(subtask.getEpicId()));
    }

    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            epic.getSubtaskIds().remove((Integer) id);
            calculateEpicStatus(epic);
            subtasks.remove(id);
        }
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                subtasksByEpic.add(subtask);
            }
        }
        return subtasksByEpic;
    }
}

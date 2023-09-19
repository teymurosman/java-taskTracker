package service;

import models.*;
import util.Managers;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int id;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private final Comparator<Task> comparatorByStartTime = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparingInt(Task::getId);
    protected Set<Task> prioritizedTasks = new TreeSet<>(comparatorByStartTime);

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int getIdManager() {
        return ++id;
    }

    protected void setStartingId(int startId) {
        id = startId;
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

//    Взаимодействие с Task

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (int id : tasks.keySet()) {
            inMemoryHistoryManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        inMemoryHistoryManager.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        checkIntersections(task);
        id = getIdManager();
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        checkIntersections(task);
        int id = task.getId();
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
            tasks.put(id, task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        inMemoryHistoryManager.remove(id);
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

    private void calculateEpicTime(Epic epic) {
        LocalDateTime minSubtaskStartTime;
        LocalDateTime maxSubtaskEndTime;

        List<Subtask> subtasksOfEpic = getSubtasksByEpicId(epic.getId());

        long duration = subtasksOfEpic.stream()
                .map(Subtask::getDuration)
                .mapToLong(Long::longValue)
                .sum();

        minSubtaskStartTime = subtasksOfEpic.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        maxSubtaskEndTime = subtasksOfEpic.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setDuration(duration);
        epic.setStartTime(minSubtaskStartTime);
        epic.setEndTime(maxSubtaskEndTime);
    }

    protected void calculateEpicFields(Epic epic) {
        calculateEpicTime(epic);
        calculateEpicStatus(epic);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        for (int id : epics.keySet()) {
            inMemoryHistoryManager.remove(id);
        }
        for (int id : subtasks.keySet()) {
            inMemoryHistoryManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicById (int id) {
        Epic epic = epics.get(id);
        inMemoryHistoryManager.add(epic);
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        id = getIdManager();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic newEpic) {
        final int id = newEpic.getId();
        if (epics.containsKey(id)) {
            final Epic oldEpic = epics.get(id);
            oldEpic.setName(newEpic.getName());
            oldEpic.setDescription(newEpic.getDescription());
        }
    }

    @Override
    public void deleteEpicById (int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
            inMemoryHistoryManager.remove(subtaskId);
        }
        epics.remove(id);
    }

//    Взаимодействие с Subtask

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        for (int id : subtasks.keySet()) {
            inMemoryHistoryManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(Status.NEW);
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(0);
        }
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            return null;
        }
        checkIntersections(subtask);
        id = getIdManager();
        subtask.setId(id);
        subtasks.put(id, subtask);
        prioritizedTasks.add(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskIds(subtask.getId());
        calculateEpicFields(epic);
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasks.containsKey(id)
                && subtask.getEpicId() == subtasks.get(id).getEpicId()) {
            checkIntersections(subtask);
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.put(id, subtask);
            prioritizedTasks.add(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            calculateEpicFields(epic);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            return;
        }
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getSubtaskIds().remove((Integer) id);
        calculateEpicFields(epic);
        prioritizedTasks.remove(subtasks.get(id));
        subtasks.remove(id);
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> subtasksByEpic = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasksByEpic.add(subtasks.get(subtaskId));
        }
        return subtasksByEpic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        inMemoryHistoryManager.add(subtask);
        return subtask;
    }

    private void checkIntersections(Task taskToCheck) {
        if (taskToCheck.getStartTime() != null) {
            for (Task task : prioritizedTasks) {
                if (task.getStartTime() == null) {
                    continue;
                }
                if (!(taskToCheck.getEndTime().isBefore(task.getStartTime()) || taskToCheck.getStartTime().isAfter(task.getEndTime()))) {
                    throw new IllegalStateException("Задача пересекается с другими задачами по времени");
                }
            }
        }
    }
}

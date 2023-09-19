package service;

import models.*;

import java.util.List;

public interface TaskManager {
    List<Task> getPrioritizedTasks();

    List<Task> getHistory();

//    Взаимодействие с Task

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int id);

//    Взаимодействие с Epic

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById (int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById (int id);

//    Взаимодействие с Subtask

    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    List<Subtask> getSubtasksByEpicId(int epicId);

    Subtask getSubtaskById(int id);
}

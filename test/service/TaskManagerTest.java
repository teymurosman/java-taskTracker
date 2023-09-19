package service;

import models.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task = new Task("task1", "description task1",
            LocalDateTime.of(2023, 9, 1, 12, 0), 60);
    protected Task task2 = new Task("task2", "description task2",
            LocalDateTime.of(2022, 5, 23, 15, 30), 150);
    protected Epic epic = new Epic("epic1", "description epic1");
    protected Epic epic2 = new Epic("epic2", "description epic2");
    protected Subtask subtask;
    protected Subtask subtask2;

    void setUpForSubtaskTests() {
        epic = taskManager.createEpic(epic);
        subtask = new Subtask("subtask1", "description subtask1", epic.getId(),
                LocalDateTime.of(2020, 12, 10, 14, 21), 7);
        subtask2 = new Subtask("subtask2", "description subtask2", epic.getId(),
                LocalDateTime.of(2023, 11, 15, 19, 45), 25);
    }
    @Test
    void createTaskStandardCase() {
        Task createdTask = taskManager.createTask(task);
        final int id = createdTask.getId();

        assertNotNull(createdTask, "Задача не создана");
        assertEquals(1, id, "Неверное присваивание id");
        assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    void createTwoTasksStandardCase() {
        Task createdTask = taskManager.createTask(task);
        Task createdTask2 = taskManager.createTask(task2);

        assertNotEquals(createdTask, createdTask2, "Созданы одинаковые задачи");
        assertEquals(2, taskManager.getAllTasks().size(), "Сохранено неверное количество задач");
    }

    @Test
    void getEmptyTaskList() {
        List<Task> tasksList = taskManager.getAllTasks();

        assertNotNull(tasksList, "Список задач - null");
        assertEquals(0, tasksList.size());
    }

    @Test
    void getTaskStandardCase() {
        task = taskManager.createTask(task);
        Task getTask = taskManager.getTaskById(task.getId());

        assertNotNull(getTask, "Возвращено значение null");
        assertEquals(task, getTask, "Вовзращена иная задача");
    }

    @Test
    void getTaskFromEmptyMap() {
        task.setId(1);
        Task getTask = taskManager.getTaskById(task.getId());

        assertNull(getTask, "Возвращённая задача - не null");
    }

    @Test
    void getTaskByWrongId() {
        taskManager.createTask(task);
        taskManager.createTask(task2);
        int id = 55;
        Task getTask = taskManager.getTaskById(id);

        assertNull(getTask);
    }

    @Test
    void updateTaskStandardCase() {
        task = taskManager.createTask(task);
        final int taskId = task.getId();
        Task updatedTask = new Task(taskId, "updated name", "updated description", Status.IN_PROGRESS,
                LocalDateTime.of(2021, 7, 18, 20, 0), 33);
        taskManager.updateTask(updatedTask);
        Task getUpdTask = taskManager.getTaskById(taskId);

        assertEquals(taskId, getUpdTask.getId(), "id не совпадают");
        assertNotEquals(task.getName(), getUpdTask.getName(), "Поле name не обновилось");
        assertNotEquals(task.getDescription(), getUpdTask.getDescription(), "Поле description не обновилось");
        assertNotEquals(task.getStatus(), getUpdTask.getStatus(), "Поле status не обновилось");
        assertNotEquals(task.getStartTime(), getUpdTask.getStartTime(), "Поле startTime не обновилось");
        assertNotEquals(task.getDuration(), getUpdTask.getDuration(), "Поле duration не обновилось");
    }

    @Test
    void updateTaskWithEmptyMap() {
        int taskId = 1;
        Task getUpdTask = taskManager.getTaskById(taskId);

        assertNull(getUpdTask, "Возвращённая задача - не null");
    }

    @Test
    void updateTaskWithWrongId() {
        task = taskManager.createTask(task);
        final int taskId = task.getId();
        final int wrongId = taskId + 55;
        Task updatedTask = new Task(wrongId, "updated Name", "updated description", Status.IN_PROGRESS,
                LocalDateTime.of(2021, 7, 18, 20, 0), 33);
        taskManager.updateTask(updatedTask);
        Task getUpdTask = taskManager.getTaskById(taskId);

        assertEquals(task, getUpdTask, "Задача была изменена");
    }

    @Test
    void deleteTaskStandardCase() {
        task = taskManager.createTask(task);
        final int taskId = task.getId();
        taskManager.deleteTaskById(taskId);
        Task getDeletedTask = taskManager.getTaskById(taskId);

        assertNull(getDeletedTask, "Возвращённая задача - не null");
        assertNotEquals(task, getDeletedTask, "Добавленная и полученная после удаления задачи равны");
        assertEquals(0, taskManager.getAllTasks().size(), "Список задач остался не пустым");
    }

    @Test
    void deleteTaskFromEmptyMap() {
        final int taskId = 1;
        task.setId(taskId);
        taskManager.deleteTaskById(taskId);
        Task getTaskFromEmptyMap = taskManager.getTaskById(taskId);

        assertNull(getTaskFromEmptyMap, "Возвращённая задача - не null");
        assertNotEquals(task, getTaskFromEmptyMap);
        assertEquals(0, taskManager.getAllTasks().size(), "Список задач стал не пустым");
    }

    @Test
    void deleteTaskByWrongId() {
        task = taskManager.createTask(task);
        task2 = taskManager.createTask(task2);
        final int wrongId = 555;
        taskManager.deleteTaskById(wrongId);

        assertEquals(2, taskManager.getAllTasks().size(), "Размер списка изменился");
        assertEquals(List.of(task, task2), taskManager.getAllTasks(), "Список задач изменился");
    }

    @Test
    void deleteAllTasksStandardCase() {
        task = taskManager.createTask(task);
        task2 = taskManager.createTask(task2);
        taskManager.deleteAllTasks();

        assertNull(taskManager.getTaskById(task.getId()), "Первая задача - не null");
        assertNull(taskManager.getTaskById(task2.getId()), "Вторая задача - не null");
        assertEquals(0, taskManager.getAllTasks().size(), "Список задач не пустой");
    }

    @Test
    void createEpicStandardCase() {
        Epic createdEpic = taskManager.createEpic(epic);
        final int id = createdEpic.getId();

        assertNotNull(createdEpic, "Эпик не создан");
        assertEquals(1, id, "Неверное присваивание id");
        assertEquals(1, taskManager.getAllEpics().size());
    }

    @Test
    void createTwoEpicsStandardCase() {
        epic = taskManager.createEpic(epic);
        epic2 = taskManager.createEpic(epic2);

        assertEquals(2, taskManager.getAllEpics().size(), "Размер списка эпиков не совпадает");
        assertEquals(List.of(epic, epic2), taskManager.getAllEpics(), "Списки эпиков не равны");
    }

    @Test
    void getEmptyEpicList() {
        List<Epic> epicList = taskManager.getAllEpics();

        assertNotNull(epicList, "Список эпиков - null");
        assertEquals(0, epicList.size(), "Список не пустой");
    }

    @Test
    void getEpicStandardCase() {
        epic = taskManager.createEpic(epic);
        Epic getEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(getEpic, "Возвращено null");
        assertEquals(epic, getEpic, "Добавленный и полученный эпики не равны");
    }

    @Test
    void getEpicFromEmptyMap() {
        final int epicId = 1;
        epic.setId(epicId);
        Epic getEpic = taskManager.getEpicById(epicId);

        assertNull(getEpic, "Возвращённый эпик - не null");
    }

    @Test
    void getEpicByWrongId() {
        epic = taskManager.createEpic(epic);
        final int wrongId = epic.getId() + 55;
        Epic getWrongEpic = taskManager.getEpicById(wrongId);

        assertNotEquals(epic, getWrongEpic, "Эпики равны");
        assertNull(getWrongEpic, "Возвращённый эпик - не null");
    }

    @Test
    void updateEpicStandardCase() {
        epic = taskManager.createEpic(epic);
        final int epicId = epic.getId();
        final String oldName = epic.getName();
        final String oldDescription = epic.getDescription();
        Epic updatedEpic = new Epic(epicId, "updated name", "updated description");
        taskManager.updateEpic(updatedEpic);
        final String newName = taskManager.getEpicById(epicId).getName();
        final String newDescription = taskManager.getEpicById(epicId).getDescription();

        assertNotEquals(oldName, newName, "Поле name не обновилось");
        assertNotEquals(oldDescription, newDescription, "Поле description не обновилось");
    }

    @Test
    void updateEpicWithEmptyMap() {
        int epicId = 1;
        Epic getUpdEpic = taskManager.getEpicById(epicId);

        assertNull(getUpdEpic, "Возвращённый эпик - не null");
    }

    @Test
    void updateEpicWithWrongId() {
        epic = taskManager.createEpic(epic);
        final int epicId = epic.getId();
        final int wrongId = epicId + 55;
        Epic updatedEpic = new Epic(wrongId, "updated name", "updated description");
        taskManager.updateEpic(updatedEpic);
        Epic getUpdEpic = taskManager.getEpicById(epicId);

        assertEquals(epic, getUpdEpic, "Эпик был изменен");
    }

    @Test
    void deleteEpicStandardCase() {
        epic = taskManager.createEpic(epic);
        final int epicId = epic.getId();
        taskManager.deleteEpicById(epicId);
        Epic getDeletedEpic = taskManager.getEpicById(epicId);

        assertNull(getDeletedEpic, "Возвращённый эпик - не null");
        assertNotEquals(epic, getDeletedEpic, "Добавленный и полученный эпики равны");
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков остался не пустым");
    }

    @Test
    void deleteEpicFromEmptyMap() {
        final int epicId = 1;
        epic.setId(epicId);
        taskManager.deleteEpicById(epicId);
        Epic getDeletedEpic = taskManager.getEpicById(epicId);

        assertNull(getDeletedEpic, "Возвращённый эпик - не null");
        assertNotEquals(epic, getDeletedEpic);
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не пустой");
    }

    @Test
    void deleteEpicByWrongId() {
        epic = taskManager.createEpic(epic);
        epic2 = taskManager.createEpic(epic2);
        final int wrongId = 555;
        taskManager.deleteEpicById(wrongId);

        assertEquals(2, taskManager.getAllEpics().size(), "Размер списка не совпадает");
        assertEquals(List.of(epic, epic2), taskManager.getAllEpics(), "Списки не равны");
    }

    @Test
    void deleteAllEpicsStandardCase() {
        epic = taskManager.createEpic(epic);
        epic2 = taskManager.createEpic(epic2);
        taskManager.deleteAllEpics();

        assertNull(taskManager.getEpicById(epic.getId()), "Первая задача не удалена");
        assertNull(taskManager.getEpicById(epic2.getId()), "Вторая задача не удалена");
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    void createSubtaskStandardCase() {
        setUpForSubtaskTests();
        Subtask createdSubtask = taskManager.createSubtask(subtask);
        final int id = createdSubtask.getId();

        assertNotNull(createdSubtask, "Подзадача не создана");
        assertEquals(2, id, "Неверное присваивание id");
        assertEquals(1, taskManager.getAllSubtasks().size());
    }

    @Test
    void createTwoSubtasksStandardCase() {
        setUpForSubtaskTests();
        Subtask createdSubtask = taskManager.createSubtask(subtask);
        Subtask createdSubtask2 = taskManager.createSubtask(subtask2);

        assertNotEquals(createdSubtask, createdSubtask2, "Созданы одинаковые задачи");
        assertEquals(2, taskManager.getAllSubtasks().size(), "Сохранено неверное количество подзадач");
    }

    @Test
    void getEmptySubtaskList() {
        List<Subtask> subtasksList = taskManager.getAllSubtasks();

        assertNotNull(subtasksList, "Список подзадач - null");
        assertEquals(0, subtasksList.size());
    }

    @Test
    void getSubtaskStandardCase() {
        setUpForSubtaskTests();
        subtask.setEpicId(epic.getId());
        subtask = taskManager.createSubtask(subtask);
        Subtask getSubtask = taskManager.getSubtaskById(subtask.getId());

        assertNotNull(getSubtask, "Возвращено значение null");
        assertEquals(subtask, getSubtask, "Вовзращена иная подзадача");
    }

    @Test
    void getSubtaskFromEmptyMap() {
        setUpForSubtaskTests();
        Subtask getSubtask = taskManager.getSubtaskById(subtask.getId());

        assertNull(getSubtask, "Возвращённая подзадача - не null");
    }

    @Test
    void getSubtaskByWrongId() {
        setUpForSubtaskTests();
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
        int id = 55;
        Subtask getSubtask = taskManager.getSubtaskById(id);

        assertNull(getSubtask);
    }

    @Test
    void updateSubtaskStandardCase() {
        setUpForSubtaskTests();
        subtask = taskManager.createSubtask(subtask);
        final int subtaskId = subtask.getId();
        Subtask updatedSubtask = new Subtask(subtaskId, "updated name", "updated description",
                Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2021, 7, 18, 20, 0), 33);
        taskManager.updateSubtask(updatedSubtask);
        Subtask getUpdSubtask = taskManager.getSubtaskById(subtaskId);

        assertEquals(subtaskId, getUpdSubtask.getId(), "id не совпадают");
        assertNotEquals(subtask.getName(), getUpdSubtask.getName(), "Поле name не обновилось");
        assertNotEquals(subtask.getDescription(), getUpdSubtask.getDescription(), "Поле description не обновилось");
        assertNotEquals(subtask.getStatus(), getUpdSubtask.getStatus(), "Поле status не обновилось");
        assertNotEquals(subtask.getStartTime(), getUpdSubtask.getStartTime(), "Поле startTime не обновилось");
        assertNotEquals(subtask.getDuration(), getUpdSubtask.getDuration(), "Поле duration не обновилось");
        assertEquals(subtask.getEpicId(), getUpdSubtask.getEpicId(), "epicId не совпадают");
    }

    @Test
    void updateSubtaskWithEmptyMap() {
        int subtaskId = 1;
        Subtask getUpdSubtask = taskManager.getSubtaskById(subtaskId);

        assertNull(getUpdSubtask, "Возвращённая подзадача - не null");
    }

    @Test
    void updateSubtaskWithWrongId() {
        setUpForSubtaskTests();
        subtask = taskManager.createSubtask(subtask);
        final int subtaskId = subtask.getId();
        final int wrongId = subtaskId + 55;
        Subtask updatedSubtask = new Subtask(wrongId, "updated Name", "updated description",
                Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2021, 7, 18, 20, 0), 33);
        taskManager.updateSubtask(updatedSubtask);
        Subtask getUpdSubtask = taskManager.getSubtaskById(subtaskId);

        assertEquals(subtask, getUpdSubtask, "Задача была изменена");
    }

    @Test
    void deleteSubtaskStandardCase() {
        setUpForSubtaskTests();
        subtask = taskManager.createSubtask(subtask);
        final int subtaskId = subtask.getId();
        taskManager.deleteSubtaskById(subtaskId);
        Subtask getDeletedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNull(getDeletedSubtask, "Возвращённая подзадача - не null");
        assertNotEquals(subtask, getDeletedSubtask, "Добавленная и полученная после удаления подзадачи равны");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач остался не пустым");
    }

    @Test
    void deleteSubtaskFromEmptyMap() {
        setUpForSubtaskTests();
        final int subtaskId = 5;
        subtask.setId(subtaskId);
        taskManager.deleteSubtaskById(subtaskId);
        Subtask getSubtaskFromEmptyMap = taskManager.getSubtaskById(subtaskId);

        assertNull(getSubtaskFromEmptyMap, "Возвращённая подзадача - не null");
        assertNotEquals(task, getSubtaskFromEmptyMap);
        assertEquals(0, taskManager.getAllTasks().size(), "Список подзадач стал не пустым");
    }

    @Test
    void deleteSubtaskByWrongId() {
        setUpForSubtaskTests();
        subtask = taskManager.createSubtask(subtask);
        subtask2 = taskManager.createSubtask(subtask2);
        final int wrongId = 555;
        taskManager.deleteSubtaskById(wrongId);

        assertEquals(2, taskManager.getAllSubtasks().size(), "Размер списка изменился");
        assertEquals(List.of(subtask, subtask2), taskManager.getAllSubtasks(), "Список подзадач изменился");
    }

    @Test
    void deleteAllSubtasksStandardCase() {
        setUpForSubtaskTests();
        subtask = taskManager.createSubtask(subtask);
        subtask2 = taskManager.createSubtask(subtask2);
        taskManager.deleteAllSubtasks();

        assertNull(taskManager.getSubtaskById(subtask.getId()), "Первая подзадача - не null");
        assertNull(taskManager.getSubtaskById(subtask2.getId()), "Вторая подзадача - не null");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не пустой");
    }

    @Test
    void getSubtasksByEpicListStandardCase() {
        setUpForSubtaskTests();
        taskManager.createEpic(epic2);
        subtask = taskManager.createSubtask(subtask); // epic
        subtask2 = taskManager.createSubtask(subtask2); // epic
        taskManager.createSubtask(new Subtask("subtask3", "description subtask3", epic2.getId())); // epic2
        List<Subtask> subtasksByEpic = taskManager.getSubtasksByEpicId(epic.getId());

        assertEquals(2, subtasksByEpic.size(), "Размеры списков не совадают");
        assertEquals(List.of(subtask, subtask2), subtasksByEpic, "Списки не совпадают");
    }

    @Test
    void getSubtasksByEpicEmptyList() {
        setUpForSubtaskTests();
        taskManager.createEpic(epic2);
        subtask = taskManager.createSubtask(subtask); // epic
        subtask2 = taskManager.createSubtask(subtask2); // epic
        List<Subtask> subtasksByEpic = taskManager.getSubtasksByEpicId(epic2.getId());

        assertEquals(0, subtasksByEpic.size(), "Список не пустой");
    }

    @Test
    void getPrioritizedTasksStandardCase() {
        setUpForSubtaskTests();
        taskManager.createTask(task); // 12:00 2023-09-01 (3)
        taskManager.createTask(task2); // 15:30 2022-05-23 (2)
        Task task3 = new Task("task with time null", "description a");
        taskManager.createTask(task3); // (5)
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask); // 14:21 2020-12-10 (1)
        taskManager.createSubtask(subtask2); // 19:45 2023-11-15 (4)
        Subtask subtask3 = new Subtask("subtask with time null", "description b", epic2.getId());
        taskManager.createSubtask(subtask3); // (6)
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(6, prioritizedTasks.size(), "Размер множества не совпадает");
        assertEquals(List.of(subtask, task2, task, subtask2, task3, subtask3), prioritizedTasks);
    }
}
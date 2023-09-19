package service;

import models.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    final String filename = "test_tasks.csv";
    FileBackedTaskManager loadedTaskManager;

    @BeforeEach
    void setUpFileBackedTaskManager() {
        taskManager = new FileBackedTaskManager(filename);
    }

    void setUpLoadedTaskManager() {
        loadedTaskManager = FileBackedTaskManager.loadFromFile(new File(filename));
    }

    @Test
    void loadFromFileNoTasks() {
        loadedTaskManager = FileBackedTaskManager.loadFromFile(new File("empty_file.csv"));

        assertEquals(0, loadedTaskManager.getAllTasks().size(), "Присутствуют задачи");
        assertEquals(0, loadedTaskManager.getAllEpics().size(), "Присутствуют эпики");
        assertEquals(0, loadedTaskManager.getAllSubtasks().size(), "Присутствуют подзадачи");
    }

    @Test
    void loadFromFileStandardCase() {
        taskManager.createTask(task); // startTime: 2023, 9, 1, 12, 0 (3)
        taskManager.createTask(task2); // startTime: 2022, 5, 23, 15, 30) (2)
        setUpForSubtaskTests();
        taskManager.createSubtask(subtask); // epic; startTime: 2020, 12, 10, 14, 21 (1)
        taskManager.createSubtask(subtask2); // epic; startTime 2023, 11, 15, 19, 45 (4)
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("subtask3", "description subtask3", epic2.getId());
        taskManager.createSubtask(subtask3); // epic2; startTime: null (5)
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getTaskById(task.getId());
        setUpLoadedTaskManager();

        assertEquals(List.of(task2, subtask2, epic, task), loadedTaskManager.getHistory(),
                "Список истории просмотров не совпадает");
        assertEquals(task, loadedTaskManager.getTaskById(task.getId()), "task не совпадает");
        assertEquals(task2.getStartTime(), loadedTaskManager.getTaskById(task2.getId()).getStartTime(),
                "startTime у task2 не совпадает");
        assertEquals(List.of(subtask, subtask2), loadedTaskManager.getSubtasksByEpicId(epic.getId()),
                "Неверно перезаписаны подзадачи по принадлежности к эпику");
        assertEquals(List.of(task, task2), loadedTaskManager.getAllTasks(), "Задачи не совпадают");
        assertEquals(List.of(epic, epic2), loadedTaskManager.getAllEpics(), "Эпик не совпадает");
        assertEquals(List.of(subtask, subtask2, subtask3), loadedTaskManager.getAllSubtasks(),
                "Подзадачи не совпадают");
        assertEquals(5, loadedTaskManager.getPrioritizedTasks().size(),
                "Размер набора по приоритету не совпадает");
        assertEquals(List.of(subtask, task2, task, subtask2, subtask3), loadedTaskManager.getPrioritizedTasks(),
                "Набор по приоритету не совпадает");
    }

    @Test
    void loadFromFileEmptyEpic() {
        taskManager.createTask(task);
        setUpForSubtaskTests();
        taskManager.createSubtask(subtask);
        taskManager.createEpic(epic2); // Эпик без подзадач
        setUpLoadedTaskManager();

        assertEquals(Collections.emptyList(), loadedTaskManager.getEpicById(epic2.getId()).getSubtaskIds(),
                "У эпика присутствуют подзадачи");
        assertEquals(epic2, loadedTaskManager.getEpicById(epic2.getId()), "Эпики не совпадают");
    }

    @Test
    void loadFromFileEmptyHistory() {
        taskManager.createTask(task);
        setUpForSubtaskTests();
        taskManager.createSubtask(subtask);
        setUpLoadedTaskManager();

        assertEquals(Collections.emptyList(), loadedTaskManager.getHistory(),
                "Список истории просмотров не пустой");
    }
}
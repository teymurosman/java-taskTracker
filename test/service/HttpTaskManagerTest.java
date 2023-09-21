package service;

import api.KVServer;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static final String HOST = "http://localhost";
    private KVServer kvServer;

    @BeforeEach
    public void setUpHttpManager() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskManager = Managers.getDefault(HOST);
        } catch (IOException e) {
            System.out.println("Ошибка при попытке запуска сервера");
        }
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    public void loadFromServerStandardCase() {
        Task task1 = taskManager.createTask(new Task("task1", "task1 description"));
        Task task2 = taskManager.createTask(new Task("task2", "task2 description",
                LocalDateTime.of(2023, 11, 18, 12, 0), 30));
        Epic epic1 = taskManager.createEpic(new Epic("epic1", "epic1 description"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("subtask1", "subtask1 description",
                epic1.getId(), LocalDateTime.of(2021, 2, 22, 15, 45), 180));
        Epic epic2 = taskManager.createEpic(new Epic("epic2", "epic2 description"));
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic1.getId());
        HttpTaskManager loadedManager = HttpTaskManager.load(HOST);

        assertEquals(3, loadedManager.getHistory().size());
        assertEquals(List.of(task2, subtask1, epic1), loadedManager.getHistory());
        assertEquals(3, loadedManager.getPrioritizedTasks().size());
        assertEquals(List.of(subtask1, task2, task1), loadedManager.getPrioritizedTasks());
        assertEquals(2, loadedManager.getAllTasks().size());
        assertEquals(task1, loadedManager.getTaskById(task1.getId()));
        assertEquals(epic1.getSubtaskIds(), loadedManager.getEpicById(epic1.getId()).getSubtaskIds());
        assertEquals(List.of(epic1, epic2), loadedManager.getAllEpics());
    }

    @Test
    public void loadFromServerNoTasks() {
        Task task1 = taskManager.createTask(new Task("task1", "task1 description"));
        taskManager.deleteTaskById(task1.getId());

        HttpTaskManager loadedManager =  HttpTaskManager.load(HOST);

        assertEquals(0, loadedManager.getPrioritizedTasks().size());
        assertEquals(0, loadedManager.getAllTasks().size());
    }
}
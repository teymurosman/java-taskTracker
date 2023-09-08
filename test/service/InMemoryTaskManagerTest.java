package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUpInMemoryTaskManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void getEpicOfSubtask() {
        setUpForSubtaskTests();
        taskManager.createSubtask(subtask);

        assertEquals(epic.getId(), taskManager.getSubtaskById(subtask.getId()).getEpicId(),
                "epicId не совпадает");
    }
}
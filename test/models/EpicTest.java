package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    TaskManager manager;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;
    Status epicStatus;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
        epic = manager.createEpic(new Epic("epic1", "description epic2"));
        subtask1 = manager.createSubtask(new Subtask("subtask1", "subtask1 description",
                epic.getId()));
        subtask2 = manager.createSubtask(new Subtask("subtask2", "subtask2 description",
                epic.getId()));
    }

    @Test
    public void shouldHaveStatusNewWithNoSubtasks() {
        Epic epic2 = manager.createEpic(new Epic("epic2", "description epic2"));
        epicStatus = manager.getEpicById(epic2.getId()).getStatus();

        assertEquals(Status.NEW, epicStatus);
    }

    @Test
    public void shouldHaveStatusNewWithNewSubtasks() {
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        epicStatus = manager.getEpicById(epic.getId()).getStatus();

        assertEquals(Status.NEW, epicStatus);
    }

    @Test
    public void shouldHaveStatusDoneWithAllDoneSubtasks() {
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        epicStatus = manager.getEpicById(epic.getId()).getStatus();

        assertEquals(Status.DONE, epicStatus);
    }

    @Test
    public void shouldHaveInProgressWithNewAndDoneSubtasks() {
        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        epicStatus = manager.getEpicById(epic.getId()).getStatus();

        assertEquals(Status.IN_PROGRESS, epicStatus);
    }

    @Test
    public void shouldHaveInProgressWithAllInProgressSubtasks() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        epicStatus = manager.getEpicById(epic.getId()).getStatus();

        assertEquals(Status.IN_PROGRESS, epicStatus);
    }
}
package service;

import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    Task task1;
    Task task2;
    Task task3;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task(1, "task1", "description 1");
        task2 = new Task(2, "task2", "description 2");
        task3 = new Task(3, "task3", "description 3");
    }

    @Test
    void shouldGetEmptyNotNullList() {
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "Список истории является null");
        assertTrue(history.isEmpty(), "История не пустая");
    }

    @Test
    void shouldHaveOneElementInHistory() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void shouldHaveOneElementWhenAddingDuplicates() {
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
    }

    @Test
    void shouldHaveTwoElementsInHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
    }

    @Test
    void shouldRemoveFirstElement() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task2, task3), history);
    }

    @Test
    void shouldRemoveMiddleElement() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task1, task3), history);
    }

    @Test
    void shouldRemoveLastElement() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task1, task2), history);
    }

    @Test
    void shouldDoNothingWhenRemovingElementNotFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        int idNotFromHistory = 55;
        historyManager.remove(idNotFromHistory);
        List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task1, task2, task3), history);
    }
}
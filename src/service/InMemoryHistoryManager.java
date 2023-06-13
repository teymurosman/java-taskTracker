package service;

import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyOfViewedTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyOfViewedTasks.size() >= 10) {
                historyOfViewedTasks.remove(0);
            }
            historyOfViewedTasks.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyOfViewedTasks);
    }

}

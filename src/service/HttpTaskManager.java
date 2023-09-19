package service;

import api.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.Epic;
import models.Subtask;
import models.Task;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager{
    private static KVTaskClient kvTaskClient;
    private static Gson gson;
    public HttpTaskManager(String host) {
        super(null);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        kvTaskClient = new KVTaskClient(host);
    }

    public static HttpTaskManager load(String url) {
        HttpTaskManager manager = new HttpTaskManager(url);
        try {
            int startId = 0;
            String loadedTasks = kvTaskClient.load("/tasks/task");
            if (loadedTasks != null) {
                List<Task> tasksList = gson.fromJson(loadedTasks, new TypeToken<List<Task>>() {}.getType());
                for (Task task : tasksList) {
                    manager.createTask(task);
                    if (task.getId() > startId) {
                        startId = task.getId();
                    }
                }
            }
            String loadedEpics = kvTaskClient.load("/tasks/epic");
            if (loadedTasks != null) {
                List<Epic> epicsList = gson.fromJson(loadedEpics, new TypeToken<List<Epic>>() {}.getType());
                for (Epic epic : epicsList) {
                    manager.createEpic(epic);
                    if (epic.getId() > startId) {
                        startId = epic.getId();
                    }
                }
            }
            String  loadedSubtasks = kvTaskClient.load("/tasks/subtask");
            if (loadedSubtasks != null) {
                List<Subtask> subtasksList = gson.fromJson(loadedSubtasks, new TypeToken<List<Subtask>>() {}
                        .getType());
                for (Subtask subtask : subtasksList) {
                    manager.createSubtask(subtask);
                    if (subtask.getId() > startId) {
                        startId = subtask.getId();
                    }
                }
            }
            String loadedHistory = kvTaskClient.load("/tasks/history");
            if (loadedHistory != null) {
                List<Task> historyList = gson.fromJson(loadedHistory, new TypeToken<List<Task>>() {}.getType());
                for (Task task : historyList) {
                    manager.inMemoryHistoryManager.add(task);
                }
            }

            manager.setStartingId(startId);
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке менеджера задач");
        }
        return manager;
    }

    @Override
    protected void save() {
        kvTaskClient.put("tasks/task", gson.toJson(tasks));
        kvTaskClient.put("tasks/epic", gson.toJson(epics));
        kvTaskClient.put("tasks/subtask", gson.toJson(subtasks));
        kvTaskClient.put("tasks/history", gson.toJson(getHistory()));
    }
}

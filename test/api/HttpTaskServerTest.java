package api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;
import util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    /**
     * Разрешите, пожалуйста, дописать оставшиеся тесты позже. Большую часть я написал, а также всё тестировал в
     * инсомнии. Просто дедлайн горит, но я их точно допишу полностью
     */
    private final String url = "http://localhost:8080";
    private TaskManager taskManager;
    private HttpTaskServer httpTaskServer;
    private final Gson gson = Managers.getGsonWithAdapters();

    @BeforeEach
    public void setUpHttpTaskServer() throws IOException, InterruptedException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    public void stopServer() {
        httpTaskServer.stop();
    }

    @Test
    public void testsForTask() throws IOException, InterruptedException {
//        Создадим задачу
        Task task1 = new Task("task1", "task1 description");
        String task1Json = gson.toJson(task1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(task1Json);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestCreate = HttpRequest.newBuilder()
                .POST(body)
                .uri(URI.create(url + "/tasks/task"))
                .build();
        HttpResponse<String> response = client.send(requestCreate, HttpResponse.BodyHandlers.ofString());

        final int id = taskManager.getAllTasks().get(0).getId();

        assertEquals(201, response.statusCode());
        assertEquals("Задача успешно создана", response.body());
        assertEquals(task1.getName(), taskManager.getTaskById(id).getName());
        assertEquals(task1.getDescription(), taskManager.getTaskById(id).getDescription());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());


//        Обновим задачу
        Task updTask = new Task(id, "updated name", "updated description", Status.IN_PROGRESS,
                LocalDateTime.of(2023, 9, 10, 11, 12), 25);
        String updJson = gson.toJson(updTask);
        HttpRequest.BodyPublisher updBody = HttpRequest.BodyPublishers.ofString(updJson);
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .POST(updBody)
                .uri(URI.create(url + "/tasks/task?id=" + id))
                .build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseUpdate.statusCode());
        assertEquals("Задача успешно обновлена", responseUpdate.body());
        assertEquals(updTask, taskManager.getTaskById(id));
        assertEquals(1, taskManager.getAllTasks().size());


//        Создадим 2 задачу
        Task task2 = new Task("task1", "task1 description");
        String task2Json = gson.toJson(task2);
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(task2Json);

        HttpRequest requestCreate2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(URI.create(url + "/tasks/task"))
                .build();
        HttpResponse<String> response2 = client.send(requestCreate2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());
        assertEquals("Задача успешно создана", response2.body());
        assertEquals(2, taskManager.getAllTasks().size());


//        Получим 1 задачу по id
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/tasks/task?id=" + id))
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGet.statusCode());
        assertEquals(updJson, responseGet.body());


//        Удалим 1 задачу
        HttpRequest requestDelete = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(url + "/tasks/task?id=" + id))
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode());
        assertEquals("Задача успешно удалена", responseDelete.body());
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertNull(taskManager.getTaskById(id));


//        Удалим все задачи
        HttpRequest requestDeleteAll = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(url + "/tasks/task"))
                .build();
        HttpResponse<String> responseDeleteAll = client.send(requestDeleteAll, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDeleteAll.statusCode());
        assertEquals("Задачи успешно удалены", responseDeleteAll.body());
        assertEquals(0, taskManager.getAllTasks().size());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertNull(taskManager.getTaskById(id));
        assertNull(taskManager.getTaskById(2));
    }

    @Test
    public void createEpicAndSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1 description");
        String epic1Json = gson.toJson(epic1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epic1Json);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(URI.create(url + "/tasks/epic"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final int epicId = taskManager.getAllEpics().get(0).getId();

        assertEquals(201, response.statusCode());
        assertEquals("Эпик успешно создан", response.body());
        assertEquals(epic1.getName(), taskManager.getEpicById(epicId).getName());
        assertEquals(epic1.getDescription(), taskManager.getEpicById(epicId).getDescription());
        assertEquals(0, taskManager.getPrioritizedTasks().size());

        Subtask subtask1 = new Subtask("subtask1", "subtask1 description", epicId);
        subtask1.setStatus(Status.IN_PROGRESS);
        String subtask1Json = gson.toJson(subtask1);
        HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(subtask1Json);

        HttpRequest requestSubtask = HttpRequest.newBuilder()
                .POST(bodySubtask)
                .uri(URI.create(url + "/tasks/subtask"))
                .build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());

        final int subtaskId = taskManager.getAllSubtasks().get(0).getId();
        subtask1.setId(subtaskId);

        assertEquals(201, responseSubtask.statusCode());
        assertEquals("Подзадача успешно создана", responseSubtask.body());
        assertEquals(subtask1, taskManager.getSubtaskById(subtaskId));
        assertEquals(epicId, taskManager.getSubtaskById(subtaskId).getEpicId());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epicId).getStatus());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1 description");
        String task1Json = gson.toJson(task1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(task1Json);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestCreate = HttpRequest.newBuilder()
                .POST(body)
                .uri(URI.create(url + "/tasks/task"))
                .build();
        HttpResponse<String> response = client.send(requestCreate, HttpResponse.BodyHandlers.ofString());

        final int id = taskManager.getAllTasks().get(0).getId();

//        Получим 1 задачу по id
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/tasks/task?id=" + id))
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

//        Создадим 2 задачу
        Task task2 = new Task("task1", "task1 description");
        String task2Json = gson.toJson(task2);
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(task2Json);

        HttpRequest requestCreate2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(URI.create(url + "/tasks/task"))
                .build();
        HttpResponse<String> response2 = client.send(requestCreate2, HttpResponse.BodyHandlers.ofString());

//        Создадим эпик
        Epic epic1 = new Epic("epic1", "epic1 description");
        String epic1Json = gson.toJson(epic1);
        HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(epic1Json);

        HttpRequest requestEpic = HttpRequest.newBuilder()
                .POST(bodyEpic)
                .uri(URI.create(url + "/tasks/epic"))
                .build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        final int epicId = taskManager.getAllEpics().get(0).getId();

//        Получим эпик по id
        HttpRequest requestGetEpic = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/tasks/epic?id=" + epicId))
                .build();
        HttpResponse<String> responseGetEpic = client.send(requestGetEpic, HttpResponse.BodyHandlers.ofString());

//        Отправим запрос на получение истории
        HttpRequest requestHistory = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/tasks/history"))
                .build();
        HttpResponse<String> responseHistory = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArrayHistory = JsonParser.parseString(responseHistory.body()).getAsJsonArray();

        assertEquals(200, responseHistory.statusCode());
        assertEquals(2, jsonArrayHistory.size());
    }

}
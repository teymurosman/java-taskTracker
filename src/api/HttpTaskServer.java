package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import models.Epic;
import models.Subtask;
import models.Task;
import util.LocalDateTimeAdapter;
import util.Managers;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final HttpServer httpServer;
    private final Gson gson;

    public HttpTaskServer(String host) throws IOException, InterruptedException {
        taskManager = Managers.getHttpTaskManager(host);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
    }

    private class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String tasks = "/tasks/task";
            String subtasks = "/tasks/subtask";
            String epics = "/tasks/epic";
            String history = "/tasks/history";
            String subtasksByEpic = "/tasks/subtask/epic";

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            if (path.contains("/tasks")) {
                System.out.println("Обработка запроса от клиента");
                String method = exchange.getRequestMethod();
                String response;
                switch (method) {
                    case "GET":
                        if (path.endsWith("/tasks")) {
                            System.out.println("Обработка запроса на получение приоритизированного списка всех задач");
                            exchange.sendResponseHeaders(200, 0);
                            response = gson.toJson(taskManager.getPrioritizedTasks());
                            try (OutputStream outputStream = exchange.getResponseBody()) {
                                outputStream.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        } else if (path.endsWith(tasks)) {
                            System.out.println("Обработка запроса на получение списка задач");
                            exchange.sendResponseHeaders(200, 0);
                            response = gson.toJson(taskManager.getAllTasks());
                            try (OutputStream outputStream = exchange.getResponseBody()) {
                                outputStream.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        } else if (path.endsWith(subtasks)) {
                            System.out.println("Обработка запроса на получение списка подзадач");
                            exchange.sendResponseHeaders(200, 0);
                            response = gson.toJson(taskManager.getAllSubtasks());
                            try (OutputStream outputStream = exchange.getResponseBody()) {
                                outputStream.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        } else if (path.endsWith(epics)) {
                            System.out.println("Обработка запроса на получение списка эпиков");
                            exchange.sendResponseHeaders(200, 0);
                            response = gson.toJson(taskManager.getAllEpics());
                            try (OutputStream outputStream = exchange.getResponseBody()) {
                                outputStream.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        } else if (path.endsWith(history)) {
                            System.out.println("Обработка запроса на получение списка истории просмотров");
                            exchange.sendResponseHeaders(200, 0);
                            response = gson.toJson(taskManager.getHistory());
                            try (OutputStream outputStream = exchange.getResponseBody()) {
                                outputStream.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        } else {
                            System.out.println("Обработка запроса поиска");
                            final int id = Integer.parseInt(query.substring(query.indexOf('=') + 1));
                            if (path.endsWith(tasks + "/")) {
                                System.out.println("Обработка запроса поиска задачи с id = " + id);
                                exchange.sendResponseHeaders(200, 0);
                                response = gson.toJson(taskManager.getTaskById(id));
                                try (OutputStream outputStream = exchange.getResponseBody()) {
                                    outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else if (path.endsWith(subtasks + "/")) {
                                System.out.println("Обработка запроса поиска подзадачи с id = " + id);
                                exchange.sendResponseHeaders(200, 0);
                                response = gson.toJson(taskManager.getSubtaskById(id));
                                try (OutputStream outputStream = exchange.getResponseBody()) {
                                    outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else if (path.endsWith(epics + "/")) {
                                System.out.println("Обработка запроса поиска эпика с id = " + id);
                                exchange.sendResponseHeaders(200, 0);
                                response = gson.toJson(taskManager.getEpicById(id));
                                try (OutputStream outputStream = exchange.getResponseBody()) {
                                    outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else if (path.endsWith(subtasksByEpic + "/")) {
                                System.out.println("Обработка запроса поиска подзадачи по эпику с id = " + id);
                                exchange.sendResponseHeaders(200, 0);
                                response = gson.toJson(taskManager.getSubtasksByEpicId(id));
                                try (OutputStream outputStream = exchange.getResponseBody()) {
                                    outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else {
                                exchange.sendResponseHeaders(404, 0);
                                System.out.println("Не удалось найти задачу с id = " + id);
                            }
                        }
                        break;
                    case "POST":
                        InputStream inputStream = exchange.getRequestBody();
                        if (inputStream != null) {
                            String requestBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                            if (query == null) {
                                if (path.endsWith(tasks)) {
                                    System.out.println("Обработка запроса создания задачи");
                                    taskManager.createTask(gson.fromJson(requestBody, Task.class));
                                    exchange.sendResponseHeaders(201, 0);
                                    response = "Задача успешно создана";
                                    try (OutputStream outputStream = exchange.getResponseBody()) {
                                        outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                    }
                                } else if (path.endsWith(subtasks)) {
                                    System.out.println("Обработка запроса создания подзадачи");
                                    taskManager.createSubtask(gson.fromJson(requestBody, Subtask.class));
                                    exchange.sendResponseHeaders(201, 0);
                                    response = "Подзадача успешно создана";
                                    try (OutputStream outputStream = exchange.getResponseBody()) {
                                        outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                    }
                                } else if (path.endsWith(epics)) {
                                    System.out.println("Обработка запроса создания эпика");
                                    taskManager.createEpic(gson.fromJson(requestBody, Epic.class));
                                    exchange.sendResponseHeaders(201, 0);
                                    response = "Эпик успешно создан";
                                    try (OutputStream outputStream = exchange.getResponseBody()) {
                                        outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                    }
                                } else {
                                    exchange.sendResponseHeaders(400, 0);
                                    System.out.println("Не удалось обработать запрос");
                                }
                            } else {
                                final int id = Integer.parseInt(query.substring(query.indexOf('=') + 1));
                                if (path.endsWith(tasks + "/")) {
                                    System.out.println("Обработка запроса обновления задачи с id = " + id);
                                    taskManager.updateTask(gson.fromJson(requestBody, Task.class));
                                    exchange.sendResponseHeaders(201, 0);
                                    response = "Задача успешно обновлена";
                                    try (OutputStream outputStream = exchange.getResponseBody()) {
                                        outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                    }
                                } else if (path.endsWith(subtasks + "/")) {
                                    System.out.println("Обработка запроса обновления подзадачи с id = " + id);
                                    taskManager.updateSubtask(gson.fromJson(requestBody, Subtask.class));
                                    exchange.sendResponseHeaders(201, 0);
                                    response = "Подзадача успешно обновлена";
                                    try (OutputStream outputStream = exchange.getResponseBody()) {
                                        outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                    }
                                } else if (path.endsWith(epics + "/")) {
                                    System.out.println("Обработка запроса обновления эпика с id = " + id);
                                    taskManager.updateEpic(gson.fromJson(requestBody, Epic.class));
                                    exchange.sendResponseHeaders(201, 0);
                                    response = "Эпик успешно обновлен";
                                    try (OutputStream outputStream = exchange.getResponseBody()) {
                                        outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                    }
                                } else {
                                    exchange.sendResponseHeaders(404, 0);
                                    System.out.println("Не удалось найти задачу с id = " + id);
                                }
                            }
                        } else {
                            exchange.sendResponseHeaders(400, 0);
                            System.out.println("Пустое тело запроса");
                        }
                        break;
                    case "DELETE":
                        if (query == null) {
                            if (path.endsWith(tasks)) {
                                System.out.println("Обработка запроса удаления задач");
                                exchange.sendResponseHeaders(200, 0);
                                taskManager.deleteAllTasks();
                                response = "Задачи успешно удалены";
                                try (OutputStream outputStream = exchange.getResponseBody()) {
                                    outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else if (path.endsWith(subtasks)) {
                                System.out.println("Обработка запроса удаления подзадач");
                                exchange.sendResponseHeaders(200, 0);
                                taskManager.deleteAllSubtasks();
                                response = "Подзадачи успешно удалены";
                                try (OutputStream outputStream = exchange.getResponseBody()) {
                                    outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else if (path.endsWith(epics)) {
                                System.out.println("Обработка запроса удаления эпиков");
                                exchange.sendResponseHeaders(200, 0);
                                taskManager.deleteAllEpics();
                                response = "Эпики успешно удалены";
                                try (OutputStream outputStream = exchange.getResponseBody()) {
                                    outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else {
                                System.out.println("Не удалось обработать запрос");
                                exchange.sendResponseHeaders(400, 0);
                            }
                        } else {
                            final int id = Integer.parseInt(query.substring(query.indexOf('=') + 1));
                            if (path.endsWith(tasks + "/")) {
                                System.out.println("Обработка запроса удаления задачи с id = " + id);
                                exchange.sendResponseHeaders(200, 0);
                                taskManager.deleteTaskById(id);
                                response = "Задача успешно удалена";
                                try (OutputStream outputStream = exchange.getResponseBody()) {
                                    outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else if (path.endsWith(subtasks + "/")) {
                                System.out.println("Обработка запроса удаления подзадачи с id = " + id);
                                exchange.sendResponseHeaders(200, 0);
                                taskManager.deleteSubtaskById(id);
                                response = "Подзадача успешно удалена";
                                try (OutputStream outputStream = exchange.getResponseBody()) {
                                    outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else if (path.endsWith(epics + "/")) {
                                System.out.println("Обработка запроса удаления эпика с id = " + id);
                                exchange.sendResponseHeaders(200, 0);
                                taskManager.deleteEpicById(id);
                                response = "Эпик успешно удален";
                                try (OutputStream outputStream = exchange.getResponseBody()) {
                                    outputStream.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else {
                                System.out.println("Не удалось обработать запрос");
                                exchange.sendResponseHeaders(400, 0);
                            }
                        }
                        break;
                    default:
                        System.out.println("Некорректный HTTP-метод");
                        exchange.sendResponseHeaders(400, 0);
                }
            } else {
                System.out.println("Некорректный путь");
                exchange.sendResponseHeaders(400, 0);
            }
        }
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту");
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("Работа HTTP-сервера завершена");
    }
}

package service;

import models.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String filename;

    public FileBackedTaskManager(String filename) {
        this.filename = filename;
    }

    public static void main(String[] args) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager("tasks.csv");
        Task task1 = fileBackedTaskManager.createTask(new Task("task1", "description task1"));
        Task task2 = fileBackedTaskManager.createTask(new Task("task2", "description task2"));
        Epic epic1 = fileBackedTaskManager.createEpic(new Epic("epic1", "description epic1"));
        Subtask subtask1 = fileBackedTaskManager.createSubtask(new Subtask("subtask1",
                "description subtask1", epic1.getId()));
        Subtask subtask2 = fileBackedTaskManager.createSubtask(new Subtask("subtask2",
                "description subtask2", epic1.getId()));
        Epic epic2 = fileBackedTaskManager.createEpic(new Epic("epic2", "description epic2"));

        fileBackedTaskManager.getTaskById(task2.getId());
        fileBackedTaskManager.getEpicById(epic1.getId());
        fileBackedTaskManager.getSubtaskById(subtask2.getId());
        fileBackedTaskManager.getSubtaskById(subtask1.getId());

        FileBackedTaskManager newFileBacked = loadFromFile(new File("tasks.csv"));
        System.out.println(newFileBacked.getAllTasks());
        System.out.println(newFileBacked.getAllEpics());
        System.out.println(newFileBacked.getAllSubtasks());
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(newFileBacked.getHistory());
        Task task3 = newFileBacked.createTask(new Task("task3", "description task3"));
        System.out.println(newFileBacked.getTaskById(task3.getId()));

    }

    //    Переопределение модифицированных методов Task
    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

//    Переопределение модифицированных методов Epic
    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

//    Переопределение модифицированных методов Subtask
    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("id,type,name,status,description,epic\n");
            for (Task task : tasks.values()) {
                writeTaskToFile(bw, task);
            }

            for (Epic epic : epics.values()) {
                writeTaskToFile(bw, epic);
            }

            for (Subtask subtask : subtasks.values()) {
                writeTaskToFile(bw, subtask);
            }

            String historyString = historyToString(inMemoryHistoryManager);
            bw.newLine();
            bw.write(historyString);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записать в файл " + filename);
        }
    }

    private void writeTaskToFile(BufferedWriter bw, Task task) throws IOException {
        bw.write(taskToString(task));
        bw.newLine();
    }

    private String taskToString(Task task) {
        String taskString = String.format("%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(),
                task.getStatus(), task.getDescription());
        if (task.getType() == TaskType.SUBTASK) {
            taskString += "," + ((Subtask) task).getEpicId();
        }
        return taskString;
    }

    private static String historyToString(HistoryManager historyManager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            sb.append(task.getId()).append(",");
        }
        if (!(sb.length() == 0)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static Task taskFromString(String taskString) {
        String[] array = taskString.split(",");
        int id = Integer.parseInt(array[0]);
        TaskType type = TaskType.valueOf(array[1]);
        String name = array[2];
        Status status = Status.valueOf(array[3]);
        String description = array[4];
        if (type == TaskType.TASK) {
            return new Task(id, name, description, status);
        } else if (type == TaskType.EPIC) {
            return new Epic(id, name, description);
        } else {
            int epicId = Integer.parseInt(array[5]);
            return new Subtask(id, name, description, status, epicId);
        }
    }

    private static List<Integer> historyFromString(String historyString) {
        String[] historyIds = historyString.split(",");
        List<Integer> historyList = new ArrayList<>();
        for (String value : historyIds) {
            historyList.add(Integer.parseInt(value));
        }
        return historyList;
    }

    private static void addSubtasksToEpics(FileBackedTaskManager fbtManager) {
        for (Subtask subtask : fbtManager.getAllSubtasks()) {
            Epic epic = fbtManager.epics.get(subtask.getEpicId());
            epic.addSubtaskIds(subtask.getId());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fbtManager = new FileBackedTaskManager(file.getName());
        int startId = 0;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            lines.remove(0);
            for (String line : lines) {
                if (line.isEmpty()) {
                    break;
                }
                Task task = taskFromString(line);
                int taskId = task.getId();
                if (task.getType() == TaskType.TASK) {
                    fbtManager.tasks.put(taskId, task);
                } else if (task.getType() == TaskType.EPIC) {
                    fbtManager.epics.put(taskId, (Epic) task);
                } else {
                    fbtManager.subtasks.put(taskId, (Subtask) task);
                }

                if (taskId > startId) {
                    startId = taskId;
                }
            }
            addSubtasksToEpics(fbtManager);

            fbtManager.setStartingId(startId);

            String historyLine = lines.get(lines.size() - 1);
            List<Integer> history = historyFromString(historyLine);
            for (int id : history) {
                if (fbtManager.tasks.containsKey(id)) {
                    Task task = fbtManager.tasks.get(id);
                    fbtManager.inMemoryHistoryManager.add(task);
                } else if (fbtManager.epics.containsKey(id)) {
                    Epic task = fbtManager.epics.get(id);
                    fbtManager.inMemoryHistoryManager.add(task);
                } else {
                    Subtask task = fbtManager.subtasks.get(id);
                    fbtManager.inMemoryHistoryManager.add(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось прочитать файл " + file);
        }
        return fbtManager;
    }
}

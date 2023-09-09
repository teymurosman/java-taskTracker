package service;

import models.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String filename;

    public FileBackedTaskManager(String filename) {
        this.filename = filename;
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
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
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
            bw.write("id,type,name,status,description,epic,startTime,duration\n");
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
        } else {
            taskString += ",";
        }
        if (task.getStartTime() != null) {
            taskString += String.format(",%s,%s", task.getStartTime(), task.getDuration());
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
        LocalDateTime startTime;
        long duration;
        if (type == TaskType.TASK) {
            if (array.length > 5) {
                startTime = LocalDateTime.parse(array[6]);
                duration = Long.parseLong(array[7]);
                return new Task(id, name, description, status, startTime, duration);
            } else {
                return new Task(id, name, description, status);
            }
        } else if (type == TaskType.EPIC) {
            return new Epic(id, name, description, status);
        } else {
            int epicId = Integer.parseInt(array[5]);
            if (array.length > 6) {
                startTime = LocalDateTime.parse(array[6]);
                duration = Long.parseLong(array[7]);
                return new Subtask(id, name, description, status, epicId, startTime, duration);
            } else {
                return new Subtask(id, name, description, status, epicId);
            }
        }
    }

    private static List<Integer> historyFromString(String historyString) {
        if (historyString.isEmpty()) {
            return Collections.emptyList();
        }
        String[] historyIds = historyString.split(",");
        List<Integer> historyList = new LinkedList<>();
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
        for (Epic epic : fbtManager.epics.values()) {
            fbtManager.calculateEpicFields(epic);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fbtManager = new FileBackedTaskManager(file.getName());
        int startId = 0;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                return fbtManager;
            }
            lines.remove(0);
            for (String line : lines) {
                if (line.isEmpty()) {
                    break;
                }
                Task task = taskFromString(line);
                int taskId = task.getId();
                if (task.getType() == TaskType.TASK) {
                    fbtManager.tasks.put(taskId, task);
                    fbtManager.prioritizedTasks.add(task);
                } else if (task.getType() == TaskType.EPIC) {
                    fbtManager.epics.put(taskId, (Epic) task);
                } else {
                    fbtManager.subtasks.put(taskId, (Subtask) task);
                    fbtManager.prioritizedTasks.add(task);
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

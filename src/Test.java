import models.*;
import service.Managers;
import service.TaskManager;

public class Test {
    TaskManager inMemoryTaskManager = Managers.getDefault();

    public void testBaseTasksFunctions() {
        Task task1 = inMemoryTaskManager.createTask(new Task("Task 1", "Description task1"));
        Task task2 = inMemoryTaskManager.createTask((new Task("Task 2", "Description task2")));

        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1", "Description epic1"));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Subtask 1",
                "Description subtask1", epic1.getId()));
        Subtask subtask2 = inMemoryTaskManager.createSubtask((new Subtask("Subtask 2",
                "Description subtask2", epic1.getId())));

        Epic epic2 = inMemoryTaskManager.createEpic(new Epic("Epic 2", "Description epic2"));
        Subtask subtask3 = inMemoryTaskManager.createSubtask(new Subtask("Subtask 3",
                "Description subtask3", epic2.getId()));

        // Просмотрите все задачи, эпики и подзадачи
        System.out.println("Список задач:");
        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println("Список эпиков:");
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println("Список подзадач:");
        System.out.println(inMemoryTaskManager.getAllSubtasks());
        System.out.println("****************************************************************************************");

        // Измените статусы
        task1.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task1);
        subtask1.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask1);
        subtask3.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask3);

        // Просмотрите после изменения статусов
        System.out.println("После изменения статусов");
        System.out.println(inMemoryTaskManager.getTaskById(task1.getId()));
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getSubtaskById(subtask1.getId()));
        System.out.println(inMemoryTaskManager.getSubtaskById(subtask3.getId()));
        System.out.println("****************************************************************************************");

        // Удалите некоторые задачи
        inMemoryTaskManager.deleteTaskById(task2.getId());
        inMemoryTaskManager.deleteEpicById(epic2.getId());
        inMemoryTaskManager.deleteSubtaskById(subtask1.getId());

        // Просмотрите все задачи, эпики и подзадачи
        System.out.println("После удаления");
        System.out.println("Список задач:");
        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println("Список эпиков:");
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println("Список подзадач:");
        System.out.println(inMemoryTaskManager.getAllSubtasks());
        System.out.println("****************************************************************************************");
    }

    public void testHistory() {
        Task task1 = inMemoryTaskManager.createTask(new Task("Task 1", "Description task1"));
        Task task2 = inMemoryTaskManager.createTask((new Task("Task 2", "Description task2")));

        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1", "Description epic1"));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Subtask 1",
                "Description subtask1", epic1.getId()));
        Subtask subtask2 = inMemoryTaskManager.createSubtask((new Subtask("Subtask 2",
                "Description subtask2", epic1.getId())));

        Epic epic2 = inMemoryTaskManager.createEpic(new Epic("Epic 2", "Description epic2"));
        Subtask subtask3 = inMemoryTaskManager.createSubtask(new Subtask("Subtask 3",
                "Description subtask3", epic2.getId()));

        // Просмотрите все задачи, эпики и подзадачи
        System.out.println("Список задач:");
        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println("Список эпиков:");
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println("Список подзадач:");
        System.out.println(inMemoryTaskManager.getAllSubtasks());
        System.out.println("****************************************************************************************");

        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getSubtaskById(subtask2.getId());
        inMemoryTaskManager.getTaskById(task2.getId());

        // Просмотрите историю
        printHistory();

        // Удалите задачу, которая есть в истории
        inMemoryTaskManager.deleteTaskById(task1.getId());

        // Просмотрите историю
        System.out.println("История после удаления task1");
        printHistory();

        // Заполните историю 10 элементами, предыдущие просмотры удалятся
        for (int i = 0; i < 10; i++) {
            inMemoryTaskManager.getSubtaskById(subtask3.getId());
        }

        // Просмотрите историю
        System.out.println("История после добавления subtask3 10 раз");
        printHistory();
    }

    public void printHistory() {
        System.out.println("История просмотров задач:");
        int i = 1;
        for (Task task : inMemoryTaskManager.getHistory()) {
            System.out.println(i + ") " + task);
            i++;
        }
    }

    public void testCustomLinkedList() {
        // Создайте 2 задачи, эпик с 3 подзадачами и эпик без подзадач
        Task task1 = inMemoryTaskManager.createTask(new Task("Task 1", "Description task1"));
        Task task2 = inMemoryTaskManager.createTask((new Task("Task 2", "Description task2")));

        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1", "Description epic1"));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Subtask 1",
                "Description subtask1", epic1.getId()));
        Subtask subtask2 = inMemoryTaskManager.createSubtask((new Subtask("Subtask 2",
                "Description subtask2", epic1.getId())));
        Subtask subtask3 = inMemoryTaskManager.createSubtask((new Subtask("Subtask 3",
                "Description subtask3", epic1.getId())));

        Epic epic2 = inMemoryTaskManager.createEpic(new Epic("Epic 2", "Description epic2"));

        // Запросите созданные задачи
        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getSubtaskById(subtask2.getId());
        inMemoryTaskManager.getSubtaskById(subtask1.getId());
        inMemoryTaskManager.getEpicById(epic2.getId());
        inMemoryTaskManager.getSubtaskById(subtask3.getId());

        // Просмотр истории
        printHistory();

        // Вызовите вновь задачи
        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getSubtaskById(subtask2.getId());
        inMemoryTaskManager.getEpicById(epic2.getId());

        // Просмотр истории
        System.out.println("История после повтороного вызова task1, subtask2 и epic2:");
        printHistory();

        // Вызовите задачи и убедитесь, что повторов в истории нет
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());

        // Просмотр истории
        System.out.println("История после множественного вызова epic1:");
        printHistory();

        // Удалите задачу, которая есть в истории
        inMemoryTaskManager.deleteTaskById(task2.getId());

        // Просмотр истории, убедитесь, что удалённой задачи в ней нет
        System.out.println("История после удаления task2:");
        printHistory();

        // Удалите эпик с 3 подзадачами
        inMemoryTaskManager.deleteEpicById(epic1.getId());

        // Просмотр истории, убедитесь, что в ней нет как самого эпика, так и его подзадач
        System.out.println("История после удаления эпика с подзадачами:");
        printHistory();
    }
}

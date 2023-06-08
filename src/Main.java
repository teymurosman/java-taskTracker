import service.TaskManager;
import models.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.createTask(new Task("Позвонить бабушке", "Спросить как у нее дела"));
        Task task2 = taskManager.createTask(new Task("Заехать за продуктами",
                "Попросить жену скинуть список продуктов"));

        Epic epic1 = taskManager.createEpic(new Epic("Ремонт", "Исправить все недочёты в квартире"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Квартира",
                "Купить новый кран, вызвать сантехника для замены", epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Квартира",
                "Купить новый замок, поменять, сделать дубликаты ключей", epic1.getId()));

        Epic epic2 = taskManager.createEpic(new Epic("Java", "Изучить язык программирования Java"));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Купить книги по Java",
                "Head first, Clean code", epic2.getId()));

        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println(taskManager.getAllTasks());
        taskManager.updateTask(new Task(task1.getId(), task1.getName(), task1.getDescription(), Status.DONE));
        System.out.println(taskManager.getTaskById(task1.getId()));

        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println(taskManager.getAllEpics());
        taskManager.updateEpic(new Epic(epic1.getId(), epic1.getName(), "Починить всё в квартире и машину"));
        taskManager.createSubtask(new Subtask("Машина", "Отвезти в сервис", epic1.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));

        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println(taskManager.getSubtasksByEpic(epic2));
        taskManager.updateSubtask(new Subtask(subtask3.getId(), subtask3.getName(), subtask3.getDescription(),
                Status.DONE, subtask3.getEpicId())); // Должен поменяться и статус эпика, и сабтаска
        System.out.println(taskManager.getEpicById(epic2.getId()));

        System.out.println("---------------------------------------------------------------------------------------");
        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteEpicById(epic1.getId());
        taskManager.deleteSubtaskById(subtask3.getId()); // Статус должен вернуться к NEW
        System.out.println(taskManager.getAllTasks() + "\n?\r" + taskManager.getAllEpics());
    }
}

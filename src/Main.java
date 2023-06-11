import service.HistoryManager;
import service.InMemoryTaskManager;
import models.*;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) taskManager;
        HistoryManager historyManager = inMemoryTaskManager.getInMemoryHistoryManager();

        Task task1 = inMemoryTaskManager.createTask(new Task("Позвонить бабушке", "Спросить как у нее дела"));
        Task task2 = inMemoryTaskManager.createTask(new Task("Заехать за продуктами",
                "Попросить жену скинуть список продуктов"));

        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Ремонт", "Исправить все недочёты в квартире"));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Квартира",
                "Купить новый кран, вызвать сантехника для замены", epic1.getId()));
        Subtask subtask2 = inMemoryTaskManager.createSubtask(new Subtask("Квартира",
                "Купить новый замок, поменять, сделать дубликаты ключей", epic1.getId()));

        Epic epic2 = inMemoryTaskManager.createEpic(new Epic("Java", "Изучить язык программирования Java"));
        Subtask subtask3 = inMemoryTaskManager.createSubtask(new Subtask("Купить книги по Java",
                "Head first, Clean code", epic2.getId()));

        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println(inMemoryTaskManager.getAllTasks());
        inMemoryTaskManager.updateTask(new Task(task1.getId(), task1.getName(), task1.getDescription(), Status.DONE));
        System.out.println(inMemoryTaskManager.getTaskById(task1.getId()));

        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println(inMemoryTaskManager.getAllEpics());
        inMemoryTaskManager.updateEpic(new Epic(epic1.getId(), epic1.getName(), "Починить всё в квартире и машину"));
        inMemoryTaskManager.createSubtask(new Subtask("Машина", "Отвезти в сервис", epic1.getId()));
        System.out.println(inMemoryTaskManager.getEpicById(epic1.getId()));

        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(epic2));
        inMemoryTaskManager.updateSubtask(new Subtask(subtask3.getId(), subtask3.getName(), subtask3.getDescription(),
                Status.DONE, subtask3.getEpicId())); // Должен поменяться и статус эпика, и сабтаска
        System.out.println(inMemoryTaskManager.getEpicById(epic2.getId()));

        System.out.println("---------------------------------------------------------------------------------------");
        inMemoryTaskManager.deleteTaskById(task2.getId());
        inMemoryTaskManager.deleteEpicById(epic1.getId());
        inMemoryTaskManager.deleteSubtaskById(subtask3.getId()); // Статус должен вернуться к NEW
        System.out.println(inMemoryTaskManager.getAllTasks() + "\n?\r" + inMemoryTaskManager.getAllEpics());

//        Просмотрим некоторые задачи
        System.out.println(taskManager.getTaskById(task1.getId())); // 1
        System.out.println(taskManager.getEpicById(epic2.getId())); // 2
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println(historyManager.getHistory());
    }
}

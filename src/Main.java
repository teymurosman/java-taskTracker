import models.*;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();

        Task task1 = inMemoryTaskManager.createTask(new Task("Позвонить бабушке",
                "Спросить как у нее дела"));
        Task task2 = inMemoryTaskManager.createTask(new Task("Заехать за продуктами",
                "Попросить жену скинуть список продуктов"));

        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Ремонт",
                "Исправить все недочёты в квартире"));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Квартира",
                "Купить новый кран, вызвать сантехника для замены", epic1.getId()));
        Subtask subtask2 = inMemoryTaskManager.createSubtask(new Subtask("Квартира",
                "Купить новый замок, поменять, сделать дубликаты ключей", epic1.getId()));

        Epic epic2 = inMemoryTaskManager.createEpic(new Epic("Java",
                "Изучить язык программирования Java"));
        Subtask subtask3 = inMemoryTaskManager.createSubtask(new Subtask("Купить книги по Java",
                "Head first, Clean code", epic2.getId()));

        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println(inMemoryTaskManager.getAllTasks());
        inMemoryTaskManager.updateTask(new Task(task1.getId(), task1.getName(), task1.getDescription(), Status.DONE));
        System.out.println(inMemoryTaskManager.getTaskById(task1.getId())); // Добавился в history (1)

        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println(inMemoryTaskManager.getAllEpics());
        inMemoryTaskManager.updateEpic(new Epic(epic1.getId(), epic1.getName(),
                "Починить всё в квартире и машину"));
        inMemoryTaskManager.createSubtask(new Subtask("Машина", "Отвезти в сервис", epic1.getId()));
        System.out.println(inMemoryTaskManager.getEpicById(epic1.getId())); // Добавился в history (2)

        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(epic2));
        inMemoryTaskManager.updateSubtask(new Subtask(subtask3.getId(), subtask3.getName(), subtask3.getDescription(),
                Status.DONE, subtask3.getEpicId())); // Должен поменяться и статус эпика, и сабтаска
        System.out.println(inMemoryTaskManager.getEpicById(epic2.getId())); // Добавился в history (3)

        System.out.println("---------------------------------------------------------------------------------------");
        inMemoryTaskManager.deleteTaskById(task2.getId());
        inMemoryTaskManager.deleteEpicById(epic1.getId());
        inMemoryTaskManager.deleteSubtaskById(subtask3.getId()); // Статус должен вернуться к NEW
        System.out.println(inMemoryTaskManager.getAllTasks() + "\n?\r" + inMemoryTaskManager.getAllEpics());

//        Тестирование функции истории просмотров
//        Просмотрим некоторые задачи
        inMemoryTaskManager.getSubtaskById(subtask1.getId()); // Не добавился в history, её эпик удалён (и она сама)
        Epic epic3 = inMemoryTaskManager.createEpic(new Epic("epic3", "description epic3"));
        Subtask subtask4 = inMemoryTaskManager.createSubtask(new Subtask("sub3", "description sub3",
                epic3.getId()));
        inMemoryTaskManager.getSubtaskById(subtask4.getId()); // Добавился в history (4)
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println(inMemoryTaskManager.getHistory()); // task1, epic1, epic 2, subtask4
        System.out.println("---------------------------------------------------------------------------------------");
        for (int i = 0; i <= 10; i++) {
            inMemoryTaskManager.getEpicById(epic3.getId()); // history должна наполниться 10 объектами epic3,
            // а прошлые просмотры удалиться
        }
        System.out.println(inMemoryTaskManager.getHistory());
    }
}

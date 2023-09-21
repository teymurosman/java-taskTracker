import api.HttpTaskServer;
import api.KVServer;
import service.HttpTaskManager;
import util.Managers;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            KVServer kvServer = new KVServer();
            kvServer.start();
            HttpTaskManager taskManager = Managers.getDefault("http://localhost");
            HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
            httpTaskServer.start();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка запуска сервера");
        }
    }
}

import api.HttpTaskServer;
import api.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            KVServer kvServer = new KVServer();
            kvServer.start();
            HttpTaskServer httpTaskServer = new HttpTaskServer("http://localhost");
            httpTaskServer.start();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка запуска сервера");
        }
    }
}

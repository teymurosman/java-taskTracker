package util;

import service.*;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HttpTaskManager getHttpTaskManager(String host) {
        return new HttpTaskManager(host);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

package draylar.timechart.api;

import java.util.List;

public class DailyTaskData {

    private final List<Task> tasks;

    public DailyTaskData(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}

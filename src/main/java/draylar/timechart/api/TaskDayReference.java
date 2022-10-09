package draylar.timechart.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;

public class TaskDayReference {

    private final Calendar date;
    private final Path path;
    private final DailyTaskData task;

    private TaskDayReference(Calendar date, Path path, DailyTaskData task) {
        this.date = date;
        this.path = path;
        this.task = task;
    }

    public static TaskDayReference read(Calendar calendar, Path file) throws IOException {
        DailyTaskData deserializedDay = FileHelper.GSON.fromJson(Files.readString(file), DailyTaskData.class);
        return new TaskDayReference(calendar, file, deserializedDay);
    }

    public Calendar getDate() {
        return date;
    }

    public DailyTaskData getTask() {
        return task;
    }

    public void addTask(Task task) {
        this.task.getTasks().add(task);
    }

    public void removeTask(Task task) {
        this.task.getTasks().remove(task);
    }

    public void save() throws IOException {
        String json = FileHelper.GSON.toJson(task);
        Files.writeString(path, json);
    }
}

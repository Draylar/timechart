package draylar.timechart.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import draylar.timechart.Main;
import draylar.timechart.TimechartApplication;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileHelper {

    public static final Path FILE_PATH = Paths.get(System.getProperty("user.home"), "time.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void saveDaily() {
        Calendar calendar = Calendar.getInstance();
        String date = String.format("%d/%d/%d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));

        try {
            if(!Files.exists(FILE_PATH)) {
                Files.createFile(FILE_PATH);
            }

            Map<String, Integer> current = readSeconds();

            // Put values into map
            if(!current.containsKey(date)) {
                current.put(date, 0);
            }

            current.put(date, Main.SECONDS_TODAY);
            Files.writeString(FILE_PATH, GSON.toJson(current));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static Map<String, Integer> readSeconds() {
        try {
            Type type = new TypeToken<Map<String, Integer>>() {
            }.getType();
            Map<String, Integer> current = GSON.fromJson(Files.readString(FILE_PATH), type);
            if (current != null) {
                return current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    public static List<TaskDayReference> readWeekTasks() throws IOException {
        Path directory = Paths.get(System.getProperty("user.home"), "timechart");
        if(!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        List<TaskDayReference> references = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.roll(Calendar.DAY_OF_YEAR, -i);
            String fileName = String.format("%d-%d-%d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
            Path file = directory.resolve(fileName + ".json");
            if(Files.exists(file)) {
                TaskDayReference read = TaskDayReference.read(calendar, file);
                references.add(read);
            }

            // Once we hit Saturday ("start" of a work week, assuming reporting is done Friday), stop counting
            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                break;
            }
        }

        return references;
    }

    public static TaskDayReference readDailyTaskData() throws IOException {
        // Timechart stores data in the user.home/timechart directory.
        // If this dir does not exist, create it now.
        Path directory = Paths.get(System.getProperty("user.home"), "timechart");
        if(!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        // Read or create a file for today.
        Calendar calendar = Calendar.getInstance();
        String fileName = String.format("%d-%d-%d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
        Path file = directory.resolve(fileName + ".json");
        if(!Files.exists(file)) {
            Files.createFile(file);
            TimechartApplication.LOGGER.info(String.format("Creating new task file for date %s", fileName));

            // Write default data to this file.
            DailyTaskData defaultTaskData = new DailyTaskData(List.of());
            String json = GSON.toJson(defaultTaskData);
            Files.writeString(file, json);
        }

        return TaskDayReference.read(calendar, file);
    }
}

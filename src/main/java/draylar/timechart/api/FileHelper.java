package draylar.timechart.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import draylar.timechart.Main;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
}

package draylar.timechart;

import draylar.timechart.api.FileHelper;
import javafx.application.Application;
import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

public class Main {

    public static int SECONDS_WEEKLY = 0;
    public static int SECONDS_PROGRESSIVE = 0;
    public static int SECONDS_TODAY = 0;
    public static int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

    public static void main(String[] args) throws IOException, AWTException {
        // Read seconds from time.json
        Map<String, Integer> read = FileHelper.readSeconds();
        // TODO: method this
        Calendar calendar = Calendar.getInstance();
        String date = String.format("%d/%d/%d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));

        if(read.containsKey(date)) {
            SECONDS_TODAY = read.get(date);
        }

        // Collect time for the previous week
        for(int i = 0; i < 7; i++) {
            Calendar today = Calendar.getInstance();
            today.add(Calendar.DAY_OF_YEAR, -i);
            String pastDate = String.format("%d/%d/%d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));
            Integer value = read.get(pastDate);

            if(value != null) {
                SECONDS_WEEKLY += value;
            }

            // do not go before monday
            if(today.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                break;
            }
        }

        // Collect time for the past 7 days
        for(int i = 0; i < 7; i++) {
            Calendar today = Calendar.getInstance();
            today.add(Calendar.DAY_OF_YEAR, -i);
            String pastDate = String.format("%d/%d/%d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));
            Integer value = read.get(pastDate);

            if(value != null) {
                SECONDS_PROGRESSIVE += value;
            }
        }

        // Initialize tray
        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon icon = new TrayIcon(ImageIO.read(Main.class.getClassLoader().getResource("icon.png")));
        icon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == 1 && event.getClickCount() == 1) {
                    Platform.runLater(() -> {
                        TimechartApplication.STAGE.show();
                    });
                }
            }
        });

        // Assemble context menu for the tray icon
        PopupMenu menu = new PopupMenu();
        MenuItem quit = new MenuItem("Quit");
        quit.addActionListener(e -> System.exit(1));
        menu.add(quit);
        icon.setPopupMenu(menu);

        // Add icon to the system tray & launch JavaFX
        tray.add(icon);
        Application.launch(TimechartApplication.class);
    }
}

package draylar.timechart.controller;

import draylar.timechart.Main;
import draylar.timechart.api.FileHelper;
import draylar.timechart.api.Time;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainController {

    @FXML public Label working_label;
    @FXML public StackPane toggle;
    @FXML public Region toggle_circle;
    @FXML public Region toggle_background;
    @FXML public Text daily_time;
    @FXML public Text weekly_time;
    @FXML public Text progressive_time;
    @FXML public BarChart<String, Double> chart;

    public boolean tracking = false;

    @FXML
    public void initialize() {
        toggle.setOnMouseClicked(event -> {
            if(!tracking) {
                StackPane.setAlignment(toggle_circle, Pos.CENTER_RIGHT);
                toggle_background.setStyle("-fx-background-color: green; -fx-background-radius: 45;");
                tracking = true;
                working_label.setText("You are currently working.");
            } else {
                StackPane.setAlignment(toggle_circle, Pos.CENTER_LEFT);
                toggle_background.setStyle("-fx-background-color: #D9D9D9; -fx-background-radius: 45;");
                tracking = false;
                working_label.setText("You are not currently working.");
            }
        });

        // Bar chart setup
        // Collect time for the past 7 days
        Map<String, Integer> read = FileHelper.readSeconds();
        XYChart.Series<String, Double> series = new XYChart.Series<>();
        chart.setBarGap(2);
        chart.getYAxis().setLabel("Hours");

        for(int i = 6; i >= 0; i--) {
            Calendar today = Calendar.getInstance();
            today.add(Calendar.DAY_OF_YEAR, -i);
            String pastDate = String.format("%d/%d/%d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));
            String withoutYear = String.format("%d/%d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));
            Integer value = read.get(pastDate);

            if(value != null) {
                series.getData().add(new XYChart.Data<>(withoutYear, (double) value / 60 / 60));
            } else {
                series.getData().add(new XYChart.Data<>(withoutYear, 0d));
            }
        }

        chart.getData().add(series);

        // Start timer
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Update UI
                daily_time.setText(new Time(Main.SECONDS_TODAY).toFormatted());
                weekly_time.setText(new Time(Main.SECONDS_WEEKLY).toFormatted());
                progressive_time.setText(new Time(Main.SECONDS_PROGRESSIVE).toFormatted());

                // Increment timers
                if(tracking) {
                    Main.SECONDS_TODAY++;
                    Main.SECONDS_PROGRESSIVE++;
                    Main.SECONDS_WEEKLY++;
                    FileHelper.saveDaily();
                }
            }
        }, 0, 1000);
    }
}

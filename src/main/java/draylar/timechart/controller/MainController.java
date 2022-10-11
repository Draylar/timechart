package draylar.timechart.controller;

import draylar.timechart.Main;
import draylar.timechart.api.FileHelper;
import draylar.timechart.api.Task;
import draylar.timechart.api.TaskDayReference;
import draylar.timechart.api.Time;
import draylar.timechart.fx.TaskEntry;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainController {

    @FXML public Label working_label;
    @FXML public StackPane toggle;
    @FXML public Region toggle_circle;
    @FXML public Region toggle_background;
    @FXML public Text daily_time;
    @FXML public Text weekly_time;
    @FXML public Text progressive_time;
    @FXML public BarChart<String, Double> chart;
    @FXML public ImageView github;
    @FXML public TextArea task_input;
    @FXML public Button submit;
    @FXML public VBox task_list;
    @FXML public Button today_select;
    @FXML public Button weekly_select;
    @FXML public VBox today;
    @FXML public VBox today_tasks;
    @FXML public VBox weekly;

    public boolean tracking = false;

    @FXML
    public void initialize() {
        today.getChildren().remove(weekly);

        today_select.setOnMouseClicked(event -> {
            today.getChildren().clear();
            today.getChildren().add(today_tasks);
        });

        weekly_select.setOnMouseClicked(event -> {
            today.getChildren().clear();
            today.getChildren().add(weekly);
        });

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

        github.setImage(new Image(Main.class.getClassLoader().getResource("github.png").toString()));

        // Bar chart setup
        // Collect time for the past 7 days
        Map<String, Integer> read = FileHelper.readSeconds();
        XYChart.Series<String, Double> series = new XYChart.Series<>();
        chart.setBarGap(2);
        chart.getYAxis().setLabel("Hours");

        for (int i = 6; i >= 0; i--) {
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
                // If the clock rolls over from 11:59 PM to 12:00 AM at midnight, reset timer
                int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                if(day != Main.dayOfYear) {
                    Main.dayOfYear = day;
                    Main.SECONDS_TODAY = 0;
                }

                // Update UI
                daily_time.setText(new Time(Main.SECONDS_TODAY).toFormatted());
                weekly_time.setText(new Time(Main.SECONDS_WEEKLY).toFormatted());
                progressive_time.setText(new Time(Main.SECONDS_PROGRESSIVE).toFormatted());

                // Increment timers
                if(tracking) {
                    Main.SECONDS_TODAY++;
                    Main.SECONDS_PROGRESSIVE++;
                    Main.SECONDS_WEEKLY++;
                }
            }
        }, 0, 1000);

        // Save every 10 seconds.
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(tracking) {
                    FileHelper.saveDaily();
                }
            }
        }, 0, 10_000);


        // Populate daily tasks
        try {
            TaskDayReference day = FileHelper.readDailyTaskData();
            List<TaskDayReference> weekly = FileHelper.readWeekTasks();
            refreshTaskListView(day);
            refreshWeeklyLogView(weekly);

            // Configure task submission event handlers to update TaskDayReference.
            submit.setOnMouseClicked(event -> {
                String value = task_input.getText();
                if(!value.isEmpty()) {
                    task_input.clear();
                    Task task = new Task(false, value);

                    // Save & re-serialize task data
                    try {
                        day.addTask(task);
                        day.save();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

                    // Update UI based on data state
                    addTaskElement(day, task);
                }
            });
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void addTaskElement(TaskDayReference day, Task task) {
        TaskEntry entry = new TaskEntry(task);
        entry.getBox().selectedProperty().set(task.isComplete());
        entry.onDelete(() -> {
            try {
                day.removeTask(task);
                day.save();
                refreshTaskListView(day);
                refreshWeeklyLogView(FileHelper.readWeekTasks());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        task_list.getChildren().add(entry);

        // When the box is clicked (updated), serialize & re-load.
        entry.getBox().selectedProperty().addListener((observable, oldValue, newValue) -> {
            task.setComplete(newValue);

            try {
                day.save();
                refreshWeeklyLogView(FileHelper.readWeekTasks());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void refreshTaskListView(TaskDayReference day) {
        task_list.getChildren().clear();
        for (Task task : day.getTask().getTasks()) {
            addTaskElement(day, task);
            task_list.getChildren().add(new Separator());
        }
    }

    private void refreshWeeklyLogView(List<TaskDayReference> weekly) {
        this.weekly.getChildren().clear();
        for (TaskDayReference reference : weekly) {
            List<Task> tasks = reference.getTask().getTasks();
            for (Task task : tasks) {
                if(task.isComplete()) {
                    String nameOfDay = new SimpleDateFormat("EEEE").format(new Date(reference.getDate().getTimeInMillis()));
                    Label label = new Label(nameOfDay + ": " + task.description());
                    this.weekly.getChildren().add(label);
                }
            }
        }
    }
}

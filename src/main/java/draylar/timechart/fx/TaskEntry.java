package draylar.timechart.fx;

import draylar.timechart.api.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class TaskEntry extends HBox {

    private Task task;
    private CheckBox box;
    private Runnable deleteListener;

    public TaskEntry(Task task) {
        super();

        // Left-hand checkbox
        box = new CheckBox(task.description());
        box.setPadding(new Insets(7.0, 10.0, 7.0, 10.0));
        getChildren().add(box);

        HBox spacing = new HBox();
        HBox.setHgrow(spacing, Priority.ALWAYS);
        getChildren().add(spacing);

        // Right-hand delete button
        Button delete = new Button("X");
        getChildren().add(delete);
        delete.setOnMouseClicked(event -> {
            if(deleteListener != null) {
                deleteListener.run();
            }
        });

        this.task = task;

        // STYLE
    }

    public CheckBox getBox() {
        return box;
    }

    public void onDelete(Runnable runner) {
        this.deleteListener = runner;
    }
}

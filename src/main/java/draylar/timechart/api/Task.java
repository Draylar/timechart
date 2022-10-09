package draylar.timechart.api;

public class Task {

    private boolean complete = false;
    private String description = "";

    public Task(boolean complete, String description) {
        this.complete = complete;
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}

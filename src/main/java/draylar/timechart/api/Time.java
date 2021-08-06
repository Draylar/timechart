package draylar.timechart.api;

// TODO: why allocate a new object every time we want to do this? Make a static method?
public class Time {

    private static int SECONDS_PER_HOUR = 60 * 60;
    private static int SECONDS_PER_MINUTE = 60;
    private final int seconds;

    public Time(int seconds) {
        this.seconds = seconds;
    }

    public int getHours() {
        return seconds / SECONDS_PER_HOUR;
    }

    public int getMinutes() {
        return (seconds - getHours() * SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
    }

    public int getSeconds() {
        return seconds - getHours() * SECONDS_PER_HOUR - getMinutes() * SECONDS_PER_MINUTE;
    }

    public String toFormatted() {
        return String.format("%dh %dm %ds", getHours(), getMinutes(), getSeconds());
    }
}

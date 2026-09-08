package wenwen.task;

/**
 * Represents a task that occurs between a specified start and end time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event that is initially not done.
     *
     * @param description The text describing the event.
     * @param from The event's starting date or time.
     * @param to The event's ending date or time.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}

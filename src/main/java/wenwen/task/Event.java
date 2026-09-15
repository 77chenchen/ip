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
        this(description, from, to, false);
    }

    /**
     * Creates an event with its saved completion state.
     *
     * @param description The text describing the event.
     * @param from The event's starting date or time.
     * @param to The event's ending date or time.
     * @param isDone Whether the event is completed.
     */
    public Event(String description, String from, String to, boolean isDone) {
        super(description, isDone);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's starting date or time.
     *
     * @return The starting date or time.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's ending date or time.
     *
     * @return The ending date or time.
     */
    public String getTo() {
        return to;
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

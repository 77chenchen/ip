package wenwen.task;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates a deadline that is initially not done.
     *
     * @param description The text describing the deadline.
     * @param by The date or time by which the task should be completed.
     */
    public Deadline(String description, String by) {
        this(description, by, false);
    }

    /**
     * Creates a deadline with its saved completion state.
     *
     * @param description The text describing the deadline.
     * @param by The date or time by which the task should be completed.
     * @param isDone Whether the deadline is completed.
     */
    public Deadline(String description, String by, boolean isDone) {
        super(description, isDone);
        this.by = by;
    }

    /**
     * Returns the deadline's due date or time.
     *
     * @return The due date or time.
     */
    public String getBy() {
        return by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}

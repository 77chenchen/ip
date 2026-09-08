package wenwen;

import java.util.Scanner;

/**
 * Starts the Wenwen chatbot and handles user commands.
 */
public class Wenwen {
    private static final int MAX_TASKS = 100;
    private static final String LINE = "____________________________________________________________";
    private static final String TODO_PREFIX = "todo";
    private static final String DEADLINE_PREFIX = "deadline";
    private static final String EVENT_PREFIX = "event";
    private static final String MARK_PREFIX = "mark";
    private static final String UNMARK_PREFIX = "unmark";
    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";
    private static final String BANNER = " __        __                                  \n"
            + " \\ \\      / /__ _ ____      _____ _ __       \n"
            + "  \\ \\ /\\ / / _ \\ '_ \\ \\ /\\ / / _ \\ '_ \\      \n"
            + "   \\ V  V /  __/ | | \\ V  V /  __/ | | |     \n"
            + "    \\_/\\_/ \\___|_| |_|\\_/\\_/ \\___|_| |_|     \n";

    /**
     * Runs the chatbot until the user enters the bye command.
     *
     * @param args Command line arguments supplied by the runtime.
     */
    public static void main(String[] args) {
        printGreeting();

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            System.out.println(LINE);

            try {
                if (input.equals("bye")) {
                    printFarewell();
                    break;
                } else if (input.equals("list")) {
                    printTaskList(tasks, taskCount);
                } else if (input.startsWith(UNMARK_PREFIX)) {
                    markTaskAsNotDone(tasks, taskCount, input);
                } else if (input.startsWith(MARK_PREFIX)) {
                    markTaskAsDone(tasks, taskCount, input);
                } else if (input.startsWith(TODO_PREFIX)) {
                    taskCount = addTodo(tasks, taskCount, input);
                } else if (input.startsWith(DEADLINE_PREFIX)) {
                    taskCount = addDeadline(tasks, taskCount, input);
                } else if (input.startsWith(EVENT_PREFIX)) {
                    taskCount = addEvent(tasks, taskCount, input);
                } else {
                    throw new WenwenException("Sorry, I don't know that command yet.");
                }
            } catch (WenwenException e) {
                printError(e.getMessage());
            }
        }
    }

    /**
     * Prints the chatbot greeting shown when the program starts.
     */
    private static void printGreeting() {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Wenwen.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Prints the chatbot farewell shown before the program exits.
     */
    private static void printFarewell() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /**
     * Prints all currently stored tasks.
     *
     * @param tasks The task array containing stored tasks.
     * @param taskCount The number of tasks currently stored.
     */
    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
        System.out.println(LINE);
    }

    /**
     * Marks the task named in the user input as completed.
     *
     * @param tasks The task array containing stored tasks.
     * @param input The full user command.
     */
    private static void markTaskAsDone(Task[] tasks, int taskCount, String input) throws WenwenException {
        int taskIndex = getTaskIndex(input, MARK_PREFIX, taskCount);
        tasks[taskIndex].markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + tasks[taskIndex]);
        System.out.println(LINE);
    }

    /**
     * Marks the task named in the user input as not completed.
     *
     * @param tasks The task array containing stored tasks.
     * @param input The full user command.
     */
    private static void markTaskAsNotDone(Task[] tasks, int taskCount, String input) throws WenwenException {
        int taskIndex = getTaskIndex(input, UNMARK_PREFIX, taskCount);
        tasks[taskIndex].markAsNotDone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + tasks[taskIndex]);
        System.out.println(LINE);
    }

    /**
     * Converts a command containing a one-based task number to a zero-based array index.
     *
     * @param input The full user command.
     * @param commandPrefix The command text before the task number.
     * @param taskCount The number of tasks currently stored.
     * @return The zero-based array index.
     * @throws WenwenException If the command does not contain a valid task number.
     */
    private static int getTaskIndex(String input, String commandPrefix, int taskCount) throws WenwenException {
        String taskNumberText = getCommandDetails(input, commandPrefix);
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new WenwenException("Please give me a valid task number for '" + commandPrefix + "'.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new WenwenException("Task number " + taskNumber + " is not in your list.");
        }

        return taskNumber - 1;
    }

    /**
     * Adds a todo task from the user input.
     *
     * @param tasks The task array containing stored tasks.
     * @param taskCount The number of tasks currently stored.
     * @param input The full user command.
     * @return The updated task count.
     * @throws WenwenException If the todo description is empty or the list is full.
     */
    private static int addTodo(Task[] tasks, int taskCount, String input) throws WenwenException {
        ensureTaskSpace(taskCount);
        String description = getCommandDetails(input, TODO_PREFIX);
        ensureNotEmpty(description, "The description of a todo cannot be empty.");

        tasks[taskCount] = new Todo(description);
        taskCount++;
        printTaskAdded(tasks[taskCount - 1], taskCount);
        return taskCount;
    }

    /**
     * Adds a deadline task from the user input if it follows the expected command format.
     *
     * @param tasks The task array containing stored tasks.
     * @param taskCount The number of tasks currently stored.
     * @param input The full user command.
     * @return The updated task count, or the original task count if the command is invalid.
     * @throws WenwenException If the deadline command has invalid or incomplete details.
     */
    private static int addDeadline(Task[] tasks, int taskCount, String input) throws WenwenException {
        ensureTaskSpace(taskCount);
        int byIndex = input.indexOf(BY_SEPARATOR);
        if (byIndex < 0) {
            throw new WenwenException("Please use: deadline DESCRIPTION /by DATE_OR_TIME");
        }

        String description = input.substring(DEADLINE_PREFIX.length(), byIndex).trim();
        String by = input.substring(byIndex + BY_SEPARATOR.length()).trim();
        ensureNotEmpty(description, "The description of a deadline cannot be empty.");
        ensureNotEmpty(by, "The deadline needs a date or time after /by.");

        tasks[taskCount] = new Deadline(description, by);
        taskCount++;
        printTaskAdded(tasks[taskCount - 1], taskCount);
        return taskCount;
    }

    /**
     * Adds an event task from the user input if it follows the expected command format.
     *
     * @param tasks The task array containing stored tasks.
     * @param taskCount The number of tasks currently stored.
     * @param input The full user command.
     * @return The updated task count, or the original task count if the command is invalid.
     * @throws WenwenException If the event command has invalid or incomplete details.
     */
    private static int addEvent(Task[] tasks, int taskCount, String input) throws WenwenException {
        ensureTaskSpace(taskCount);
        int fromIndex = input.indexOf(FROM_SEPARATOR);
        int toIndex = input.indexOf(TO_SEPARATOR);
        if (fromIndex < 0 || toIndex < 0 || fromIndex >= toIndex) {
            throw new WenwenException("Please use: event DESCRIPTION /from START /to END");
        }

        String description = input.substring(EVENT_PREFIX.length(), fromIndex).trim();
        String from = input.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String to = input.substring(toIndex + TO_SEPARATOR.length()).trim();
        ensureNotEmpty(description, "The description of an event cannot be empty.");
        ensureNotEmpty(from, "The event needs a start time after /from.");
        ensureNotEmpty(to, "The event needs an end time after /to.");

        tasks[taskCount] = new Event(description, from, to);
        taskCount++;
        printTaskAdded(tasks[taskCount - 1], taskCount);
        return taskCount;
    }

    /**
     * Returns the command details after a command word.
     *
     * @param input The full user command.
     * @param commandPrefix The command word at the start of the input.
     * @return The command details after the command word.
     * @throws WenwenException If the input contains a malformed command word.
     */
    private static String getCommandDetails(String input, String commandPrefix) throws WenwenException {
        if (input.length() == commandPrefix.length()) {
            return "";
        }

        if (!input.startsWith(commandPrefix + " ")) {
            throw new WenwenException("Did you mean '" + commandPrefix + "'? Add a space after the command word.");
        }

        return input.substring(commandPrefix.length()).trim();
    }

    /**
     * Ensures there is still room to add another task.
     *
     * @param taskCount The number of tasks currently stored.
     * @throws WenwenException If the task list has reached its fixed capacity.
     */
    private static void ensureTaskSpace(int taskCount) throws WenwenException {
        if (taskCount >= MAX_TASKS) {
            throw new WenwenException("Your task list is full. Please complete the current list before adding more.");
        }
    }

    /**
     * Ensures a required command detail has content.
     *
     * @param text The text to check.
     * @param message The message to show if the text is empty.
     * @throws WenwenException If the given text is empty.
     */
    private static void ensureNotEmpty(String text, String message) throws WenwenException {
        if (text.isEmpty()) {
            throw new WenwenException(message);
        }
    }

    /**
     * Prints confirmation after a task has been added.
     *
     * @param task The task that was added.
     * @param taskCount The current number of tasks.
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
        System.out.println(LINE);
    }

    /**
     * Prints an error message for invalid user input.
     *
     * @param message The specific explanation of the input error.
     */
    private static void printError(String message) {
        System.out.println("Oops! " + message);
        System.out.println(LINE);
    }
}

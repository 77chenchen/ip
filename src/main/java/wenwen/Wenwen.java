package wenwen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import wenwen.exception.WenwenException;
import wenwen.storage.Storage;
import wenwen.task.Deadline;
import wenwen.task.Event;
import wenwen.task.Task;
import wenwen.task.Todo;

/**
 * Starts the Wenwen chatbot and handles user commands.
 */
public class Wenwen {
    private static final Path DATA_FILE_PATH = Path.of("data", "wenwen.txt");
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
        Storage storage = new Storage(DATA_FILE_PATH);
        List<Task> tasks = loadTasks(storage);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            System.out.println(LINE);

            try {
                if (input.equals("bye")) {
                    printFarewell();
                    break;
                } else if (input.equals("list")) {
                    printTaskList(tasks);
                } else if (input.startsWith(UNMARK_PREFIX)) {
                    markTaskAsNotDone(tasks, input);
                    storage.saveTasks(tasks);
                } else if (input.startsWith(MARK_PREFIX)) {
                    markTaskAsDone(tasks, input);
                    storage.saveTasks(tasks);
                } else if (input.startsWith(TODO_PREFIX)) {
                    addTodo(tasks, input);
                    storage.saveTasks(tasks);
                } else if (input.startsWith(DEADLINE_PREFIX)) {
                    addDeadline(tasks, input);
                    storage.saveTasks(tasks);
                } else if (input.startsWith(EVENT_PREFIX)) {
                    addEvent(tasks, input);
                    storage.saveTasks(tasks);
                } else {
                    throw new WenwenException("Sorry, I don't know that command yet.");
                }
            } catch (WenwenException e) {
                printError(e.getMessage());
            }
        }
    }

    /**
     * Loads saved tasks without preventing startup when the data is unavailable.
     *
     * @param storage The storage service to load from.
     * @return The saved tasks, or an empty list if loading fails.
     */
    private static List<Task> loadTasks(Storage storage) {
        try {
            return storage.loadTasks();
        } catch (WenwenException exception) {
            printError(exception.getMessage() + " Starting with an empty list instead.");
            return new ArrayList<>();
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
     * @param tasks The list containing stored tasks.
     */
    private static void printTaskList(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.println(LINE);
    }

    /**
     * Marks the task named in the user input as completed.
     *
     * @param tasks The list containing stored tasks.
     * @param input The full user command.
     * @throws WenwenException If the command does not contain a valid task number.
     */
    private static void markTaskAsDone(List<Task> tasks, String input) throws WenwenException {
        int taskIndex = getTaskIndex(input, MARK_PREFIX, tasks.size());
        tasks.get(taskIndex).markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + tasks.get(taskIndex));
        System.out.println(LINE);
    }

    /**
     * Marks the task named in the user input as not completed.
     *
     * @param tasks The list containing stored tasks.
     * @param input The full user command.
     * @throws WenwenException If the command does not contain a valid task number.
     */
    private static void markTaskAsNotDone(List<Task> tasks, String input) throws WenwenException {
        int taskIndex = getTaskIndex(input, UNMARK_PREFIX, tasks.size());
        tasks.get(taskIndex).markAsNotDone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + tasks.get(taskIndex));
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
        } catch (NumberFormatException exception) {
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
     * @param tasks The list containing stored tasks.
     * @param input The full user command.
     * @throws WenwenException If the todo description is empty.
     */
    private static void addTodo(List<Task> tasks, String input) throws WenwenException {
        String description = getCommandDetails(input, TODO_PREFIX);
        ensureNotEmpty(description, "The description of a todo cannot be empty.");

        Task task = new Todo(description);
        tasks.add(task);
        printTaskAdded(task, tasks.size());
    }

    /**
     * Adds a deadline task from the user input if it follows the expected command format.
     *
     * @param tasks The list containing stored tasks.
     * @param input The full user command.
     * @throws WenwenException If the deadline command has invalid or incomplete details.
     */
    private static void addDeadline(List<Task> tasks, String input) throws WenwenException {
        int byIndex = input.indexOf(BY_SEPARATOR);
        if (byIndex < 0) {
            throw new WenwenException("Please use: deadline DESCRIPTION /by DATE_OR_TIME");
        }

        String description = input.substring(DEADLINE_PREFIX.length(), byIndex).trim();
        String by = input.substring(byIndex + BY_SEPARATOR.length()).trim();
        ensureNotEmpty(description, "The description of a deadline cannot be empty.");
        ensureNotEmpty(by, "The deadline needs a date or time after /by.");

        Task task = new Deadline(description, by);
        tasks.add(task);
        printTaskAdded(task, tasks.size());
    }

    /**
     * Adds an event task from the user input if it follows the expected command format.
     *
     * @param tasks The list containing stored tasks.
     * @param input The full user command.
     * @throws WenwenException If the event command has invalid or incomplete details.
     */
    private static void addEvent(List<Task> tasks, String input) throws WenwenException {
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

        Task task = new Event(description, from, to);
        tasks.add(task);
        printTaskAdded(task, tasks.size());
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

package wenwen;

import java.util.Scanner;

/**
 * Starts the Wenwen chatbot and handles user commands.
 */
public class Wenwen {
    private static final int MAX_TASKS = 100;
    private static final String LINE = "____________________________________________________________";
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
            String input = scanner.nextLine();
            System.out.println(LINE);

            if (input.equals("bye")) {
                printFarewell();
                break;
            } else if (input.equals("list")) {
                printTaskList(tasks, taskCount);
            } else if (input.startsWith("unmark ")) {
                markTaskAsNotDone(tasks, input);
            } else if (input.startsWith("mark ")) {
                markTaskAsDone(tasks, input);
            } else if (input.startsWith("todo ")) {
                taskCount = addTodo(tasks, taskCount, input);
            } else if (input.startsWith("deadline ")) {
                taskCount = addDeadline(tasks, taskCount, input);
            } else if (input.startsWith("event ")) {
                taskCount = addEvent(tasks, taskCount, input);
            } else {
                printUnknownCommand();
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
    private static void markTaskAsDone(Task[] tasks, String input) {
        int taskIndex = getTaskIndex(input, "mark ");
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
    private static void markTaskAsNotDone(Task[] tasks, String input) {
        int taskIndex = getTaskIndex(input, "unmark ");
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
     * @return The zero-based array index.
     */
    private static int getTaskIndex(String input, String commandPrefix) {
        int taskNumber = Integer.parseInt(input.substring(commandPrefix.length()));
        return taskNumber - 1;
    }

    /**
     * Adds a todo task from the user input.
     *
     * @param tasks The task array containing stored tasks.
     * @param taskCount The number of tasks currently stored.
     * @param input The full user command.
     * @return The updated task count.
     */
    private static int addTodo(Task[] tasks, int taskCount, String input) {
        String description = input.substring("todo ".length()).trim();
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
     */
    private static int addDeadline(Task[] tasks, int taskCount, String input) {
        int byIndex = input.indexOf(" /by ");
        if (byIndex < 0) {
            printInvalidDeadlineFormat();
            return taskCount;
        }

        String description = input.substring("deadline ".length(), byIndex).trim();
        String by = input.substring(byIndex + " /by ".length()).trim();
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
     */
    private static int addEvent(Task[] tasks, int taskCount, String input) {
        int fromIndex = input.indexOf(" /from ");
        int toIndex = input.indexOf(" /to ");
        if (fromIndex < 0 || toIndex < 0 || fromIndex >= toIndex) {
            printInvalidEventFormat();
            return taskCount;
        }

        String description = input.substring("event ".length(), fromIndex).trim();
        String from = input.substring(fromIndex + " /from ".length(), toIndex).trim();
        String to = input.substring(toIndex + " /to ".length()).trim();
        tasks[taskCount] = new Event(description, from, to);
        taskCount++;
        printTaskAdded(tasks[taskCount - 1], taskCount);
        return taskCount;
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
     * Prints a message for commands that Wenwen does not support yet.
     */
    private static void printUnknownCommand() {
        System.out.println("I don't understand that command.");
        System.out.println(LINE);
    }

    /**
     * Prints the required syntax for adding a deadline.
     */
    private static void printInvalidDeadlineFormat() {
        System.out.println("Please use: deadline DESCRIPTION /by DATE_OR_TIME");
        System.out.println(LINE);
    }

    /**
     * Prints the required syntax for adding an event.
     */
    private static void printInvalidEventFormat() {
        System.out.println("Please use: event DESCRIPTION /from START /to END");
        System.out.println(LINE);
    }
}

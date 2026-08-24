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
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Wenwen.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (true) {
            String input = scanner.nextLine();
            System.out.println(LINE);

            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            } else if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
                System.out.println(LINE);
            } else if (input.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(input.substring(7));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[taskIndex]);
                System.out.println(LINE);
            } else if (input.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(input.substring(5));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[taskIndex]);
                System.out.println(LINE);
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println("added: " + input);
                System.out.println(LINE);
            }
        }
    }
}

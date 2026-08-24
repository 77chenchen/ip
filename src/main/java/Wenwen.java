import java.util.Scanner;

public class Wenwen {
    public static void main(String[] args) {
        String line = "____________________________________________________________";
        String banner = " __        __                                  \n"
          + " \\ \\      / /__ _ ____      _____ _ __       \n"
          + "  \\ \\ /\\ / / _ \\ '_ \\ \\ /\\ / / _ \\ '_ \\      \n"
          + "   \\ V  V /  __/ | | \\ V  V /  __/ | | |     \n"
          + "    \\_/\\_/ \\___|_| |_|\\_/\\_/ \\___|_| |_|     \n";

        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hello! I'm Wenwen.");
        System.out.println("What can I do for you?");
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        while (true) {
            String input = scanner.nextLine();
            System.out.println(line);

            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            } else if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String statusIcon = isDone[i] ? "X" : " ";
                    System.out.println((i + 1) + ".[" + statusIcon + "] " + tasks[i]);
                }
                System.out.println(line);
            } else if (input.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(input.substring(7));
                int taskIndex = taskNumber - 1;
                isDone[taskIndex] = false;
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  [ ] " + tasks[taskIndex]);
                System.out.println(line);
            } else if (input.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(input.substring(5));
                int taskIndex = taskNumber - 1;
                isDone[taskIndex] = true;
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  [X] " + tasks[taskIndex]);
                System.out.println(line);
            } else {
                tasks[taskCount] = input;
                taskCount++;
                System.out.println("added: " + input);
                System.out.println(line);
            }
        }
    }
}

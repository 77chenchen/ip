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
	while(true){
        String input = scanner.nextLine();
	System.out.println(line);
	if(input.equals("bye"))
	{
        	System.out.println("Bye. Hope to see you again soon!");
		System.out.println(line);
		break;
	}
	System.out.println(input);
        System.out.println(line);
	}
    }
}

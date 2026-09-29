package wenwen.parser;

import wenwen.command.AddCommand;
import wenwen.command.Command;
import wenwen.command.DeleteCommand;
import wenwen.command.ExitCommand;
import wenwen.command.ListCommand;
import wenwen.command.MarkCommand;
import wenwen.exception.WenwenException;
import wenwen.task.Deadline;
import wenwen.task.Event;
import wenwen.task.Todo;

/**
 * Converts raw user input into executable commands.
 */
public final class Parser {
    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";

    private Parser() {
    }

    /**
     * Parses one complete user command.
     *
     * @param fullCommand command entered by the user
     * @return command that represents the requested action
     * @throws WenwenException if the command is unknown or malformed
     */
    public static Command parse(String fullCommand) throws WenwenException {
        String input = fullCommand.trim();
        String commandWord = getCommandWord(input);

        switch (commandWord) {
        case "bye":
            ensureNoDetails(input, commandWord);
            return new ExitCommand();
        case "list":
            ensureNoDetails(input, commandWord);
            return new ListCommand();
        case "mark":
            return new MarkCommand(parseTaskNumber(input, commandWord), true);
        case "unmark":
            return new MarkCommand(parseTaskNumber(input, commandWord), false);
        case "delete":
            return new DeleteCommand(parseTaskNumber(input, commandWord));
        case "todo":
            return parseTodo(input);
        case "deadline":
            return parseDeadline(input);
        case "event":
            return parseEvent(input);
        default:
            throw new WenwenException("Sorry, I don't know that command yet.");
        }
    }

    private static Command parseTodo(String input) throws WenwenException {
        String description = getCommandDetails(input, "todo");
        ensureNotEmpty(description, "The description of a todo cannot be empty.");
        return new AddCommand(new Todo(description));
    }

    private static Command parseDeadline(String input) throws WenwenException {
        int byIndex = input.indexOf(BY_SEPARATOR);
        if (byIndex < 0) {
            throw new WenwenException("Please use: deadline DESCRIPTION /by DATE_OR_TIME");
        }

        String description = input.substring("deadline".length(), byIndex).trim();
        String by = input.substring(byIndex + BY_SEPARATOR.length()).trim();
        ensureNotEmpty(description, "The description of a deadline cannot be empty.");
        ensureNotEmpty(by, "The deadline needs a date or time after /by.");
        return new AddCommand(new Deadline(description, by));
    }

    private static Command parseEvent(String input) throws WenwenException {
        int fromIndex = input.indexOf(FROM_SEPARATOR);
        int toIndex = input.indexOf(TO_SEPARATOR);
        if (fromIndex < 0 || toIndex < 0 || fromIndex >= toIndex) {
            throw new WenwenException("Please use: event DESCRIPTION /from START /to END");
        }

        String description = input.substring("event".length(), fromIndex).trim();
        String from = input.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String to = input.substring(toIndex + TO_SEPARATOR.length()).trim();
        ensureNotEmpty(description, "The description of an event cannot be empty.");
        ensureNotEmpty(from, "The event needs a start time after /from.");
        ensureNotEmpty(to, "The event needs an end time after /to.");
        return new AddCommand(new Event(description, from, to));
    }

    private static int parseTaskNumber(String input, String commandWord) throws WenwenException {
        String taskNumberText = getCommandDetails(input, commandWord);
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new WenwenException("Please give me a valid task number for '" + commandWord + "'.");
        }
    }

    private static String getCommandWord(String input) {
        int firstSpace = input.indexOf(' ');
        return firstSpace < 0 ? input : input.substring(0, firstSpace);
    }

    private static String getCommandDetails(String input, String commandWord) {
        if (input.length() == commandWord.length()) {
            return "";
        }
        return input.substring(commandWord.length()).trim();
    }

    private static void ensureNoDetails(String input, String commandWord) throws WenwenException {
        if (!getCommandDetails(input, commandWord).isEmpty()) {
            throw new WenwenException("The '" + commandWord + "' command does not take extra details.");
        }
    }

    private static void ensureNotEmpty(String text, String message) throws WenwenException {
        if (text.isEmpty()) {
            throw new WenwenException(message);
        }
    }
}

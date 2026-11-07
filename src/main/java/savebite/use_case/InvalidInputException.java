package savebite.use_case;

/**
 * Thrown when text typed by the user cannot be turned into a valid value.
 * The message is shown to the user.
 */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

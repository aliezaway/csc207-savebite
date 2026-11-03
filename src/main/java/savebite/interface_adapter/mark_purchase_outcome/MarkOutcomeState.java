package savebite.interface_adapter.mark_purchase_outcome;

/**
 * State of the eaten / discarded buttons.
 */
public class MarkOutcomeState {
    private String message = "";
    private String error = "";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

package savebite.interface_adapter.record_purchase;

import java.util.List;

/**
 * State of the purchase form.
 */
public class RecordPurchaseState {
    private List<ProductChoice> productChoices = List.of();
    private String suggestedTargetDate;
    private String suggestionMessage = "";
    private String error = "";
    private String successMessage = "";

    public List<ProductChoice> getProductChoices() {
        return productChoices;
    }

    public void setProductChoices(List<ProductChoice> productChoices) {
        this.productChoices = productChoices;
    }

    /**
     * Returns the suggested target date as YYYY-MM-DD, or null if there is none.
     */
    public String getSuggestedTargetDate() {
        return suggestedTargetDate;
    }

    public void setSuggestedTargetDate(String suggestedTargetDate) {
        this.suggestedTargetDate = suggestedTargetDate;
    }

    public String getSuggestionMessage() {
        return suggestionMessage;
    }

    public void setSuggestionMessage(String suggestionMessage) {
        this.suggestionMessage = suggestionMessage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}

package savebite.use_case.mark_purchase_outcome;

/**
 * The batch to finish and what happened to it.
 */
public class MarkPurchaseOutcomeInputData {
    private final String purchaseId;
    private final Outcome outcome;

    public MarkPurchaseOutcomeInputData(String purchaseId, Outcome outcome) {
        this.purchaseId = purchaseId;
        this.outcome = outcome;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public Outcome getOutcome() {
        return outcome;
    }
}

package savebite.use_case.mark_purchase_outcome;

/**
 * Output boundary for marking a batch as eaten or discarded.
 */
public interface MarkPurchaseOutcomeOutputBoundary {
    void prepareSuccessView(MarkPurchaseOutcomeOutputData outputData);

    void prepareFailView(String error);
}

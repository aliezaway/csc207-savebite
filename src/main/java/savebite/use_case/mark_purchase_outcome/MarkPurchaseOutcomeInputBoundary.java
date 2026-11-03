package savebite.use_case.mark_purchase_outcome;

/**
 * Input boundary for marking a whole batch as eaten or discarded.
 */
public interface MarkPurchaseOutcomeInputBoundary {
    void execute(MarkPurchaseOutcomeInputData inputData);
}

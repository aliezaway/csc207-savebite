package savebite.interface_adapter.mark_purchase_outcome;

import savebite.use_case.mark_purchase_outcome.MarkPurchaseOutcomeInputBoundary;
import savebite.use_case.mark_purchase_outcome.MarkPurchaseOutcomeInputData;
import savebite.use_case.mark_purchase_outcome.Outcome;

/**
 * Passes eaten / discarded clicks to the mark purchase outcome use case.
 */
public class MarkPurchaseOutcomeController {
    private final MarkPurchaseOutcomeInputBoundary interactor;

    public MarkPurchaseOutcomeController(MarkPurchaseOutcomeInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void markEaten(String purchaseId) {
        interactor.execute(new MarkPurchaseOutcomeInputData(purchaseId, Outcome.CONSUMED));
    }

    public void markDiscarded(String purchaseId) {
        interactor.execute(new MarkPurchaseOutcomeInputData(purchaseId, Outcome.DISCARDED));
    }
}

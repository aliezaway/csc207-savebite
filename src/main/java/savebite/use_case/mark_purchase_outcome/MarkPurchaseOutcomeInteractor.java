package savebite.use_case.mark_purchase_outcome;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

import savebite.entity.Product;
import savebite.entity.Purchase;
import savebite.use_case.DataAccessException;
import savebite.use_case.ProductDataAccessInterface;
import savebite.use_case.PurchaseDataAccessInterface;

/**
 * Marks a whole batch as eaten or discarded today (UC5).
 * A passed date does not stop the user from marking a batch as eaten; SaveBite only records what the user did.
 */
public class MarkPurchaseOutcomeInteractor implements MarkPurchaseOutcomeInputBoundary {
    private final PurchaseDataAccessInterface purchaseDataAccess;
    private final ProductDataAccessInterface productDataAccess;
    private final MarkPurchaseOutcomeOutputBoundary presenter;
    private final Clock clock;

    public MarkPurchaseOutcomeInteractor(PurchaseDataAccessInterface purchaseDataAccess,
                                         ProductDataAccessInterface productDataAccess,
                                         MarkPurchaseOutcomeOutputBoundary presenter, Clock clock) {
        this.purchaseDataAccess = purchaseDataAccess;
        this.productDataAccess = productDataAccess;
        this.presenter = presenter;
        this.clock = clock;
    }

    @Override
    public void execute(MarkPurchaseOutcomeInputData inputData) {
        if (inputData.getPurchaseId() == null || inputData.getOutcome() == null) {
            presenter.prepareFailView("Select a batch in the table first.");
            return;
        }
        Optional<Purchase> existing = purchaseDataAccess.findById(inputData.getPurchaseId());
        if (existing.isEmpty()) {
            presenter.prepareFailView("This batch no longer exists. Refresh the inventory.");
            return;
        }

        LocalDate today = LocalDate.now(clock);
        Purchase finished;
        try {
            finished = inputData.getOutcome() == Outcome.CONSUMED
                    ? existing.get().markConsumed(today)
                    : existing.get().markDiscarded(today);
        } catch (IllegalStateException | IllegalArgumentException e) {
            presenter.prepareFailView(e.getMessage());
            return;
        }

        try {
            purchaseDataAccess.save(finished);
        } catch (DataAccessException e) {
            presenter.prepareFailView("Could not save, so the batch is still in stock. (" + e.getMessage() + ")");
            return;
        }
        String productName = "(unknown product)";
        Optional<Product> product = productDataAccess.findById(finished.getProductId());
        if (product.isPresent()) {
            productName = product.get().getName();
        }
        presenter.prepareSuccessView(new MarkPurchaseOutcomeOutputData(finished.getId(), productName,
                inputData.getOutcome(), finished.getTotalPrice(), today));
    }
}

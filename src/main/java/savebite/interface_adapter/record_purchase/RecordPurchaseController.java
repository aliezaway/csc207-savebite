package savebite.interface_adapter.record_purchase;

import savebite.use_case.record_purchase.RecordPurchaseInputBoundary;
import savebite.use_case.record_purchase.RecordPurchaseInputData;

/**
 * Passes purchase form actions to the record purchase use case.
 */
public class RecordPurchaseController {
    private final RecordPurchaseInputBoundary interactor;

    public RecordPurchaseController(RecordPurchaseInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void loadProducts() {
        interactor.loadProducts();
    }

    public void suggestTargetDate(String productId, String purchaseDate) {
        interactor.suggestTargetDate(productId, purchaseDate);
    }

    public void execute(String productId, String totalPrice, String quantity, String unit, String purchaseDate,
                        String targetDate, boolean packageDate, boolean estimateConfirmed) {
        interactor.execute(new RecordPurchaseInputData(productId, totalPrice, quantity, unit, purchaseDate,
                targetDate, packageDate, estimateConfirmed));
    }
}

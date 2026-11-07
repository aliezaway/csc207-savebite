package savebite.use_case.record_purchase;

import java.time.LocalDate;

/**
 * The batch that was saved.
 */
public class RecordPurchaseOutputData {
    private final String purchaseId;
    private final String productName;
    private final LocalDate targetDate;

    public RecordPurchaseOutputData(String purchaseId, String productName, LocalDate targetDate) {
        this.purchaseId = purchaseId;
        this.productName = productName;
        this.targetDate = targetDate;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public String getProductName() {
        return productName;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }
}

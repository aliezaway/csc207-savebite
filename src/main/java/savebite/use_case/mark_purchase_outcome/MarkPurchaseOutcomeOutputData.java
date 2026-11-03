package savebite.use_case.mark_purchase_outcome;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The batch that was finished.
 */
public class MarkPurchaseOutcomeOutputData {
    private final String purchaseId;
    private final String productName;
    private final Outcome outcome;
    private final BigDecimal totalPrice;
    private final LocalDate outcomeDate;

    public MarkPurchaseOutcomeOutputData(String purchaseId, String productName, Outcome outcome,
                                         BigDecimal totalPrice, LocalDate outcomeDate) {
        this.purchaseId = purchaseId;
        this.productName = productName;
        this.outcome = outcome;
        this.totalPrice = totalPrice;
        this.outcomeDate = outcomeDate;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public String getProductName() {
        return productName;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDate getOutcomeDate() {
        return outcomeDate;
    }
}

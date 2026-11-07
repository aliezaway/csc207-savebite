package savebite.use_case.record_purchase;

/**
 * Values typed by the user for a new purchase batch.
 */
public class RecordPurchaseInputData {
    private final String productId;
    private final String totalPrice;
    private final String quantity;
    private final String unit;
    private final String purchaseDate;
    private final String targetDate;
    private final boolean packageDate;
    private final boolean estimateConfirmed;

    /**
     * @param productId the selected product
     * @param totalPrice the total price of the whole batch
     * @param quantity the quantity bought
     * @param unit the unit of the quantity
     * @param purchaseDate the purchase date, YYYY-MM-DD
     * @param targetDate the target date, YYYY-MM-DD
     * @param packageDate true if the target date was read from the package
     * @param estimateConfirmed true if the user confirmed an estimated target date
     */
    public RecordPurchaseInputData(String productId, String totalPrice, String quantity, String unit,
                                   String purchaseDate, String targetDate, boolean packageDate,
                                   boolean estimateConfirmed) {
        this.productId = productId;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.unit = unit;
        this.purchaseDate = purchaseDate;
        this.targetDate = targetDate;
        this.packageDate = packageDate;
        this.estimateConfirmed = estimateConfirmed;
    }

    public String getProductId() {
        return productId;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public boolean isPackageDate() {
        return packageDate;
    }

    public boolean isEstimateConfirmed() {
        return estimateConfirmed;
    }
}

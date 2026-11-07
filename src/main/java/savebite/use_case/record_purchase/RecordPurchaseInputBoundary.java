package savebite.use_case.record_purchase;

/**
 * Input boundary for recording a purchase batch.
 */
public interface RecordPurchaseInputBoundary {
    /**
     * Presents the products that can be used for a new purchase.
     */
    void loadProducts();

    /**
     * Presents a target date estimated from the product default.
     *
     * @param productId        the selected product
     * @param purchaseDateText the purchase date typed by the user
     */
    void suggestTargetDate(String productId, String purchaseDateText);

    /**
     * Validates and saves a new purchase batch.
     */
    void execute(RecordPurchaseInputData inputData);
}

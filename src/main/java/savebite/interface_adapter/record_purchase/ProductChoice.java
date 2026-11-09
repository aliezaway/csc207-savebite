package savebite.interface_adapter.record_purchase;

/**
 * A product shown in the product drop-down of the purchase form.
 */
public class ProductChoice {
    private final String productId;
    private final String label;

    public ProductChoice(String productId, String label) {
        this.productId = productId;
        this.label = label;
    }

    public String getProductId() {
        return productId;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}

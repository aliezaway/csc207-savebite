package savebite.use_case.record_purchase;

/**
 * A product that can be chosen for a new purchase.
 */
public class ProductOption {
    private final String productId;
    private final String name;
    private final String brand;

    /**
     * @param productId the productId
     * @param name the name
     * @param brand the brand, or null
     */
    public ProductOption(String productId, String name, String brand) {
        this.productId = productId;
        this.name = name;
        this.brand = brand;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }
}

package savebite.use_case.create_product;

/**
 * The product that was created.
 */
public class CreateProductOutputData {
    private final String productId;
    private final String name;

    public CreateProductOutputData(String productId, String name) {
        this.productId = productId;
        this.name = name;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }
}

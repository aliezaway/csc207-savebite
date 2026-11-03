package savebite.use_case.create_product;

/**
 * Product details returned by the food database. Either value may be null.
 */
public class ProductLookupResult {
    private final String name;
    private final String brand;

    public ProductLookupResult(String name, String brand) {
        this.name = name;
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }
}

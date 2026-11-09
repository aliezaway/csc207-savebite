package savebite.use_case.create_product;

/**
 * Details found for a barcode. The name or brand may be null when the database does not have them.
 */
public class ProductLookupOutputData {
    private final String barcode;
    private final String name;
    private final String brand;

    public ProductLookupOutputData(String barcode, String name, String brand) {
        this.barcode = barcode;
        this.name = name;
        this.brand = brand;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }
}

package savebite.use_case.create_product;

/**
 * Values typed by the user for a new product.
 */
public class CreateProductInputData {
    private final String name;
    private final String category;
    private final String brand;
    private final String barcode;
    private final String defaultDays;
    private final boolean confirmDuplicateName;

    /**
     * @param name product name
     * @param category category name, one of {@code savebite.entity.Category}
     * @param brand brand, may be blank
     * @param barcode barcode, may be blank
     * @param defaultDays default days to use the product, may be blank
     * @param confirmDuplicateName true if the user agreed to create a product whose name already exists
     */
    public CreateProductInputData(String name, String category, String brand, String barcode,
                                  String defaultDays, boolean confirmDuplicateName) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.barcode = barcode;
        this.defaultDays = defaultDays;
        this.confirmDuplicateName = confirmDuplicateName;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getDefaultDays() {
        return defaultDays;
    }

    public boolean isConfirmDuplicateName() {
        return confirmDuplicateName;
    }
}

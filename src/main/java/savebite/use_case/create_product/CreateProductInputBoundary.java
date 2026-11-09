package savebite.use_case.create_product;

/**
 * Input boundary for creating a product.
 */
public interface CreateProductInputBoundary {
    /**
     * Looks up a barcode in the online food database. Nothing is saved.
     */
    void lookup(String barcode);

    /**
     * Validates and saves a new product.
     */
    void execute(CreateProductInputData inputData);
}

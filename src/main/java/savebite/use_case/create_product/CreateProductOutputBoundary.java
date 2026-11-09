package savebite.use_case.create_product;

/**
 * Output boundary for creating a product.
 */
public interface CreateProductOutputBoundary {
    void prepareLookupResult(ProductLookupOutputData outputData);

    void prepareLookupFailView(String message);

    void prepareDuplicateNameWarning(String message);

    void prepareSuccessView(CreateProductOutputData outputData);

    void prepareFailView(String error);
}

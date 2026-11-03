package savebite.use_case.create_product;

/**
 * Thrown when the food database could not be reached or answered with an error.
 */
public class ProductLookupException extends Exception {
    public ProductLookupException(String message) {
        super(message);
    }

    public ProductLookupException(String message, Throwable cause) {
        super(message, cause);
    }
}

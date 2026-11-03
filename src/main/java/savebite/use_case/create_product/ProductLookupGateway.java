package savebite.use_case.create_product;

import java.util.Optional;

/**
 * Looks up product details by barcode in an external food database.
 */
public interface ProductLookupGateway {
    /**
     * @param barcode a barcode of 8 to 14 digits
     * @return the details found, or empty if the database has no product with this barcode
     * @throws ProductLookupException if the database could not be reached or answered with an error
     */
    Optional<ProductLookupResult> lookup(String barcode) throws ProductLookupException;
}

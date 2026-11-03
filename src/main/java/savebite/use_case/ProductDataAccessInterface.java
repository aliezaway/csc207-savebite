package savebite.use_case;

import java.util.List;
import java.util.Optional;

import savebite.entity.Product;

/**
 * Stores products. Implementations load their data once, so reads do not fail.
 */
public interface ProductDataAccessInterface {
    Optional<Product> findById(String id);

    Optional<Product> findByBarcode(String barcode);

    /**
     * Returns every product, including archived ones.
     */
    List<Product> listAll();

    /**
     * Adds a product, or replaces the stored product with the same id.
     * If saving fails, the stored data is left unchanged.
     *
     * @throws DataAccessException if the product could not be saved
     */
    void save(Product product) throws DataAccessException;
}

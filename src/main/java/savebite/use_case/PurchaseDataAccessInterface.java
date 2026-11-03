package savebite.use_case;

import java.util.List;
import java.util.Optional;

import savebite.entity.Purchase;

/**
 * Stores purchase batches. Implementations load their data once, so reads do not fail.
 */
public interface PurchaseDataAccessInterface {
    Optional<Purchase> findById(String id);

    /**
     * Returns every purchase batch, whatever its status.
     */
    List<Purchase> listAll();

    /**
     * Adds a batch, or replaces the stored batch with the same id.
     * If saving fails, the stored data is left unchanged.
     *
     * @throws DataAccessException if the batch could not be saved
     */
    void save(Purchase purchase) throws DataAccessException;
}

package savebite.data_access;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import savebite.entity.Purchase;
import savebite.use_case.PurchaseDataAccessInterface;

/**
 * Keeps purchase batches in memory only. Used by tests and while the JSON storage is being built.
 */
public class InMemoryPurchaseDataAccess implements PurchaseDataAccessInterface {
    private final Map<String, Purchase> purchases = new LinkedHashMap<>();

    @Override
    public Optional<Purchase> findById(String id) {
        return Optional.ofNullable(purchases.get(id));
    }

    @Override
    public List<Purchase> listAll() {
        return new ArrayList<>(purchases.values());
    }

    @Override
    public void save(Purchase purchase) {
        purchases.put(purchase.getId(), purchase);
    }
}

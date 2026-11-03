package savebite.data_access;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import savebite.entity.Product;
import savebite.use_case.ProductDataAccessInterface;

/**
 * Keeps products in memory only. Used by tests and while the JSON storage is being built.
 */
public class InMemoryProductDataAccess implements ProductDataAccessInterface {
    private final Map<String, Product> products = new LinkedHashMap<>();

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public Optional<Product> findByBarcode(String barcode) {
        for (Product product : products.values()) {
            if (barcode != null && barcode.equals(product.getBarcode())) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Product> listAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public void save(Product product) {
        products.put(product.getId(), product);
    }
}

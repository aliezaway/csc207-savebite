package savebite.use_case;

import java.util.Comparator;

import savebite.entity.Product;

/**
 * Orders products alphabetically by name, ignoring upper and lower case.
 */
public class ProductNameComparator implements Comparator<Product> {
    @Override
    public int compare(Product first, Product second) {
        return first.getName().compareToIgnoreCase(second.getName());
    }
}

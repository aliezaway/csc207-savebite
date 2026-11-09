package savebite.interface_adapter;

import java.util.ArrayList;
import java.util.List;

import savebite.entity.Category;

/**
 * Category names for views, so views do not depend on the entity layer.
 */
public final class CategoryNames {
    private CategoryNames() {
    }

    public static List<String> all() {
        List<String> names = new ArrayList<>();
        for (Category category : Category.values()) {
            names.add(category.name());
        }
        return names;
    }
}

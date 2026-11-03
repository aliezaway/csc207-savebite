package savebite.entity;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A reusable product record, such as "Milk 1L". Each time the product is bought,
 * a separate {@link Purchase} is recorded. Products are immutable: changes return a new copy.
 */
public class Product {
    private final String id;
    private final String name;
    private final Category category;
    private final String brand;
    private final String barcode;
    private final Integer defaultUseWithinDays;
    private final boolean archived;

    /**
     * Creates a product.
     *
     * @param id                   unique id
     * @param name                 display name, must not be blank
     * @param category             category
     * @param brand                brand, or null
     * @param barcode              barcode, or null
     * @param defaultUseWithinDays suggested number of days to use the product, or null for none
     * @param archived             whether the product is archived
     * @throws IllegalArgumentException if a value is invalid
     */
    public Product(String id, String name, Category category, String brand, String barcode,
                   Integer defaultUseWithinDays, boolean archived) {
        this.id = requireText(id, "Product id");
        this.name = requireText(name, "Name");
        this.category = Objects.requireNonNull(category, "Category is required.");
        this.brand = blankToNull(brand);
        this.barcode = blankToNull(barcode);
        if (defaultUseWithinDays != null && defaultUseWithinDays <= 0) {
            throw new IllegalArgumentException("Default days must be greater than 0.");
        }
        this.defaultUseWithinDays = defaultUseWithinDays;
        this.archived = archived;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public String getBarcode() {
        return barcode;
    }

    public Integer getDefaultUseWithinDays() {
        return defaultUseWithinDays;
    }

    public boolean isArchived() {
        return archived;
    }

    /**
     * Suggests a target date for a batch bought on the given date.
     *
     * @param purchaseDate the purchase date
     * @return the suggested date, or empty if this product has no default days
     */
    public Optional<LocalDate> suggestTargetDate(LocalDate purchaseDate) {
        Objects.requireNonNull(purchaseDate);
        if (defaultUseWithinDays == null) {
            return Optional.empty();
        }
        return Optional.of(purchaseDate.plusDays(defaultUseWithinDays));
    }

    /**
     * Returns a copy with new editable details. The id, barcode and archived flag are kept.
     */
    public Product withDetails(String newName, Category newCategory, String newBrand, Integer newDefaultDays) {
        return new Product(id, newName, newCategory, newBrand, barcode, newDefaultDays, archived);
    }

    /**
     * Returns an archived copy of this product.
     */
    public Product archive() {
        return new Product(id, name, category, brand, barcode, defaultUseWithinDays, true);
    }

    /**
     * Returns whether this product matches a search query by name, brand or exact barcode.
     * A blank query matches every product.
     */
    public boolean matches(String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String trimmed = query.trim();
        String lower = trimmed.toLowerCase(Locale.ROOT);
        return name.toLowerCase(Locale.ROOT).contains(lower)
                || brand != null && brand.toLowerCase(Locale.ROOT).contains(lower)
                || trimmed.equals(barcode);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

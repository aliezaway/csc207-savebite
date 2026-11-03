package savebite;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;

import savebite.entity.Category;
import savebite.entity.DateSource;
import savebite.entity.Product;
import savebite.entity.Purchase;

/**
 * Shared values for tests. "Today" is Wednesday 2026-11-04.
 */
public final class TestData {
    public static final LocalDate TODAY = LocalDate.of(2026, 11, 4);
    public static final Clock CLOCK = Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

    private TestData() {
    }

    public static Product milk() {
        return new Product("milk", "Milk 1L", Category.DAIRY, null, null, 7, false);
    }

    public static Product rice() {
        return new Product("rice", "Rice (bag)", Category.GRAIN, null, null, null, false);
    }

    public static Purchase purchase(String id, String productId, String price, LocalDate bought, LocalDate target) {
        return new Purchase(id, productId, new BigDecimal(price), 1, "item", bought, target, DateSource.PACKAGE);
    }
}

package savebite.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ProductTest {
    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product("p", "  ", Category.OTHER, null, null, null, false));
    }

    @Test
    void rejectsDefaultDaysThatAreNotPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product("p", "Milk", Category.DAIRY, null, null, 0, false));
    }

    @Test
    void blankBrandAndBarcodeAreStoredAsNull() {
        Product product = new Product("p", " Milk ", Category.DAIRY, " ", "", null, false);
        assertEquals("Milk", product.getName());
        assertNull(product.getBrand());
        assertNull(product.getBarcode());
    }

    @Test
    void suggestsTargetDateFromDefaultDays() {
        Product product = new Product("p", "Milk", Category.DAIRY, null, null, 7, false);
        assertEquals(LocalDate.of(2026, 11, 9), product.suggestTargetDate(LocalDate.of(2026, 11, 2)).orElseThrow());
    }

    @Test
    void noSuggestionWithoutDefaultDays() {
        Product product = new Product("p", "Rice", Category.GRAIN, null, null, null, false);
        assertTrue(product.suggestTargetDate(LocalDate.of(2026, 11, 2)).isEmpty());
    }

    @Test
    void withDetailsKeepsIdAndBarcode() {
        Product product = new Product("p", "Milk", Category.DAIRY, null, "12345678", 7, false);
        Product edited = product.withDetails("Oat milk", Category.BEVERAGE, "Oatly", null);
        assertEquals("p", edited.getId());
        assertEquals("12345678", edited.getBarcode());
        assertEquals("Oat milk", edited.getName());
        assertNull(edited.getDefaultUseWithinDays());
        assertEquals("Milk", product.getName());
    }

    @Test
    void archiveReturnsArchivedCopy() {
        Product product = new Product("p", "Milk", Category.DAIRY, null, null, 7, false);
        assertTrue(product.archive().isArchived());
        assertFalse(product.isArchived());
    }

    @Test
    void matchesNameBrandOrExactBarcode() {
        Product product = new Product("p", "Greek Yogurt", Category.DAIRY, "Oikos", "0123456789012", 14, false);
        assertTrue(product.matches("yogurt"));
        assertTrue(product.matches("oik"));
        assertTrue(product.matches("0123456789012"));
        assertTrue(product.matches(""));
        assertFalse(product.matches("0123"));
    }
}

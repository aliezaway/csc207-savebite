package savebite.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PurchaseTest {
    private static final LocalDate BOUGHT = LocalDate.of(2026, 11, 2);

    private static Purchase batch(LocalDate target) {
        return new Purchase("b", "milk", new BigDecimal("5.98"), 2, "bottle", BOUGHT, target, DateSource.PACKAGE);
    }

    @Test
    void rejectsNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> new Purchase("b", "milk", new BigDecimal("-1"), 1,
                "item", BOUGHT, BOUGHT, DateSource.PACKAGE));
    }

    @Test
    void rejectsQuantityThatIsNotPositive() {
        assertThrows(IllegalArgumentException.class, () -> new Purchase("b", "milk", BigDecimal.ONE, 0,
                "item", BOUGHT, BOUGHT, DateSource.PACKAGE));
    }

    @Test
    void rejectsTargetDateBeforePurchaseDate() {
        assertThrows(IllegalArgumentException.class, () -> batch(BOUGHT.minusDays(1)));
    }

    @Test
    void newBatchIsInStockWithoutOutcomeDate() {
        Purchase purchase = batch(BOUGHT.plusDays(7));
        assertTrue(purchase.isInStock());
        assertNull(purchase.getOutcomeDate());
    }

    @Test
    void alertLevelsFollowTheThreeDayRule() {
        LocalDate today = LocalDate.of(2026, 11, 5);
        assertEquals(AlertLevel.PAST, batch(today.minusDays(1)).alertLevel(today));
        assertEquals(AlertLevel.USE_SOON, batch(today).alertLevel(today));
        assertEquals(AlertLevel.USE_SOON, batch(today.plusDays(3)).alertLevel(today));
        assertEquals(AlertLevel.NORMAL, batch(today.plusDays(4)).alertLevel(today));
    }

    @Test
    void passedDateDoesNotChangeStatus() {
        Purchase purchase = batch(BOUGHT.plusDays(1));
        assertEquals(AlertLevel.PAST, purchase.alertLevel(BOUGHT.plusDays(10)));
        assertTrue(purchase.isInStock());
    }

    @Test
    void markDiscardedReturnsFinishedCopy() {
        Purchase purchase = batch(BOUGHT.plusDays(7));
        Purchase discarded = purchase.markDiscarded(BOUGHT.plusDays(3));
        assertEquals(PurchaseStatus.DISCARDED, discarded.getStatus());
        assertEquals(BOUGHT.plusDays(3), discarded.getOutcomeDate());
        assertTrue(purchase.isInStock());
    }

    @Test
    void finishedBatchCannotBeFinishedAgain() {
        Purchase eaten = batch(BOUGHT.plusDays(7)).markConsumed(BOUGHT.plusDays(1));
        assertThrows(IllegalStateException.class, () -> eaten.markDiscarded(BOUGHT.plusDays(2)));
    }

    @Test
    void outcomeDateCannotBeBeforePurchaseDate() {
        assertThrows(IllegalArgumentException.class, () -> batch(BOUGHT.plusDays(7)).markConsumed(BOUGHT.minusDays(1)));
    }

    @Test
    void restoreRejectsFinishedBatchWithoutOutcomeDate() {
        assertThrows(IllegalArgumentException.class, () -> Purchase.restore("b", "milk", BigDecimal.ONE, 1, "item",
                BOUGHT, BOUGHT, DateSource.PACKAGE, PurchaseStatus.CONSUMED, null));
    }
}

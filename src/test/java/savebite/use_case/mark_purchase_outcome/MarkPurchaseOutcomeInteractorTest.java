package savebite.use_case.mark_purchase_outcome;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import savebite.TestData;
import savebite.data_access.InMemoryProductDataAccess;
import savebite.data_access.InMemoryPurchaseDataAccess;
import savebite.entity.Purchase;
import savebite.entity.PurchaseStatus;

class MarkPurchaseOutcomeInteractorTest {
    private InMemoryPurchaseDataAccess purchases;
    private RecordingPresenter presenter;
    private MarkPurchaseOutcomeInteractor interactor;

    @BeforeEach
    void setUp() {
        InMemoryProductDataAccess products = new InMemoryProductDataAccess();
        products.save(TestData.milk());
        purchases = new InMemoryPurchaseDataAccess();
        purchases.save(TestData.purchase("b", "milk", "5.98", TestData.TODAY.minusDays(2),
                TestData.TODAY.plusDays(5)));
        presenter = new RecordingPresenter();
        interactor = new MarkPurchaseOutcomeInteractor(purchases, products, presenter, TestData.CLOCK);
    }

    @Test
    void discardRecordsTodayAsOutcomeDate() {
        interactor.execute(new MarkPurchaseOutcomeInputData("b", Outcome.DISCARDED));
        Purchase purchase = purchases.findById("b").orElseThrow();
        assertEquals(PurchaseStatus.DISCARDED, purchase.getStatus());
        assertEquals(TestData.TODAY, purchase.getOutcomeDate());
        assertEquals("Milk 1L", presenter.success.getProductName());
    }

    @Test
    void batchCannotBeFinishedTwice() {
        interactor.execute(new MarkPurchaseOutcomeInputData("b", Outcome.CONSUMED));
        presenter.success = null;
        interactor.execute(new MarkPurchaseOutcomeInputData("b", Outcome.DISCARDED));
        assertNotNull(presenter.error);
        assertNull(presenter.success);
        assertEquals(PurchaseStatus.CONSUMED, purchases.findById("b").orElseThrow().getStatus());
    }

    @Test
    void passedDateCanStillBeMarkedEaten() {
        purchases.save(TestData.purchase("old", "milk", "2", TestData.TODAY.minusDays(9),
                TestData.TODAY.minusDays(1)));
        interactor.execute(new MarkPurchaseOutcomeInputData("old", Outcome.CONSUMED));
        assertEquals(PurchaseStatus.CONSUMED, purchases.findById("old").orElseThrow().getStatus());
    }

    @Test
    void missingSelectionIsAnError() {
        interactor.execute(new MarkPurchaseOutcomeInputData(null, Outcome.CONSUMED));
        assertNotNull(presenter.error);
        interactor.execute(new MarkPurchaseOutcomeInputData("nope", Outcome.CONSUMED));
        assertNotNull(presenter.error);
    }

    private static class RecordingPresenter implements MarkPurchaseOutcomeOutputBoundary {
        private MarkPurchaseOutcomeOutputData success;
        private String error;

        @Override
        public void prepareSuccessView(MarkPurchaseOutcomeOutputData outputData) {
            success = outputData;
        }

        @Override
        public void prepareFailView(String message) {
            error = message;
        }
    }
}

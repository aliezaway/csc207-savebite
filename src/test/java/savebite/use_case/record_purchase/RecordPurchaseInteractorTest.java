package savebite.use_case.record_purchase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import savebite.TestData;
import savebite.data_access.InMemoryProductDataAccess;
import savebite.data_access.InMemoryPurchaseDataAccess;
import savebite.entity.Category;
import savebite.entity.DateSource;
import savebite.entity.Product;
import savebite.entity.Purchase;
import savebite.use_case.DataAccessException;
import savebite.use_case.PurchaseDataAccessInterface;

class RecordPurchaseInteractorTest {
    private InMemoryProductDataAccess products;
    private InMemoryPurchaseDataAccess purchases;
    private RecordingPresenter presenter;
    private RecordPurchaseInteractor interactor;

    @BeforeEach
    void setUp() {
        products = new InMemoryProductDataAccess();
        purchases = new InMemoryPurchaseDataAccess();
        presenter = new RecordingPresenter();
        products.save(TestData.milk());
        products.save(TestData.rice());
        products.save(new Product("cereal", "Old cereal", Category.GRAIN, null, null, null, true));
        interactor = new RecordPurchaseInteractor(products, purchases, presenter, TestData.CLOCK);
    }

    private static RecordPurchaseInputData milkInput(String target, boolean packageDate, boolean confirmed) {
        return new RecordPurchaseInputData("milk", "5.98", "2", "bottle", "2026-11-04", target, packageDate, confirmed);
    }

    @Test
    void savesPackageDateBatch() {
        interactor.execute(milkInput("2026-11-10", true, false));

        assertNull(presenter.error);
        List<Purchase> saved = purchases.listAll();
        assertEquals(1, saved.size());
        Purchase purchase = saved.get(0);
        assertEquals(new BigDecimal("5.98"), purchase.getTotalPrice());
        assertEquals(LocalDate.of(2026, 11, 10), purchase.getTargetDate());
        assertEquals(DateSource.PACKAGE, purchase.getDateSource());
        assertTrue(purchase.isInStock());
        assertEquals("Milk 1L", presenter.success.getProductName());
    }

    @Test
    void estimateMustBeConfirmed() {
        interactor.execute(milkInput("2026-11-11", false, false));
        assertNotNull(presenter.error);
        assertTrue(purchases.listAll().isEmpty());

        interactor.execute(milkInput("2026-11-11", false, true));
        assertEquals(DateSource.USER_ESTIMATE, purchases.listAll().get(0).getDateSource());
    }

    @Test
    void rejectsTargetDateBeforePurchaseDate() {
        interactor.execute(milkInput("2026-11-01", true, false));
        assertNotNull(presenter.error);
        assertTrue(purchases.listAll().isEmpty());
    }

    @Test
    void rejectsFuturePurchaseDate() {
        interactor.execute(new RecordPurchaseInputData("milk", "5", "1", "item", "2026-11-05", "2026-11-09",
                true, false));
        assertNotNull(presenter.error);
        assertTrue(purchases.listAll().isEmpty());
    }

    @Test
    void rejectsInvalidPriceAndQuantity() {
        interactor.execute(new RecordPurchaseInputData("milk", "abc", "1", "item", "2026-11-04", "2026-11-09",
                true, false));
        assertNotNull(presenter.error);
        interactor.execute(new RecordPurchaseInputData("milk", "1.999", "1", "item", "2026-11-04", "2026-11-09",
                true, false));
        assertNotNull(presenter.error);
        interactor.execute(new RecordPurchaseInputData("milk", "1.99", "0", "item", "2026-11-04", "2026-11-09",
                true, false));
        assertNotNull(presenter.error);
        assertTrue(purchases.listAll().isEmpty());
    }

    @Test
    void archivedProductCannotBeBought() {
        interactor.execute(new RecordPurchaseInputData("cereal", "1", "1", "item", "2026-11-04", "2026-11-09",
                true, false));
        assertNotNull(presenter.error);
        assertTrue(purchases.listAll().isEmpty());
    }

    @Test
    void failedSaveReportsError() {
        RecordPurchaseInteractor failing = new RecordPurchaseInteractor(products, new FailingPurchaseDataAccess(),
                presenter, TestData.CLOCK);
        failing.execute(milkInput("2026-11-10", true, false));
        assertNotNull(presenter.error);
        assertNull(presenter.success);
    }

    @Test
    void suggestsDateFromProductDefault() {
        interactor.suggestTargetDate("milk", "2026-11-04");
        assertEquals(LocalDate.of(2026, 11, 11), presenter.suggestion.getSuggestedDate());
    }

    @Test
    void noSuggestionWhenProductHasNoDefault() {
        interactor.suggestTargetDate("rice", "2026-11-04");
        assertNull(presenter.suggestion);
        assertNotNull(presenter.noSuggestionReason);
    }

    @Test
    void loadProductsSkipsArchivedAndSortsByName() {
        interactor.loadProducts();
        assertEquals(2, presenter.options.size());
        assertEquals("Milk 1L", presenter.options.get(0).getName());
        assertEquals("Rice (bag)", presenter.options.get(1).getName());
    }

    private static class FailingPurchaseDataAccess implements PurchaseDataAccessInterface {
        @Override
        public Optional<Purchase> findById(String id) {
            return Optional.empty();
        }

        @Override
        public List<Purchase> listAll() {
            return List.of();
        }

        @Override
        public void save(Purchase purchase) throws DataAccessException {
            throw new DataAccessException("disk full");
        }
    }

    private static class RecordingPresenter implements RecordPurchaseOutputBoundary {
        private List<ProductOption> options;
        private DateSuggestionOutputData suggestion;
        private String noSuggestionReason;
        private RecordPurchaseOutputData success;
        private String error;

        @Override
        public void prepareProductOptions(List<ProductOption> productOptions) {
            options = productOptions;
        }

        @Override
        public void prepareSuggestion(DateSuggestionOutputData outputData) {
            suggestion = outputData;
        }

        @Override
        public void prepareNoSuggestion(String reason) {
            noSuggestionReason = reason;
        }

        @Override
        public void prepareSuccessView(RecordPurchaseOutputData outputData) {
            success = outputData;
        }

        @Override
        public void prepareFailView(String message) {
            error = message;
        }
    }
}

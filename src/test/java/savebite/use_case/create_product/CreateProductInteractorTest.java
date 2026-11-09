package savebite.use_case.create_product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import savebite.data_access.InMemoryProductDataAccess;
import savebite.entity.Category;
import savebite.entity.Product;

class CreateProductInteractorTest {
    private InMemoryProductDataAccess products;
    private FakeGateway gateway;
    private RecordingPresenter presenter;
    private CreateProductInteractor interactor;

    @BeforeEach
    void setUp() {
        products = new InMemoryProductDataAccess();
        gateway = new FakeGateway();
        presenter = new RecordingPresenter();
        interactor = new CreateProductInteractor(products, gateway, presenter);
    }

    @Test
    void lookupFillsDetailsButSavesNothing() {
        gateway.result = Optional.of(new ProductLookupResult("Greek Yogurt", "Oikos"));
        interactor.lookup("0123456789012");
        assertEquals("Greek Yogurt", presenter.lookup.getName());
        assertEquals("Oikos", presenter.lookup.getBrand());
        assertTrue(products.listAll().isEmpty());
    }

    @Test
    void lookupRejectsInvalidBarcodeWithoutCallingApi() {
        interactor.lookup("12ab");
        assertNotNull(presenter.lookupError);
        assertEquals(0, gateway.calls);
    }

    @Test
    void lookupNotFound() {
        gateway.result = Optional.empty();
        interactor.lookup("12345678");
        assertNotNull(presenter.lookupError);
    }

    @Test
    void lookupFailureLetsUserTypeDetails() {
        gateway.failure = new ProductLookupException("Could not reach the food database.");
        interactor.lookup("12345678");
        assertTrue(presenter.lookupError.contains("fill in the details yourself"));
    }

    @Test
    void lookupWithNoNameOrBrandIsTreatedAsMissing() {
        gateway.result = Optional.of(new ProductLookupResult(null, null));
        interactor.lookup("12345678");
        assertNull(presenter.lookup);
        assertNotNull(presenter.lookupError);
    }

    @Test
    void lookupOfKnownBarcodeSuggestsExistingProduct() {
        products.save(new Product("y", "Yogurt", Category.DAIRY, null, "12345678", 14, false));
        interactor.lookup("12345678");
        assertTrue(presenter.lookupError.contains("Yogurt"));
        assertEquals(0, gateway.calls);
    }

    @Test
    void createsProductManually() {
        interactor.execute(new CreateProductInputData("Kimchi", "VEGETABLE", "", "", "30", false));
        assertNull(presenter.error);
        Product created = products.findById(presenter.created.getProductId()).orElseThrow();
        assertEquals("Kimchi", created.getName());
        assertEquals(Category.VEGETABLE, created.getCategory());
        assertEquals(30, created.getDefaultUseWithinDays());
    }

    @Test
    void defaultDaysAreOptional() {
        interactor.execute(new CreateProductInputData("Honey", "OTHER", "", "", "", false));
        assertNull(products.findById(presenter.created.getProductId()).orElseThrow().getDefaultUseWithinDays());
    }

    @Test
    void rejectsInvalidDays() {
        interactor.execute(new CreateProductInputData("Honey", "OTHER", "", "", "-2", false));
        assertNotNull(presenter.error);
        assertTrue(products.listAll().isEmpty());
    }

    @Test
    void rejectsDuplicateBarcode() {
        products.save(new Product("y", "Yogurt", Category.DAIRY, null, "12345678", 14, false));
        interactor.execute(new CreateProductInputData("Other yogurt", "DAIRY", "", "12345678", "", false));
        assertNotNull(presenter.error);
        assertEquals(1, products.listAll().size());
    }

    @Test
    void duplicateNameNeedsConfirmation() {
        products.save(new Product("m", "Milk 1L", Category.DAIRY, null, null, 7, false));

        interactor.execute(new CreateProductInputData("milk 1l", "DAIRY", "Natrel", "", "7", false));
        assertNotNull(presenter.duplicateWarning);
        assertEquals(1, products.listAll().size());

        interactor.execute(new CreateProductInputData("milk 1l", "DAIRY", "Natrel", "", "7", true));
        assertEquals(2, products.listAll().size());
    }

    private static class FakeGateway implements ProductLookupGateway {
        private Optional<ProductLookupResult> result = Optional.empty();
        private ProductLookupException failure;
        private int calls;

        @Override
        public Optional<ProductLookupResult> lookup(String barcode) throws ProductLookupException {
            calls++;
            if (failure != null) {
                throw failure;
            }
            return result;
        }
    }

    private static class RecordingPresenter implements CreateProductOutputBoundary {
        private ProductLookupOutputData lookup;
        private String lookupError;
        private String duplicateWarning;
        private CreateProductOutputData created;
        private String error;

        @Override
        public void prepareLookupResult(ProductLookupOutputData outputData) {
            lookup = outputData;
        }

        @Override
        public void prepareLookupFailView(String message) {
            lookupError = message;
        }

        @Override
        public void prepareDuplicateNameWarning(String message) {
            duplicateWarning = message;
        }

        @Override
        public void prepareSuccessView(CreateProductOutputData outputData) {
            created = outputData;
        }

        @Override
        public void prepareFailView(String message) {
            error = message;
        }
    }
}

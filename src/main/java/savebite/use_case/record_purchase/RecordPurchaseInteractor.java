package savebite.use_case.record_purchase;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import savebite.entity.DateSource;
import savebite.entity.Product;
import savebite.entity.Purchase;
import savebite.use_case.DataAccessException;
import savebite.use_case.InputParser;
import savebite.use_case.InvalidInputException;
import savebite.use_case.ProductDataAccessInterface;
import savebite.use_case.ProductNameComparator;
import savebite.use_case.PurchaseDataAccessInterface;

/**
 * Records a new purchase batch (UC1).
 */
public class RecordPurchaseInteractor implements RecordPurchaseInputBoundary {
    private final ProductDataAccessInterface productDataAccess;
    private final PurchaseDataAccessInterface purchaseDataAccess;
    private final RecordPurchaseOutputBoundary presenter;
    private final Clock clock;

    public RecordPurchaseInteractor(ProductDataAccessInterface productDataAccess,
                                    PurchaseDataAccessInterface purchaseDataAccess,
                                    RecordPurchaseOutputBoundary presenter, Clock clock) {
        this.productDataAccess = productDataAccess;
        this.purchaseDataAccess = purchaseDataAccess;
        this.presenter = presenter;
        this.clock = clock;
    }

    @Override
    public void loadProducts() {
        List<Product> activeProducts = new ArrayList<>();
        for (Product product : productDataAccess.listAll()) {
            if (!product.isArchived()) {
                activeProducts.add(product);
            }
        }
        activeProducts.sort(new ProductNameComparator());

        List<ProductOption> options = new ArrayList<>();
        for (Product product : activeProducts) {
            options.add(new ProductOption(product.getId(), product.getName(), product.getBrand()));
        }
        presenter.prepareProductOptions(options);
    }

    @Override
    public void suggestTargetDate(String productId, String purchaseDateText) {
        Optional<Product> product = findActiveProduct(productId);
        if (product.isEmpty()) {
            presenter.prepareNoSuggestion("Choose a product first.");
            return;
        }
        LocalDate purchaseDate;
        try {
            purchaseDate = InputParser.parseDate(purchaseDateText, "Purchase date");
        } catch (InvalidInputException e) {
            presenter.prepareNoSuggestion("Enter a valid purchase date first.");
            return;
        }
        Optional<LocalDate> suggestion = product.get().suggestTargetDate(purchaseDate);
        if (suggestion.isEmpty()) {
            presenter.prepareNoSuggestion("This product has no default days. Enter the date yourself.");
            return;
        }
        presenter.prepareSuggestion(new DateSuggestionOutputData(suggestion.get(),
                product.get().getDefaultUseWithinDays()));
    }

    @Override
    public void execute(RecordPurchaseInputData inputData) {
        Optional<Product> product = findActiveProduct(inputData.getProductId());
        if (product.isEmpty()) {
            presenter.prepareFailView("Choose a product. If it is not in the list, create it first.");
            return;
        }
        if (!inputData.isPackageDate() && !inputData.isEstimateConfirmed()) {
            presenter.prepareFailView("Please check the estimated date and confirm it before saving.");
            return;
        }

        Purchase purchase;
        try {
            BigDecimal price = InputParser.parsePrice(inputData.getTotalPrice());
            double quantity = InputParser.parseQuantity(inputData.getQuantity());
            LocalDate purchaseDate = InputParser.parseDate(inputData.getPurchaseDate(), "Purchase date");
            LocalDate targetDate = InputParser.parseDate(inputData.getTargetDate(), "Target date");
            if (purchaseDate.isAfter(LocalDate.now(clock))) {
                presenter.prepareFailView("The purchase date cannot be in the future.");
                return;
            }
            if (targetDate.isBefore(purchaseDate)) {
                presenter.prepareFailView("The target date cannot be before the purchase date. "
                        + "SaveBite does not record food whose date had already passed when it was bought.");
                return;
            }
            DateSource source = inputData.isPackageDate() ? DateSource.PACKAGE : DateSource.USER_ESTIMATE;
            purchase = new Purchase(UUID.randomUUID().toString(), product.get().getId(), price, quantity,
                    inputData.getUnit(), purchaseDate, targetDate, source);
        } catch (InvalidInputException | IllegalArgumentException e) {
            presenter.prepareFailView(e.getMessage());
            return;
        }

        try {
            purchaseDataAccess.save(purchase);
        } catch (DataAccessException e) {
            presenter.prepareFailView("Could not save the purchase. Your input was kept, so you can try again. ("
                    + e.getMessage() + ")");
            return;
        }
        presenter.prepareSuccessView(new RecordPurchaseOutputData(purchase.getId(), product.get().getName(),
                purchase.getTargetDate()));
    }

    private Optional<Product> findActiveProduct(String productId) {
        if (productId == null || productId.isBlank()) {
            return Optional.empty();
        }
        Optional<Product> product = productDataAccess.findById(productId);
        if (product.isPresent() && product.get().isArchived()) {
            return Optional.empty();
        }
        return product;
    }
}

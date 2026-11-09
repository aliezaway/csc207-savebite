package savebite.use_case.create_product;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import savebite.entity.Category;
import savebite.entity.Product;
import savebite.use_case.DataAccessException;
import savebite.use_case.InputParser;
import savebite.use_case.InvalidInputException;
import savebite.use_case.ProductDataAccessInterface;

/**
 * Creates a product, optionally filling in details from a barcode lookup (UC2).
 */
public class CreateProductInteractor implements CreateProductInputBoundary {
    private static final Pattern BARCODE = Pattern.compile("\\d{8,14}");

    private final ProductDataAccessInterface productDataAccess;
    private final ProductLookupGateway lookupGateway;
    private final CreateProductOutputBoundary presenter;

    public CreateProductInteractor(ProductDataAccessInterface productDataAccess,
                                   ProductLookupGateway lookupGateway,
                                   CreateProductOutputBoundary presenter) {
        this.productDataAccess = productDataAccess;
        this.lookupGateway = lookupGateway;
        this.presenter = presenter;
    }

    @Override
    public void lookup(String barcode) {
        String code = barcode == null ? "" : barcode.trim();
        if (!BARCODE.matcher(code).matches()) {
            presenter.prepareLookupFailView("A barcode must be 8 to 14 digits.");
            return;
        }
        Optional<Product> existing = productDataAccess.findByBarcode(code);
        if (existing.isPresent()) {
            presenter.prepareLookupFailView("You already have a product with this barcode: "
                    + existing.get().getName() + ". Use that product instead.");
            return;
        }

        Optional<ProductLookupResult> result;
        try {
            result = lookupGateway.lookup(code);
        } catch (ProductLookupException e) {
            presenter.prepareLookupFailView(e.getMessage() + " You can fill in the details yourself.");
            return;
        }
        if (result.isEmpty()) {
            presenter.prepareLookupFailView("No product was found for this barcode. "
                    + "You can fill in the details yourself.");
            return;
        }
        if (result.get().getName() == null && result.get().getBrand() == null) {
            presenter.prepareLookupFailView("The product was found but has no name or brand. "
                    + "Please fill in the details yourself.");
            return;
        }
        presenter.prepareLookupResult(new ProductLookupOutputData(code, result.get().getName(),
                result.get().getBrand()));
    }

    @Override
    public void execute(CreateProductInputData inputData) {
        String barcode = inputData.getBarcode() == null ? "" : inputData.getBarcode().trim();
        if (!barcode.isEmpty() && !BARCODE.matcher(barcode).matches()) {
            presenter.prepareFailView("A barcode must be 8 to 14 digits, or left blank.");
            return;
        }
        if (!barcode.isEmpty()) {
            Optional<Product> existing = productDataAccess.findByBarcode(barcode);
            if (existing.isPresent()) {
                presenter.prepareFailView("You already have a product with this barcode: "
                        + existing.get().getName() + ". Use that product instead.");
                return;
            }
        }

        Product product;
        try {
            Category category = parseCategory(inputData.getCategory());
            Integer days = InputParser.parseOptionalDays(inputData.getDefaultDays());
            product = new Product(UUID.randomUUID().toString(), inputData.getName(), category,
                    inputData.getBrand(), barcode, days, false);
        } catch (InvalidInputException | IllegalArgumentException e) {
            presenter.prepareFailView(e.getMessage());
            return;
        }

        if (!inputData.isConfirmDuplicateName() && nameExists(product.getName())) {
            presenter.prepareDuplicateNameWarning("A product named \"" + product.getName()
                    + "\" already exists. Create another one anyway?");
            return;
        }

        try {
            productDataAccess.save(product);
        } catch (DataAccessException e) {
            presenter.prepareFailView("Could not save the product. Your input was kept, so you can try again. ("
                    + e.getMessage() + ")");
            return;
        }
        presenter.prepareSuccessView(new CreateProductOutputData(product.getId(), product.getName()));
    }

    private boolean nameExists(String name) {
        for (Product product : productDataAccess.listAll()) {
            if (product.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private static Category parseCategory(String text) throws InvalidInputException {
        if (text == null || text.isBlank()) {
            throw new InvalidInputException("Category is required.");
        }
        try {
            return Category.valueOf(text.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Unknown category: " + text);
        }
    }
}

package savebite.interface_adapter.create_product;

import savebite.use_case.create_product.CreateProductInputBoundary;
import savebite.use_case.create_product.CreateProductInputData;

/**
 * Passes new product form actions to the create product use case.
 */
public class CreateProductController {
    private final CreateProductInputBoundary interactor;

    public CreateProductController(CreateProductInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Looks up a barcode. This calls the network, so views should call it off the Swing thread.
     */
    public void lookup(String barcode) {
        interactor.lookup(barcode);
    }

    public void execute(String name, String category, String brand, String barcode, String defaultDays,
                        boolean confirmDuplicateName) {
        interactor.execute(new CreateProductInputData(name, category, brand, barcode, defaultDays,
                confirmDuplicateName));
    }
}

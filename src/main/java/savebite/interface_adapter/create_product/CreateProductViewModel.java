package savebite.interface_adapter.create_product;

import savebite.interface_adapter.ViewModel;

/**
 * View model of the new product form.
 * Fires "lookup", "lookupError", "duplicate", "created" and "error".
 */
public class CreateProductViewModel extends ViewModel<CreateProductState> {
    public CreateProductViewModel() {
        super("create product");
        setState(new CreateProductState());
    }
}

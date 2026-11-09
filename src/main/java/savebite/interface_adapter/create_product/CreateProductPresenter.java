package savebite.interface_adapter.create_product;

import savebite.use_case.create_product.CreateProductOutputBoundary;
import savebite.use_case.create_product.CreateProductOutputData;
import savebite.use_case.create_product.ProductLookupOutputData;

/**
 * Turns create product results into text for the new product form.
 */
public class CreateProductPresenter implements CreateProductOutputBoundary {
    private final CreateProductViewModel viewModel;

    public CreateProductPresenter(CreateProductViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareLookupResult(ProductLookupOutputData outputData) {
        CreateProductState state = viewModel.getState();
        state.setLookedUpName(outputData.getName());
        state.setLookedUpBrand(outputData.getBrand());
        if (outputData.getName() == null) {
            state.setLookupMessage("Found the brand but no name. Please type the name, then choose a category.");
        } else if (outputData.getBrand() == null) {
            state.setLookupMessage("Found the name but no brand. Choose a category and save.");
        } else {
            state.setLookupMessage("Found it. Check the details, choose a category and save.");
        }
        viewModel.firePropertyChanged("lookup");
    }

    @Override
    public void prepareLookupFailView(String message) {
        CreateProductState state = viewModel.getState();
        state.setLookedUpName(null);
        state.setLookedUpBrand(null);
        state.setLookupMessage(message);
        viewModel.firePropertyChanged("lookupError");
    }

    @Override
    public void prepareDuplicateNameWarning(String message) {
        viewModel.getState().setDuplicateWarning(message);
        viewModel.firePropertyChanged("duplicate");
    }

    @Override
    public void prepareSuccessView(CreateProductOutputData outputData) {
        CreateProductState state = viewModel.getState();
        state.setError("");
        state.setCreatedProductId(outputData.getProductId());
        viewModel.firePropertyChanged("created");
    }

    @Override
    public void prepareFailView(String error) {
        viewModel.getState().setError(error);
        viewModel.firePropertyChanged("error");
    }
}

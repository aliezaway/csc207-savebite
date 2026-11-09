package savebite.interface_adapter.record_purchase;

import java.util.ArrayList;
import java.util.List;

import savebite.use_case.record_purchase.DateSuggestionOutputData;
import savebite.use_case.record_purchase.ProductOption;
import savebite.use_case.record_purchase.RecordPurchaseOutputBoundary;
import savebite.use_case.record_purchase.RecordPurchaseOutputData;

/**
 * Turns record purchase results into text for the purchase form.
 */
public class RecordPurchasePresenter implements RecordPurchaseOutputBoundary {
    private final RecordPurchaseViewModel viewModel;

    public RecordPurchasePresenter(RecordPurchaseViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareProductOptions(List<ProductOption> options) {
        List<ProductChoice> choices = new ArrayList<>();
        for (ProductOption option : options) {
            String label = option.getName();
            if (option.getBrand() != null) {
                label = label + " (" + option.getBrand() + ")";
            }
            choices.add(new ProductChoice(option.getProductId(), label));
        }
        viewModel.getState().setProductChoices(choices);
        viewModel.firePropertyChanged("products");
    }

    @Override
    public void prepareSuggestion(DateSuggestionOutputData suggestion) {
        RecordPurchaseState state = viewModel.getState();
        state.setSuggestedTargetDate(suggestion.getSuggestedDate().toString());
        state.setSuggestionMessage("Estimated from the product default (" + suggestion.getDefaultDays()
                + " days). This is only a reference value: check it before saving.");
        viewModel.firePropertyChanged("suggestion");
    }

    @Override
    public void prepareNoSuggestion(String reason) {
        RecordPurchaseState state = viewModel.getState();
        state.setSuggestedTargetDate(null);
        state.setSuggestionMessage(reason);
        viewModel.firePropertyChanged("suggestion");
    }

    @Override
    public void prepareSuccessView(RecordPurchaseOutputData outputData) {
        RecordPurchaseState state = viewModel.getState();
        state.setError("");
        state.setSuccessMessage("Saved " + outputData.getProductName() + ", use by " + outputData.getTargetDate()
                + ".");
        viewModel.firePropertyChanged("saved");
    }

    @Override
    public void prepareFailView(String error) {
        viewModel.getState().setError(error);
        viewModel.firePropertyChanged("error");
    }
}

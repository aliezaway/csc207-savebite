package savebite.interface_adapter.mark_purchase_outcome;

import savebite.interface_adapter.MoneyFormat;
import savebite.use_case.mark_purchase_outcome.MarkPurchaseOutcomeOutputBoundary;
import savebite.use_case.mark_purchase_outcome.MarkPurchaseOutcomeOutputData;
import savebite.use_case.mark_purchase_outcome.Outcome;

/**
 * Turns mark outcome results into messages.
 */
public class MarkPurchaseOutcomePresenter implements MarkPurchaseOutcomeOutputBoundary {
    private final MarkOutcomeViewModel viewModel;

    public MarkPurchaseOutcomePresenter(MarkOutcomeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(MarkPurchaseOutcomeOutputData outputData) {
        MarkOutcomeState state = viewModel.getState();
        state.setError("");
        if (outputData.getOutcome() == Outcome.CONSUMED) {
            state.setMessage("Marked the whole batch of " + outputData.getProductName() + " as eaten.");
        } else {
            state.setMessage("Marked the whole batch of " + outputData.getProductName() + " as discarded ("
                    + MoneyFormat.format(outputData.getTotalPrice()) + ").");
        }
        viewModel.firePropertyChanged("success");
    }

    @Override
    public void prepareFailView(String error) {
        viewModel.getState().setError(error);
        viewModel.firePropertyChanged("error");
    }
}

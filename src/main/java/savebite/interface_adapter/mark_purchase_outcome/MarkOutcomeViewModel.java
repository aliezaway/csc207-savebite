package savebite.interface_adapter.mark_purchase_outcome;

import savebite.interface_adapter.ViewModel;

/**
 * View model of the eaten / discarded buttons. Fires "success" and "error".
 */
public class MarkOutcomeViewModel extends ViewModel<MarkOutcomeState> {
    public MarkOutcomeViewModel() {
        super("mark outcome");
        setState(new MarkOutcomeState());
    }
}

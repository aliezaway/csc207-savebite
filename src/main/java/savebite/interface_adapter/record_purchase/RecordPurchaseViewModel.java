package savebite.interface_adapter.record_purchase;

import savebite.interface_adapter.ViewModel;

/**
 * View model of the purchase form. Fires "products", "suggestion", "saved" and "error".
 */
public class RecordPurchaseViewModel extends ViewModel<RecordPurchaseState> {
    public RecordPurchaseViewModel() {
        super("record purchase");
        setState(new RecordPurchaseState());
    }
}

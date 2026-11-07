package savebite.use_case.record_purchase;

import java.util.List;

/**
 * Output boundary for recording a purchase batch.
 */
public interface RecordPurchaseOutputBoundary {
    void prepareProductOptions(List<ProductOption> options);

    void prepareSuggestion(DateSuggestionOutputData suggestion);

    void prepareNoSuggestion(String reason);

    void prepareSuccessView(RecordPurchaseOutputData outputData);

    void prepareFailView(String error);
}

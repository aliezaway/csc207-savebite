package savebite.use_case.record_purchase;

import java.time.LocalDate;

/**
 * A target date estimated from the product default.
 */
public class DateSuggestionOutputData {
    private final LocalDate suggestedDate;
    private final int defaultDays;

    public DateSuggestionOutputData(LocalDate suggestedDate, int defaultDays) {
        this.suggestedDate = suggestedDate;
        this.defaultDays = defaultDays;
    }

    public LocalDate getSuggestedDate() {
        return suggestedDate;
    }

    public int getDefaultDays() {
        return defaultDays;
    }
}

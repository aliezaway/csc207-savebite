package savebite.interface_adapter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Formats CAD amounts for display.
 */
public final class MoneyFormat {
    private MoneyFormat() {
    }

    public static String format(BigDecimal amount) {
        return "$" + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}

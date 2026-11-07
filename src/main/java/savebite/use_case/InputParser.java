package savebite.use_case;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Turns text typed by the user into values, with messages the user can act on.
 */
public final class InputParser {
    private InputParser() {
    }

    /**
     * Parses a price in CAD with at most two decimal places.
     */
    public static BigDecimal parsePrice(String text) throws InvalidInputException {
        if (text == null || text.isBlank()) {
            throw new InvalidInputException("Price is required.");
        }
        BigDecimal price;
        try {
            price = new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Price must be a number, for example 4.99.");
        }
        if (price.signum() < 0) {
            throw new InvalidInputException("Price cannot be negative.");
        }
        if (price.stripTrailingZeros().scale() > 2) {
            throw new InvalidInputException("Price can have at most 2 decimal places.");
        }
        return price;
    }

    /**
     * Parses a quantity greater than 0.
     */
    public static double parseQuantity(String text) throws InvalidInputException {
        if (text == null || text.isBlank()) {
            throw new InvalidInputException("Quantity is required.");
        }
        double quantity;
        try {
            quantity = Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Quantity must be a number, for example 2 or 0.5.");
        }
        if (!(quantity > 0) || Double.isInfinite(quantity)) {
            throw new InvalidInputException("Quantity must be greater than 0.");
        }
        return quantity;
    }

    /**
     * Parses a date written as YYYY-MM-DD.
     *
     * @param fieldName the field name used in the error message
     */
    public static LocalDate parseDate(String text, String fieldName) throws InvalidInputException {
        if (text == null || text.isBlank()) {
            throw new InvalidInputException(fieldName + " is required.");
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new InvalidInputException(fieldName + " must be written as YYYY-MM-DD.");
        }
    }

    /**
     * Parses an optional number of default days. Blank text means no default.
     */
    public static Integer parseOptionalDays(String text) throws InvalidInputException {
        if (text == null || text.isBlank()) {
            return null;
        }
        int days;
        try {
            days = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Default days must be a whole number, or left blank.");
        }
        if (days <= 0) {
            throw new InvalidInputException("Default days must be greater than 0.");
        }
        return days;
    }
}

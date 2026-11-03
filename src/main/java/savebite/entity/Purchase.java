package savebite.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * One batch of a product bought on one day. Every item in a batch shares the same unit and
 * target date, and the whole batch is either eaten or discarded. Purchases are immutable.
 */
public class Purchase {
    /** A batch whose target date is at most this many days away should be used soon. */
    public static final int USE_SOON_DAYS = 3;

    private final String id;
    private final String productId;
    private final BigDecimal totalPrice;
    private final double quantity;
    private final String unit;
    private final LocalDate purchaseDate;
    private final LocalDate targetDate;
    private final DateSource dateSource;
    private final PurchaseStatus status;
    private final LocalDate outcomeDate;

    /**
     * Creates a new in-stock purchase batch.
     *
     * @throws IllegalArgumentException if a value is invalid
     */
    public Purchase(String id, String productId, BigDecimal totalPrice, double quantity, String unit,
                    LocalDate purchaseDate, LocalDate targetDate, DateSource dateSource) {
        this(id, productId, totalPrice, quantity, unit, purchaseDate, targetDate, dateSource,
                PurchaseStatus.IN_STOCK, null);
    }

    private Purchase(String id, String productId, BigDecimal totalPrice, double quantity, String unit,
                     LocalDate purchaseDate, LocalDate targetDate, DateSource dateSource,
                     PurchaseStatus status, LocalDate outcomeDate) {
        this.id = requireText(id, "Purchase id");
        this.productId = requireText(productId, "Product");
        this.totalPrice = Objects.requireNonNull(totalPrice, "Price is required.");
        if (totalPrice.signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (!(quantity > 0) || Double.isInfinite(quantity)) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }
        this.quantity = quantity;
        this.unit = requireText(unit, "Unit");
        this.purchaseDate = Objects.requireNonNull(purchaseDate, "Purchase date is required.");
        this.targetDate = Objects.requireNonNull(targetDate, "Target date is required.");
        if (targetDate.isBefore(purchaseDate)) {
            throw new IllegalArgumentException("The target date cannot be before the purchase date.");
        }
        this.dateSource = Objects.requireNonNull(dateSource, "Date source is required.");
        this.status = Objects.requireNonNull(status, "Status is required.");
        if (status == PurchaseStatus.IN_STOCK && outcomeDate != null) {
            throw new IllegalArgumentException("An in-stock batch cannot have an outcome date.");
        }
        if (status != PurchaseStatus.IN_STOCK && outcomeDate == null) {
            throw new IllegalArgumentException("A finished batch needs an outcome date.");
        }
        if (outcomeDate != null && outcomeDate.isBefore(purchaseDate)) {
            throw new IllegalArgumentException("The outcome date cannot be before the purchase date.");
        }
        this.outcomeDate = outcomeDate;
    }

    /**
     * Rebuilds a stored purchase, including its status and outcome date.
     *
     * @throws IllegalArgumentException if the stored values are inconsistent
     */
    public static Purchase restore(String id, String productId, BigDecimal totalPrice, double quantity,
                                   String unit, LocalDate purchaseDate, LocalDate targetDate,
                                   DateSource dateSource, PurchaseStatus status, LocalDate outcomeDate) {
        return new Purchase(id, productId, totalPrice, quantity, unit, purchaseDate, targetDate,
                dateSource, status, outcomeDate);
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public DateSource getDateSource() {
        return dateSource;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    /**
     * Returns the day the batch was eaten or discarded, or null while it is in stock.
     */
    public LocalDate getOutcomeDate() {
        return outcomeDate;
    }

    public boolean isInStock() {
        return status == PurchaseStatus.IN_STOCK;
    }

    /**
     * Returns the number of days from today until the target date; negative once it has passed.
     */
    public long daysUntilTarget(LocalDate today) {
        return ChronoUnit.DAYS.between(today, targetDate);
    }

    /**
     * Returns how urgently this batch should be used. A passed date never changes the status by itself.
     */
    public AlertLevel alertLevel(LocalDate today) {
        long days = daysUntilTarget(today);
        if (days < 0) {
            return AlertLevel.PAST;
        }
        if (days <= USE_SOON_DAYS) {
            return AlertLevel.USE_SOON;
        }
        return AlertLevel.NORMAL;
    }

    /**
     * Returns a copy of this batch marked as eaten on the given day.
     *
     * @throws IllegalStateException    if the batch was already finished
     * @throws IllegalArgumentException if the date is before the purchase date
     */
    public Purchase markConsumed(LocalDate date) {
        return finish(PurchaseStatus.CONSUMED, date);
    }

    /**
     * Returns a copy of this batch marked as discarded on the given day.
     *
     * @throws IllegalStateException    if the batch was already finished
     * @throws IllegalArgumentException if the date is before the purchase date
     */
    public Purchase markDiscarded(LocalDate date) {
        return finish(PurchaseStatus.DISCARDED, date);
    }

    private Purchase finish(PurchaseStatus newStatus, LocalDate date) {
        if (!isInStock()) {
            throw new IllegalStateException("This batch has already been marked as "
                    + (status == PurchaseStatus.CONSUMED ? "eaten." : "discarded."));
        }
        Objects.requireNonNull(date, "Outcome date is required.");
        return new Purchase(id, productId, totalPrice, quantity, unit, purchaseDate, targetDate,
                dateSource, newStatus, date);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }
}

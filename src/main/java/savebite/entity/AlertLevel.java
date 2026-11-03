package savebite.entity;

/**
 * How urgently an in-stock batch should be used.
 */
public enum AlertLevel {
    /** The target date is before today. */
    PAST,
    /** The target date is today or within the next few days. */
    USE_SOON,
    /** The target date is further away. */
    NORMAL
}

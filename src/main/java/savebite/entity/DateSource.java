package savebite.entity;

/**
 * Where the target date of a purchase batch came from.
 */
public enum DateSource {
    /** The date printed on the package. */
    PACKAGE,
    /** A date estimated from the product default and confirmed by the user. */
    USER_ESTIMATE
}

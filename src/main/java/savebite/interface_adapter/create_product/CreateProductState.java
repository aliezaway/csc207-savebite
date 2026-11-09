package savebite.interface_adapter.create_product;

/**
 * State of the new product form.
 */
public class CreateProductState {
    private String lookedUpName;
    private String lookedUpBrand;
    private String lookupMessage = "";
    private String duplicateWarning = "";
    private String error = "";
    private String createdProductId;

    /**
     * Returns the name found by the barcode lookup, or null if none was found.
     */
    public String getLookedUpName() {
        return lookedUpName;
    }

    public void setLookedUpName(String lookedUpName) {
        this.lookedUpName = lookedUpName;
    }

    /**
     * Returns the brand found by the barcode lookup, or null if none was found.
     */
    public String getLookedUpBrand() {
        return lookedUpBrand;
    }

    public void setLookedUpBrand(String lookedUpBrand) {
        this.lookedUpBrand = lookedUpBrand;
    }

    public String getLookupMessage() {
        return lookupMessage;
    }

    public void setLookupMessage(String lookupMessage) {
        this.lookupMessage = lookupMessage;
    }

    public String getDuplicateWarning() {
        return duplicateWarning;
    }

    public void setDuplicateWarning(String duplicateWarning) {
        this.duplicateWarning = duplicateWarning;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCreatedProductId() {
        return createdProductId;
    }

    public void setCreatedProductId(String createdProductId) {
        this.createdProductId = createdProductId;
    }
}

package savebite.data_access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import savebite.use_case.create_product.ProductLookupException;
import savebite.use_case.create_product.ProductLookupResult;

class OpenFoodFactsGatewayTest {
    @Test
    void parsesFoundProduct() throws ProductLookupException {
        String body = "{\"code\":\"3017620422003\",\"status\":1,"
                + "\"product\":{\"product_name\":\"Nutella\",\"brands\":\"Ferrero\"}}";
        ProductLookupResult result = OpenFoodFactsGateway.parse(body).orElseThrow();
        assertEquals("Nutella", result.getName());
        assertEquals("Ferrero", result.getBrand());
    }

    @Test
    void missingFieldsBecomeNull() throws ProductLookupException {
        ProductLookupResult result = OpenFoodFactsGateway.parse(
                "{\"status\":1,\"product\":{\"product_name\":\"  \"}}").orElseThrow();
        assertNull(result.getName());
        assertNull(result.getBrand());
    }

    @Test
    void statusZeroMeansNotFound() throws ProductLookupException {
        assertTrue(OpenFoodFactsGateway.parse("{\"status\":0,\"status_verbose\":\"product not found\"}").isEmpty());
    }

    @Test
    void unreadableBodyIsAnError() {
        assertThrows(ProductLookupException.class, () -> OpenFoodFactsGateway.parse("<html>"));
    }

    /**
     * Calls the real API. Run with: mvn test -Dsavebite.liveApi=true -Dtest=OpenFoodFactsGatewayTest
     */
    @Test
    @EnabledIfSystemProperty(named = "savebite.liveApi", matches = "true")
    void liveLookupOfKnownBarcode() throws ProductLookupException {
        Optional<ProductLookupResult> result = new OpenFoodFactsGateway().lookup("3017620422003");
        assertTrue(result.isPresent());
        System.out.println("Live lookup result: " + result.get().getName() + " / " + result.get().getBrand());
    }
}

package savebite.data_access;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;

import savebite.use_case.create_product.ProductLookupException;
import savebite.use_case.create_product.ProductLookupGateway;
import savebite.use_case.create_product.ProductLookupResult;

/**
 * Looks up products by barcode with the Open Food Facts API
 * (https://openfoodfacts.github.io/openfoodfacts-server/api/).
 * The API asks every app to send a custom User-Agent. Lookups only happen when the user clicks a button.
 */
public class OpenFoodFactsGateway implements ProductLookupGateway {
    private static final String BASE_URL = "https://world.openfoodfacts.org/api/v2/product/";
    private static final String USER_AGENT = "SaveBite/1.0 (CSC207 student project)";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient client;

    public OpenFoodFactsGateway() {
        this.client = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    @Override
    public Optional<ProductLookupResult> lookup(String barcode) throws ProductLookupException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + barcode + "?fields=product_name,brands"))
                .timeout(TIMEOUT)
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException e) {
            throw new ProductLookupException("The food database did not answer in time.", e);
        } catch (IOException e) {
            throw new ProductLookupException("Could not reach the food database.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProductLookupException("The lookup was interrupted.", e);
        }
        if (response.statusCode() == 404) {
            return Optional.empty();
        }
        if (response.statusCode() != 200) {
            throw new ProductLookupException("The food database returned an error (HTTP "
                    + response.statusCode() + ").");
        }
        return parse(response.body());
    }

    /**
     * Reads an Open Food Facts product response.
     *
     * @return the details, or empty if the response says the product was not found
     * @throws ProductLookupException if the body is not the expected JSON
     */
    static Optional<ProductLookupResult> parse(String body) throws ProductLookupException {
        try {
            JSONObject json = new JSONObject(body);
            JSONObject product = json.optJSONObject("product");
            if (json.optInt("status", 0) != 1 || product == null) {
                return Optional.empty();
            }
            return Optional.of(new ProductLookupResult(blankToNull(product.optString("product_name", null)),
                    blankToNull(product.optString("brands", null))));
        } catch (JSONException e) {
            throw new ProductLookupException("The food database sent an answer SaveBite could not read.", e);
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

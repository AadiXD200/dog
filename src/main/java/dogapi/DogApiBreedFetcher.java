package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // Basic validation
        if (breed == null || breed.trim().isEmpty()) {
            throw new BreedNotFoundException("Breed must be a non-empty string.");
        }

        final String normalized = breed.trim().toLowerCase();
        final String url = "https://dog.ceo/api/breed/" + normalized + "/list";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new BreedNotFoundException("Empty response from API.");
            }

            String body = response.body().string();

            // Parse JSON
            JSONObject json = new JSONObject(body);
            String status = json.optString("status", "");

            // Dog CEO returns "success" or "error" with message/code
            if (!"success".equalsIgnoreCase(status)) {
                String apiMessage = json.optString("message", "Unknown error.");
                throw new BreedNotFoundException(apiMessage);
            }

            JSONArray subBreedsJson = json.getJSONArray("message");
            List<String> subBreeds = new ArrayList<>(subBreedsJson.length());
            for (int i = 0; i < subBreedsJson.length(); i++) {
                subBreeds.add(subBreedsJson.getString(i));
            }
            return subBreeds;

        } catch (IOException e) {
            // Any network/HTTP failure is reported as BreedNotFoundException per spec
            throw new BreedNotFoundException("Failed to fetch breed info: " + e.getMessage());
        } catch (Exception e) {
            // Covers unexpected JSON issues, etc., still mapped to BreedNotFoundException
            throw new BreedNotFoundException("Unexpected error: " + e.getMessage());
        }
    }
}
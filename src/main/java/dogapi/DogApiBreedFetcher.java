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
 * All failures are reported as BreedNotFoundException per spec.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
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
            JSONObject json = new JSONObject(body);
            String status = json.optString("status", "");

            if (!"success".equalsIgnoreCase(status)) {
                String apiMessage = json.optString("message", "Unknown error.");
                throw new BreedNotFoundException(apiMessage);
            }

            JSONArray arr = json.getJSONArray("message");
            List<String> subs = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                subs.add(arr.getString(i));
            }
            return subs;

        } catch (IOException e) {
            throw new BreedNotFoundException("Failed to fetch breed info: " + e.getMessage());
        } catch (Exception e) {
            throw new BreedNotFoundException("Unexpected error: " + e.getMessage());
        }
    }
}
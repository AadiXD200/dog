package dogapi;

import java.util.List;

/**
 * Minimal local implementation to avoid hitting the real API during tests.
 */
public class BreedFetcherForLocalTesting implements BreedFetcher {
    private int callCount = 0;

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        callCount++;
        if ("hound".equalsIgnoreCase(breed)) {
            return List.of("afghan", "basset");
        }
        throw new BreedNotFoundException("Breed not found: " + breed);
    }

    public int getCallCount() {
        return callCount;
    }
}
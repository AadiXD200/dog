package dogapi;

import java.util.*;

/**
 * Caches successful results to reduce calls to the underlying fetcher.
 * Exceptions are NOT cached.
 */
public class CachingBreedFetcher implements BreedFetcher {

    private final BreedFetcher underlying;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.underlying = Objects.requireNonNull(fetcher);
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }

        callsMade++;
        List<String> result = underlying.getSubBreeds(breed);

        // store a defensive copy (and keep unmodifiable for safety)
        List<String> copy = Collections.unmodifiableList(new ArrayList<>(result));
        cache.put(breed, copy);
        return copy;
    }

    public int getCallsMade() {
        return callsMade;
    }
}
package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {

    private final BreedFetcher underlyingFetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    /**
     * Construct a caching fetcher that wraps another BreedFetcher.
     * @param fetcher the underlying fetcher used for actual API calls
     */
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.underlyingFetcher = fetcher;
    }

    /**
     * Returns the sub-breeds of the given breed, caching successful results.
     * If a call throws BreedNotFoundException, it is NOT cached.
     * @param breed the breed name to fetch
     * @return a list of sub-breed names
     * @throws BreedNotFoundException if the underlying fetcher throws it
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // If already cached, return cached value
        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }

        // Otherwise, fetch from the underlying fetcher
        callsMade++;
        List<String> result = underlyingFetcher.getSubBreeds(breed);

        // Cache successful results only
        cache.put(breed, result);
        return result;
    }

    /**
     * Returns the number of calls made to the underlying fetcher.
     * @return number of actual calls
     */
    public int getCallsMade() {
        return callsMade;
    }
}
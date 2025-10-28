package dogapi;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String breed = "hound";
        BreedFetcher fetcher = new CachingBreedFetcher(new BreedFetcherForLocalTesting());
        int result = getNumberOfSubBreeds(breed, fetcher);
        System.out.println(breed + " has " + result + " sub breeds");

        breed = "cat";
        result = getNumberOfSubBreeds(breed, fetcher);
        System.out.println(breed + " has " + result + " sub breeds");
    }

    /**
     * Return the number of sub breeds that the given dog breed has according to the provided fetcher.
     * Returns 0 if there are no sub breeds or if the breed does not exist.
     */
    public static int getNumberOfSubBreeds(String breed, BreedFetcher breedFetcher) {
        try {
            List<String> subs = breedFetcher.getSubBreeds(breed);
            return (subs == null) ? 0 : subs.size();
        } catch (BreedFetcher.BreedNotFoundException e) {
            return 0;
        }
    }
}
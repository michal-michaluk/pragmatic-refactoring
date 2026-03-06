package shortages;

public interface ShortagesRepository {
    Shortages get(String productRefNo);

    void save(Shortages shortages);

    void delete(String productRefNo);
}

package shortages;

public interface DemandsRepository {
    Demands get(String productRefNo, DataRange range);
}

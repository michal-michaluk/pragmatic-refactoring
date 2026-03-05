package shortages;

public interface ProductionOutputsRepository {
    ProductionOutputs getOutputs(String productRefNo, DataRange range);
}

package shortages;

public class ShortagePredictionService {
    private final DemandsRepository demands;
    private final ProductionOutputsRepository production;
    private final WarehouseStockRepository stock;

    public ShortagePredictionService(DemandsRepository demands, ProductionOutputsRepository production, WarehouseStockRepository stock) {
        this.demands = demands;
        this.production = production;
        this.stock = stock;
    }

    public ShortagesBuilder predict(String productRefNo, DataRange dates) {
        ProductionOutputs outputs = production.getOutputs(productRefNo, dates);
        Demands demandsPerDay = demands.get(productRefNo, dates);
        WarehouseStock level = stock.getStock(productRefNo);

        ShortagePrediction prediction = new ShortagePrediction(productRefNo, dates, outputs, demandsPerDay, level);

        return prediction.predict();
    }
}

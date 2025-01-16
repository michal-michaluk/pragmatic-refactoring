package shortages;

public class ShortagePredictionService {
    private final DemandsRepository demandsRepository;
    private final ProductionOutputsRepository productionRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    public ShortagePredictionService(DemandsRepository demandsRepository, ProductionOutputsRepository productionRepository, WarehouseStockRepository warehouseStockRepository) {
        this.demandsRepository = demandsRepository;
        this.productionRepository = productionRepository;
        this.warehouseStockRepository = warehouseStockRepository;
    }


    public ShortagesBuilder predict(String productRefNo, DataRange dates) {
        ProductionOutputs outputs = productionRepository.getOutputs(productRefNo, dates.start());
        Demands demandsPerDay = demandsRepository.get(productRefNo, dates.start());
        WarehouseStock stock = warehouseStockRepository.get(productRefNo);
        ShortagesPrediction prediction = new ShortagesPrediction(productRefNo, stock, dates, outputs, demandsPerDay);
        return prediction.predict();
    }
}

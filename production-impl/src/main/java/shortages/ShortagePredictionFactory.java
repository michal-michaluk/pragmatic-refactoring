package shortages;

import java.time.LocalDate;

public class ShortagePredictionFactory {

    private final DemandRepository demandRepository;
    private final ProductionRepository productionRepository;
    private final WarehouseRepository warehouseRepository;
    private final ConfigurationParameters config;

    public ShortagePredictionFactory(DemandRepository demandRepository, ProductionRepository productionRepository, WarehouseRepository warehouseRepository, ConfigurationParameters config) {
        this.demandRepository = demandRepository;
        this.productionRepository = productionRepository;
        this.warehouseRepository = warehouseRepository;
        this.config = config;
    }

    public ShortagePrediction get(String productRefNo, LocalDate start) {
        DateRange dates = config.predictionRange(start);
        ProductionOutputs outputs = productionRepository.get(productRefNo, start);
        Demands demandsPerDay = demandRepository.get(productRefNo, start);
        WarehouseStock stock = warehouseRepository.get(productRefNo);

        return new ShortagePrediction(productRefNo, dates, outputs, demandsPerDay, stock);
    }
}

package shortages;

import entities.ShortageEntity;

import java.util.List;

public class ShortagePredictionService {

    private final DemandRepository demandRepository;
    private final ProductionRepository productionRepository;
    private final WarehouseRepository warehouseRepository;

    public ShortagePredictionService(DemandRepository demandRepository, ProductionRepository productionRepository, WarehouseRepository warehouseRepository) {
        this.demandRepository = demandRepository;
        this.productionRepository = productionRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public List<ShortageEntity> predict(String productRefNo, DateRange dates) {
        ProductionOutputs outputs = productionRepository.get(productRefNo, dates.start());
        Demands demandsPerDay = demandRepository.get(productRefNo, dates.start());
        long level = warehouseRepository.get(productRefNo);

        ShortagePrediction prediction = new ShortagePrediction(productRefNo, dates, outputs, demandsPerDay, level);

        return prediction.predict();
    }
}

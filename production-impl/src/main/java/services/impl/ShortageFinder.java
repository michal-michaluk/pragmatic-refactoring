package services.impl;

import entities.ShortageEntity;
import external.CurrentStock;
import shortages.*;

import java.time.LocalDate;
import java.util.List;

public class ShortageFinder {

    private final DemandsRepository demandsRepository;
    private final ProductionOutputsRepository productionRepository;

    public ShortageFinder(DemandsRepository demandsRepository, ProductionOutputsRepository productionRepository) {
        this.demandsRepository = demandsRepository;
        this.productionRepository = productionRepository;
    }

    public List<ShortageEntity> findShortages(String productRefNo, LocalDate today, int daysAhead, CurrentStock externalStock) {
        WarehouseStockRepository warehouseStockRepository = refNo -> new WarehouseStock(externalStock.getLevel(), externalStock.getLocked());
        ShortagePredictionService service = new ShortagePredictionService(demandsRepository, productionRepository, warehouseStockRepository);
        ShortagesBuilder shortages = service.predict(productRefNo, DataRange.of(today, daysAhead));
        return shortages.build();
    }

}

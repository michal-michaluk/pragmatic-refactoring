package acl;

import entities.ShortageEntity;
import external.CurrentStock;
import shortages.*;

import java.time.LocalDate;
import java.util.List;

public class ShortageFinder {

    public static final boolean COMPUTE_WITH_NEW_MODEL = false;

    private final DemandsRepository demandsRepository;
    private final ProductionOutputsRepository productionRepository;
    private final services.impl.ShortageFinder legacy;

    public ShortageFinder(DemandsRepository demandsRepository, ProductionOutputsRepository productionRepository, services.impl.ShortageFinder legacy) {
        this.demandsRepository = demandsRepository;
        this.productionRepository = productionRepository;
        this.legacy = legacy;
    }

    public List<ShortageEntity> findShortages(String productRefNo, LocalDate today, int daysAhead, CurrentStock externalStock) {
        if (COMPUTE_WITH_NEW_MODEL) {
            WarehouseStockRepository warehouseStockRepository = refNo -> new WarehouseStock(externalStock.getLevel(), externalStock.getLocked());
            ShortagePredictionService service = new ShortagePredictionService(demandsRepository, productionRepository, warehouseStockRepository);
            ShortagesBuilder shortages = service.predict(productRefNo, DataRange.of(today, daysAhead));
            return shortages.build();
        } else {
            return legacy.findShortages(productRefNo, today, daysAhead, externalStock);
        }
    }

}

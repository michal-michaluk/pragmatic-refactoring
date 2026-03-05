package acl;

import entities.ShortageEntity;
import external.CurrentStock;
import shortages.*;

import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionACL {

    private final DemandsRepository demandsRepository;
    private final ProductionOutputsRepository productionRepository;

    public ShortagePredictionACL(DemandsRepository demandsRepository, ProductionOutputsRepository productionRepository) {
        this.demandsRepository = demandsRepository;
        this.productionRepository = productionRepository;
    }

    public List<ShortageEntity> findShortages(String productRefNo, LocalDate today, int daysAhead, CurrentStock stock) {
        DataRange dates = DataRange.of(today, daysAhead);
        ShortagePredictionService service = new ShortagePredictionService(
                demandsRepository,
                productionRepository,
                new WarehouseStockRepositoryInMemory(stock)
        );

        ShortagesBuilder shortages = service.predict(productRefNo, dates);
        return shortages.build();
    }

}

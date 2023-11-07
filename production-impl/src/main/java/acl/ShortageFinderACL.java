package acl;

import entities.ShortageEntity;
import external.CurrentStock;
import shortages.DateRange;
import shortages.DemandRepository;
import shortages.ProductionRepository;
import shortages.ShortagePredictionService;

import java.time.LocalDate;
import java.util.List;

public class ShortageFinderACL {

    private final DemandRepository demandRepository;
    private final ProductionRepository productionRepository;

    public ShortageFinderACL(DemandRepository demandRepository, ProductionRepository productionRepository) {
        this.demandRepository = demandRepository;
        this.productionRepository = productionRepository;
    }

    public List<ShortageEntity> findShortages(String productRefNo, LocalDate today, int daysAhead, CurrentStock stock) {
        var service = new ShortagePredictionService(
                demandRepository,
                productionRepository,
                refNo -> stock.getLevel()
        );
        return service.predict(productRefNo, DateRange.of(today, daysAhead));
    }
}

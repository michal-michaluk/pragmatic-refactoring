package services.impl;

import entities.ShortageEntity;
import external.CurrentStock;
import shortages.*;

import java.time.LocalDate;
import java.util.List;

public class ShortageFinder {

    private final DemandRepository demandRepository;
    private final ProductionRepository productionRepository;

    public ShortageFinder(DemandRepository demandRepository, ProductionRepository productionRepository) {
        this.demandRepository = demandRepository;
        this.productionRepository = productionRepository;
    }

    /**
     * Production at day of expected delivery is quite complex:
     * We are able to produce and deliver just in time at same day
     * but depending on delivery time or scheme of multiple deliveries,
     * we need to plan properly to have right amount of parts ready before delivery time.
     * <p/>
     * Typical schemas are:
     * <li>Delivery at prod day start</li>
     * <li>Delivery till prod day end</li>
     * <li>Delivery during specified shift</li>
     * <li>Multiple deliveries at specified times</li>
     * Schema changes the way how we calculate shortages.
     * Pick of schema depends on customer demand on daily basis and for each product differently.
     * Some customers includes that information in callof document,
     * other stick to single schema per product. By manual adjustments of demand,
     * customer always specifies desired delivery schema
     * (increase amount in scheduled transport or organize extra transport at given time)
     */
    public List<ShortageEntity> findShortages(String productRefNo, LocalDate today, int daysAhead, CurrentStock stock) {
        DateRange dates = DateRange.of(today, daysAhead);
        ProductionOutputs outputs = productionRepository.get(productRefNo, today);
        Demands demandsPerDay = demandRepository.get(productRefNo, today);

        long level = stock.getLevel();

        List<ShortageEntity> gap = new ShortagePrediction(productRefNo, dates, outputs, demandsPerDay, level).predict();
        return gap;
    }

}

package shortages;

import entities.ShortageEntity;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class ShortagePrediction {
    private final String productRefNo;
    private final DateRange dates;
    private final ProductionOutputs outputs;
    private final Demands demands;
    private final long warehouseStock;

    public ShortagePrediction(String productRefNo, DateRange dates, ProductionOutputs outputs, Demands demands, long warehouseStock) {
        this.productRefNo = productRefNo;
        this.dates = dates;
        this.outputs = outputs;
        this.demands = demands;
        this.warehouseStock = warehouseStock;
    }

    public List<ShortageEntity> predict() {
        long level = this.warehouseStock;
        List<ShortageEntity> gap = new LinkedList<>();
        for (LocalDate day : dates) {
            Demands.DailyDemand demand = demands.get(day);
            long produced = outputs.getProduced(day);

            long levelOnDelivery = demand.calculateLevelOnDelivery(level, produced);

            if (levelOnDelivery < 0) {
                ShortageEntity entity = new ShortageEntity();
                entity.setRefNo(productRefNo);
                entity.setFound(LocalDate.now());
                entity.setAtDay(day);
                entity.setMissing(-levelOnDelivery);
                gap.add(entity);
            }
            long endOfDayLevel = demand.calculateEndOfDayLevel(level, produced);
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return gap;
    }
}

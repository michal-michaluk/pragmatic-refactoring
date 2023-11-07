package shortages;

import java.time.LocalDate;

class ShortagePrediction {
    private final String productRefNo;
    private final DateRange dates;
    private final ProductionOutputs outputs;
    private final Demands demands;
    private final long warehouseStock;

    ShortagePrediction(String productRefNo, DateRange dates, ProductionOutputs outputs, Demands demands, long warehouseStock) {
        this.productRefNo = productRefNo;
        this.dates = dates;
        this.outputs = outputs;
        this.demands = demands;
        this.warehouseStock = warehouseStock;
    }

    Shortages predict() {
        long level = this.warehouseStock;
        Shortages.Builder gap = Shortages.builder(productRefNo);
        for (LocalDate day : dates) {
            Demands.DailyDemand demand = demands.get(day);
            long produced = outputs.getProduced(day);

            long levelOnDelivery = demand.calculateLevelOnDelivery(level, produced);

            if (levelOnDelivery < 0) {
                gap.add(day, levelOnDelivery);
            }
            long endOfDayLevel = demand.calculateEndOfDayLevel(level, produced);
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return gap.build();
    }

}

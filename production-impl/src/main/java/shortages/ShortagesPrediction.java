package shortages;

import java.time.LocalDate;

public class ShortagesPrediction {
    private final String productRefNo;
    private final WarehouseStock stock;
    private final DataRange dates;
    private final ProductionOutputs outputs;
    private final Demands demandsPerDay;

    public ShortagesPrediction(String productRefNo, WarehouseStock stock, DataRange dates, ProductionOutputs outputs, Demands demandsPerDay) {
        this.productRefNo = productRefNo;
        this.stock = stock;
        this.dates = dates;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
    }

    public ShortagesBuilder predict() {
        long level = stock.level();

        ShortagesBuilder shortages = new ShortagesBuilder(productRefNo);
        for (LocalDate day : dates) {
            Demands.DailyDemand demand = demandsPerDay.get(day);
            if (demand == null) {
                level += outputs.getProduced(day);
                continue;
            }
            long produced = outputs.getProduced(day);

            long levelOnDelivery = demand.calculateLevelOnDelivery(level, produced);

            if (levelOnDelivery < 0) {
                shortages.add(day, levelOnDelivery);
            }
            long endOfDayLevel = demand.calculateEndOfDayLevel(level, produced);
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return shortages;
    }
}

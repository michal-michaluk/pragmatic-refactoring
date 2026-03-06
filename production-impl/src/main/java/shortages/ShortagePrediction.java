package shortages;

import java.time.LocalDate;

class ShortagePrediction {
    private final String productRefNo;
    private final DataRange dates;
    private final ProductionOutputs outputs;
    private final Demands demandsPerDay;
    private final WarehouseStock warehouseStock;

    ShortagePrediction(String productRefNo, DataRange dates, ProductionOutputs outputs, Demands demandsPerDay, WarehouseStock warehouseStock) {
        this.productRefNo = productRefNo;
        this.dates = dates;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
        this.warehouseStock = warehouseStock;
    }

    Shortages predict() {
        Shortages shortages = new Shortages(productRefNo, warehouseStock);
        long level = warehouseStock.level();
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

    public long lockedPartsOnStock() {
        return warehouseStock.locked();
    }
}

package shortages;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Shortages {
    private final WarehouseStock warehouseStock;
    private final String productRefNo;
    private final SortedMap<LocalDate, Long> missing;

    public Shortages(String productRefNo, SortedMap<LocalDate, Long> missing, WarehouseStock warehouseStock) {
        this.productRefNo = productRefNo;
        this.missing = missing;
        this.warehouseStock = warehouseStock;
    }

    public static Builder builder(String productRefNo, WarehouseStock warehouseStock) {
        return new Builder(productRefNo, warehouseStock);
    }

    public <T> List<T> map(BiFunction<String, Map.Entry<LocalDate, Long>, T> mappper) {
        return missing.entrySet().stream()
                .map(entry -> mappper.apply(productRefNo, entry))
                .collect(Collectors.toList());
    }

    public boolean differentThan(Shortages previous) {
        return !missing.isEmpty() && !this.equals(previous);
    }

    public boolean solved(Shortages previous) {
        return missing.isEmpty() && !previous.missing.isEmpty();
    }

    public boolean lockedPartsBeforeDate(LocalDate date) {
        return warehouseStock.locked() > 0 && missing.firstKey().isBefore(date);
    }

    public String productRedNo() {
        return productRefNo;
    }

    public static class Builder {
        private final String productRefNo;
        private final WarehouseStock warehouseStock;
        private final SortedMap<LocalDate, Long> missing = new TreeMap<>();

        public Builder(String productRefNo, WarehouseStock warehouseStock) {
            this.productRefNo = productRefNo;
            this.warehouseStock = warehouseStock;
        }

        public Shortages build() {
            return new Shortages(productRefNo, missing, warehouseStock);
        }

        public void add(LocalDate day, long levelOnDelivery) {
            missing.put(day, Math.abs(levelOnDelivery));
        }
    }
}

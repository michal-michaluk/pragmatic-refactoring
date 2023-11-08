package shortages;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Shortages {
    private final String productRefNo;
    private final Map<LocalDate, Long> missing;

    public Shortages(String productRefNo, Map<LocalDate, Long> missing) {
        this.productRefNo = productRefNo;
        this.missing = missing;
    }

    public static Builder builder(String productRefNo) {
        return new Builder(productRefNo);
    }

    public <T> List<T> map(BiFunction<String, Map.Entry<LocalDate, Long>, T> mappper) {
        return missing.entrySet().stream()
                .map(entry -> mappper.apply(productRefNo, entry))
                .collect(Collectors.toList());
    }

    public static class Builder {
        private final String productRefNo;
        private final Map<LocalDate, Long> missing = new TreeMap<>();

        public Builder(String productRefNo) {
            this.productRefNo = productRefNo;
        }

        public Shortages build() {
            return new Shortages(productRefNo, missing);
        }

        public void add(LocalDate day, long levelOnDelivery) {
            missing.put(day, Math.abs(levelOnDelivery));
        }
    }
}

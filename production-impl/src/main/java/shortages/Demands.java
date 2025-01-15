package shortages;

import enums.DeliverySchema;

import java.time.LocalDate;
import java.util.Map;

public class Demands {
    private final Map<LocalDate, DailyDemand> demandsPerDay;

    public Demands(Map<LocalDate, DailyDemand> demands) {
        this.demandsPerDay = demands;
    }

    public DailyDemand get(LocalDate day) {
        if (demandsPerDay.containsKey(day)) {
            return demandsPerDay.get(day);
        }
        return null;
    }

    public static class DailyDemand {
        private final long level;
        private final DeliverySchema schema;

        public DailyDemand(long level, DeliverySchema schema) {
            this.level = level;
            this.schema = schema;
        }

        public long calculateLevelOnDelivery(long level, long produced) {
            long levelOnDelivery;
            if (schema == DeliverySchema.atDayStart) {
                levelOnDelivery = level - this.level;
            } else if (schema == DeliverySchema.tillEndOfDay) {
                levelOnDelivery = level - this.level + produced;
            } else {
                if (schema == DeliverySchema.every3hours) {
                    // TODO WTF ?? we need to rewrite that app :/
                    throw new UnsupportedOperationException();
                } else {
                    // TODO implement other variants
                    throw new UnsupportedOperationException();
                }
            }
            return levelOnDelivery;
        }

        public long calculateEndOfDayLevel(long level, long produced) {
            return level + produced - this.level;
        }
    }
}

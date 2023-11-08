package shortages;

import demands.DeliverySchema;

import java.time.LocalDate;
import java.util.Map;

public class Demands {
    private static final DailyDemand NO_DEMAND = new DailyDemand(0, DeliverySchema.atDayStart);

    private final Map<LocalDate, DailyDemand> demands;

    public Demands(Map<LocalDate, DailyDemand> demands) {
        this.demands = demands;
    }

    public DailyDemand get(LocalDate day) {
        return demands.getOrDefault(day, NO_DEMAND);
    }

    public static class DailyDemand {
        private final long demand;
        private final DeliverySchema schema;

        public DailyDemand(long demand, DeliverySchema schema) {
            this.demand = demand;
            this.schema = schema;

        }

        public long calculateLevelOnDelivery(long level, long produced) {
            long levelOnDelivery;
            if (schema == DeliverySchema.atDayStart) {
                levelOnDelivery = level - demand;
            } else if (schema == DeliverySchema.tillEndOfDay) {
                levelOnDelivery = level - demand + produced;
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
            return level + produced - this.demand;
        }
    }
}

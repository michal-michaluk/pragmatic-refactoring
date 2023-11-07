package shortages;

import entities.DemandEntity;
import enums.DeliverySchema;
import tools.Util;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demands {
    private final Map<LocalDate, DemandEntity> demandsPerDay;

    public Demands(List<DemandEntity> demands) {
        Map<LocalDate, DemandEntity> demandsPerDay = new HashMap<>();
        for (DemandEntity demand : demands) {
            demandsPerDay.put(demand.getDay(), demand);
        }
        this.demandsPerDay = Collections.unmodifiableMap(demandsPerDay);
    }

    public DailyDemand get(LocalDate day) {
        if (demandsPerDay.containsKey(day)) {
            return new DailyDemand(demandsPerDay.get(day));
        }
        return null;
    }

    public static class DailyDemand {
        private final DemandEntity demand;

        public DailyDemand(DemandEntity demand) {
            this.demand = demand;
        }

        public long calculateLevelOnDelivery(long level, long produced) {
            long levelOnDelivery;
            if (getDeliverySchema() == DeliverySchema.atDayStart) {
                levelOnDelivery = level - getLevel();
            } else if (getDeliverySchema() == DeliverySchema.tillEndOfDay) {
                levelOnDelivery = level - getLevel() + produced;
            } else if (getDeliverySchema() == DeliverySchema.every3hours) {
                // TODO WTF ?? we need to rewrite that app :/
                throw new UnsupportedOperationException();
            } else {
                // TODO implement other variants
                throw new UnsupportedOperationException();
            }
            return levelOnDelivery;
        }

        public long getLevel() {
            return Util.getLevel(demand);
        }

        private DeliverySchema getDeliverySchema() {
            return Util.getDeliverySchema(demand);
        }

        public long calculateEndOfDayLevel(long level, long produced) {
            return level + produced - getLevel();
        }
    }
}

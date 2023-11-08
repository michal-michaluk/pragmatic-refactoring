package acl;

import demands.DemandForecasting;
import demands.DemandForecasting.Demand;
import shortages.DemandRepository;
import shortages.Demands;
import shortages.Demands.DailyDemand;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DemandACLRepository implements DemandRepository {
    private final DemandForecasting demands;

    public DemandACLRepository(DemandForecasting demands) {
        this.demands = demands;
    }

    @Override
    public Demands get(String productRefNo, LocalDate today) {
        List<Demand> list = demands.getDemands(productRefNo, today);
        Map<LocalDate, DailyDemand> mapped = list.stream().collect(Collectors.toUnmodifiableMap(
                Demand::date,
                demand -> new DailyDemand(demand.demand(), demand.schema())
        ));
        return new Demands(mapped);
    }
}

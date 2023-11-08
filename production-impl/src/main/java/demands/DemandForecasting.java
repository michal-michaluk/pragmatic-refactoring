package demands;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DemandForecasting {
    private final DemandDao demandDao;

    public DemandForecasting(DemandDao demandDao) {
        this.demandDao = demandDao;
    }

    public List<Demand> getDemands(String productRefNo, LocalDate start) {
        List<DemandEntity> demands = demandDao.findFrom(start.atStartOfDay(), productRefNo);
        return demands.stream()
                .map(demand -> new Demand(
                        productRefNo,
                        demand.getDay(),
                        Util.getLevel(demand),
                        Util.getDeliverySchema(demand)))
                .collect(Collectors.toList());
    }

    public record Demand(
            String productRefNo,
            LocalDate date,
            long demand,
            DeliverySchema schema
    ) {}
}

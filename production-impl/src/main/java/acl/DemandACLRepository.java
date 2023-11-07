package acl;

import dao.DemandDao;
import entities.DemandEntity;
import shortages.DemandRepository;
import shortages.Demands;
import tools.Util;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DemandACLRepository implements DemandRepository {
    private final DemandDao demandDao;

    public DemandACLRepository(DemandDao demandDao) {
        this.demandDao = demandDao;
    }

    @Override
    public Demands get(String productRefNo, LocalDate today) {
        List<DemandEntity> demands = demandDao.findFrom(today.atStartOfDay(), productRefNo);
        Map<LocalDate, Demands.DailyDemand> mapped = demands.stream()
                .collect(Collectors.toUnmodifiableMap(DemandEntity::getDay, demand -> new Demands.DailyDemand(
                                Util.getLevel(demand),
                                Util.getDeliverySchema(demand)
                        ), (a, b) -> b)
                );
        return new Demands(mapped);
    }
}

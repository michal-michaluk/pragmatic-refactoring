package acl;

import dao.DemandDao;
import entities.DemandEntity;
import shortages.DataRange;
import shortages.Demands;
import shortages.DemandsRepository;
import tools.Util;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DemandsRepositoryACL implements DemandsRepository {
    private final DemandDao demandDao;

    public DemandsRepositoryACL(DemandDao demandDao) {
        this.demandDao = demandDao;
    }

    @Override public Demands get(String productRefNo, DataRange range) {
        List<DemandEntity> demands = demandDao.findFrom(range.start().atStartOfDay(), productRefNo);
        Demands demandsPerDay = new Demands(demands.stream().collect(Collectors.toUnmodifiableMap(
                DemandEntity::getDay,
                demand -> new Demands.DailyDemand(
                        Util.getLevel(demand),
                        Util.getDeliverySchema(demand)
                )
        )));
        return demandsPerDay;
    }
}

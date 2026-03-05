package acl;

import dao.ProductionDao;
import entities.ProductionEntity;
import shortages.DataRange;
import shortages.ProductionOutputs;
import shortages.ProductionOutputsRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductionOutputsRepositoryACL implements ProductionOutputsRepository {
    private final ProductionDao productionDao;

    public ProductionOutputsRepositoryACL(ProductionDao productionDao) {
        this.productionDao = productionDao;
    }

    @Override public ProductionOutputs getOutputs(String productRefNo, DataRange range) {
        List<ProductionEntity> productions = productionDao.findFromTime(productRefNo, range.start().atStartOfDay());
        return new ProductionOutputs(Collections.unmodifiableMap(productions.stream()
                .collect(Collectors.groupingBy(
                        production -> production.getStart().toLocalDate(),
                        Collectors.summingLong(ProductionEntity::getOutput)
                ))));
    }
}

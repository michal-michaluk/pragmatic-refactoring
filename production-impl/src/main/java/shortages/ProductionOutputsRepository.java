package shortages;

import dao.ProductionDao;
import entities.ProductionEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductionOutputsRepository {
    private final ProductionDao productionDao;

    public ProductionOutputsRepository(ProductionDao productionDao) {
        this.productionDao = productionDao;
    }

    public ProductionOutputs getOutputs(String productRefNo, LocalDate today) {
        List<ProductionEntity> productions = productionDao.findFromTime(productRefNo, today.atStartOfDay());
        return new ProductionOutputs(Collections.unmodifiableMap(productions.stream()
                .collect(Collectors.groupingBy(
                        production -> production.getStart().toLocalDate(),
                        Collectors.summingLong(ProductionEntity::getOutput)
                ))));
    }
}

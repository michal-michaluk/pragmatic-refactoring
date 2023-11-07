package shortages;

import dao.ProductionDao;
import entities.ProductionEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductionRepository {
    private final ProductionDao productionDao;

    public ProductionRepository(ProductionDao productionDao) {
        this.productionDao = productionDao;
    }

    public ProductionOutputs get(String productRefNo, LocalDate today) {
        List<ProductionEntity> productions = productionDao.findFromTime(productRefNo, today.atStartOfDay());
        Map<LocalDate, Long> sum = productions.stream()
                .collect(Collectors.groupingBy(
                        production -> production.getStart().toLocalDate(),
                        Collectors.summingLong(ProductionEntity::getOutput)
                ));
        return new ProductionOutputs(Collections.unmodifiableMap(sum));
    }
}

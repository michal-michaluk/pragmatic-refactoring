package production;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ProductionPlanningService {
    private final ProductionDao productionDao;

    public ProductionPlanningService(ProductionDao productionDao) {
        this.productionDao = productionDao;
    }

    // GET /outputs?productRefNo={productRefNo}&start={date}
    public List<OutputSummary> getDailyOutputsSummary(String productRefNo, LocalDate start) {
        return productionDao.findFromTime(productRefNo, start.atStartOfDay()).stream()
                .collect(Collectors.groupingBy(
                        production -> production.getStart().toLocalDate(),
                        Collectors.summingLong(ProductionEntity::getOutput)
                )).entrySet().stream()
                .map(entry -> new OutputSummary(
                        productRefNo,
                        entry.getKey(),
                        entry.getValue()
                )).collect(Collectors.toList());
    }

    public record OutputSummary(String productRefNo, LocalDate date, Long output) {
    }
}

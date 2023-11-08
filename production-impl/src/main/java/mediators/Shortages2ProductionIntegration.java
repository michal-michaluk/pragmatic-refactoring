package mediators;

import production.ProductionPlanningService;
import production.ProductionPlanningService.OutputSummary;
import shortages.ProductionOutputs;
import shortages.ProductionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Shortages2ProductionIntegration implements ProductionRepository {
    private final ProductionPlanningService production;

    public Shortages2ProductionIntegration(ProductionPlanningService production) {
        this.production = production;
    }

    @Override
    public ProductionOutputs get(String productRefNo, LocalDate today) {
        List<OutputSummary> outputs = production.getDailyOutputsSummary(productRefNo, today);
        return new ProductionOutputs(
                outputs.stream()
                        .collect(Collectors.toUnmodifiableMap(
                                OutputSummary::date,
                                OutputSummary::output
                        ))
        );
    }
}

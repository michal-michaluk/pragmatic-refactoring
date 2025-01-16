package shortages;

import java.time.LocalDate;

public interface ProductionOutputsRepository {
    ProductionOutputs getOutputs(String productRefNo, LocalDate today);
}

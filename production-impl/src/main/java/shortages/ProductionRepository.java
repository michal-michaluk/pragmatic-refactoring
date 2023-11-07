package shortages;

import java.time.LocalDate;

public interface ProductionRepository {
    ProductionOutputs get(String productRefNo, LocalDate today);
}

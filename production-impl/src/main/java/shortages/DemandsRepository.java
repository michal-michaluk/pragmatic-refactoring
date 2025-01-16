package shortages;

import java.time.LocalDate;

public interface DemandsRepository {
    Demands get(String productRefNo, LocalDate today);
}

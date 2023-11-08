package shortages;

import java.time.LocalDate;

public record ConfigurationParameters(
        int predictionDaysAhead,
        long increaseQATaskPriorityInDays
) {
    public DateRange predictionRange(LocalDate start) {
        return DateRange.of(start, predictionDaysAhead());
    }
}

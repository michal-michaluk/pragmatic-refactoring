package shortages;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class DataRange implements Iterable<LocalDate> {
    private final List<LocalDate> dates;

    private DataRange(List<LocalDate> dates) {
        assert !dates.isEmpty();
        this.dates = dates;
    }

    public static DataRange of(LocalDate today, int daysAhead) {
        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .toList();
        return new DataRange(dates);
    }

    @Override
    public Iterator<LocalDate> iterator() {
        return dates.iterator();
    }

    public LocalDate start() {
        return dates.getFirst();
    }
}

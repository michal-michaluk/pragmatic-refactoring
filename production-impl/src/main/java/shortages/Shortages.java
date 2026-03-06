package shortages;

import entities.ShortageEntity;

import java.time.LocalDate;
import java.util.List;

public class Shortages {
    private final String productRefNo;
    private final WarehouseStock stock;
    private final List<ShortageEntity> shortages;

    public Shortages(String productRefNo, List<ShortageEntity> shortages, WarehouseStock stock) {
        this.productRefNo = productRefNo;
        this.shortages = shortages;
        this.stock = stock;
    }

    public void add(LocalDate day, long missing) {
        ShortageEntity entity = new ShortageEntity();
        entity.setRefNo(productRefNo);
        entity.setFound(LocalDate.now());
        entity.setAtDay(day);
        entity.setMissing(Math.abs(missing));
        shortages.add(entity);
    }

    public List<ShortageEntity> toEntities() {
        return shortages;
    }

    public boolean checkIncreasePriorityForLockedParts(LocalDate date) {
        return shortages.get(0).getAtDay().isBefore(date);

    }

    public boolean isDifferentShortage(Shortages previous) {
        return !shortages.isEmpty() && !shortages.equals(previous.shortages);
    }

    public boolean solved(Shortages previous) {
        return shortages.isEmpty() && !previous.shortages.isEmpty();
    }
}

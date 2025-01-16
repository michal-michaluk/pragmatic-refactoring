package shortages;

import entities.ShortageEntity;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class ShortagesBuilder {
    private final String productRefNo;
    private final List<ShortageEntity> shortages = new LinkedList<>();

    public ShortagesBuilder(String productRefNo) {
        this.productRefNo = productRefNo;
    }

    public void add(LocalDate day, long missing) {
        ShortageEntity entity = new ShortageEntity();
        entity.setRefNo(productRefNo);
        entity.setFound(LocalDate.now());
        entity.setAtDay(day);
        entity.setMissing(Math.abs(missing));
        shortages.add(entity);
    }

    public List<ShortageEntity> build() {
        return shortages;
    }
}

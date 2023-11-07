package shortages;

import entities.ShortageEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Shortages {
    private final List<ShortageEntity> entities;

    public Shortages(List<ShortageEntity> entities) {
        this.entities = entities;
    }

    public static Builder builder(String productRefNo) {
        return new Builder(productRefNo);
    }

    public List<ShortageEntity> getEntities() {
        return entities;
    }

    public static class Builder {
        private final String productRefNo;
        private final List<ShortageEntity> entities = new ArrayList<>();

        public Builder(String productRefNo) {
            this.productRefNo = productRefNo;
        }

        public Shortages build() {
            return new Shortages(entities);
        }

        public void add(LocalDate day, long levelOnDelivery) {
            ShortageEntity entity = new ShortageEntity();
            entity.setRefNo(productRefNo);
            entity.setFound(LocalDate.now());
            entity.setAtDay(day);
            entity.setMissing(Math.abs(levelOnDelivery));
            entities.add(entity);
        }
    }
}

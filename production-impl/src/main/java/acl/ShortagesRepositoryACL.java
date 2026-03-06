package acl;

import dao.ShortageDao;
import entities.ShortageEntity;
import shortages.Shortages;
import shortages.ShortagesRepository;

import java.util.List;

public class ShortagesRepositoryACL implements ShortagesRepository {

    private final ShortageDao dao;

    public ShortagesRepositoryACL(ShortageDao dao) {
        this.dao = dao;
    }

    @Override
    public Shortages get(String productRefNo) {
        List<ShortageEntity> shortages = dao.getForProduct(productRefNo);
        return new Shortages(productRefNo, shortages, null);
    }

    @Override
    public void save(Shortages shortages) {
        dao.save(shortages.toEntities());
    }

    @Override
    public void delete(String productRefNo) {
        dao.delete(productRefNo);
    }
}

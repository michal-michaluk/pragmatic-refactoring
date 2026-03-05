package acl;

import external.CurrentStock;
import shortages.WarehouseStock;
import shortages.WarehouseStockRepository;

public class WarehouseStockRepositoryInMemory implements WarehouseStockRepository {
    private final CurrentStock stock;

    public WarehouseStockRepositoryInMemory(CurrentStock stock) {
        this.stock = stock;
    }

    @Override
    public WarehouseStock getStock(String productRefNo1) {
        return new WarehouseStock(stock.getLevel(), stock.getLocked());
    }
}

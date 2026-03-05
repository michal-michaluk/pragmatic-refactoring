package shortages;

@FunctionalInterface
public interface WarehouseStockRepository {

    WarehouseStock getStock(String productRefNo);
}

package mediators;

import external.CurrentStock;
import external.StockService;
import shortages.WarehouseRepository;
import shortages.WarehouseStock;

public class Shortages2WarehouseIntegration implements WarehouseRepository {
    private final StockService stockService;

    public Shortages2WarehouseIntegration(StockService stockService) {
        this.stockService = stockService;
    }

    @Override
    public WarehouseStock get(String productRefNo) {
        CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
        return new WarehouseStock(currentStock.getLevel(), currentStock.getLocked());
    }
}

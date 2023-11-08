package shortages;

public interface WarehouseRepository {
    WarehouseStock get(String productRefNo);
}

package shortages;

import api.AdjustDemandDto;
import production.ProductionEntity;
import shortages.NotificationService.NotificationTrigger;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {

    private final ShortageRepository repository;
    private final ShortagePredictionFactory factory;
    private final NotificationService notifications;

    private final Clock clock;

    public ShortagePredictionService(ShortageRepository repository, ShortagePredictionFactory factory, NotificationService notifications, Clock clock) {
        this.repository = repository;
        this.factory = factory;
        this.notifications = notifications;
        this.clock = clock;
    }

    public void processShortagesLogistic(AdjustDemandDto adjustment) {
        processForProduct(adjustment.getProductRefNo(), LocalDate.now(clock), NotificationTrigger.DemandsChanged);
    }

    public void processShortagesQuality(String productRefNo) {
        processForProduct(productRefNo, LocalDate.now(clock), NotificationTrigger.QualityEvent);
    }

    public void processShortagesWarehouse(String productRefNo) {
        processForProduct(productRefNo, LocalDate.now(clock), NotificationTrigger.WarehouseStockChanged);
    }

    public void processShortagesProduction(List<ProductionEntity> products) {
        LocalDate today = LocalDate.now(clock);

        for (ProductionEntity production : products) {
            processForProduct(production.getForm().getRefNo(), today, NotificationTrigger.ProductionPlanChanged);
        }
    }

    private void processForProduct(String productRefNo, LocalDate today, NotificationTrigger notify) {
        ShortagePrediction prediction = factory.get(productRefNo, today);
        Shortages shortages = prediction.predict();
        Shortages previous = repository.get(productRefNo);

        if (shortages.differentThan(previous)) {
            notifications.notifyAboutShortages(notify, shortages);
            repository.save(shortages);
        } else if (shortages.solved(previous)) {
            repository.delete(productRefNo);
        }
    }
}

package shortages;

import external.JiraService;
import external.NotificationsService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Set;

public class ShortagePredictionService {
    private final DemandsRepository demands;
    private final ProductionOutputsRepository production;
    private final WarehouseStockRepository stock;
    private final ShortagesRepository shortagesRepository;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;

    public ShortagePredictionService(DemandsRepository demands, ProductionOutputsRepository production, WarehouseStockRepository stock, ShortagesRepository shortagesRepository) {
        this.demands = demands;
        this.production = production;
        this.stock = stock;
        this.shortagesRepository = shortagesRepository;
    }

    interface NotificationStrategy {
        void notifyAboutNewShortages(NotificationsService service, Shortages shortages);

        NotificationStrategy alert = (notificationsService, shortages) -> notificationsService.alertPlanner(shortages.toEntities());
        NotificationStrategy softNotifyPlanner = (notificationsService, shortages) -> notificationsService.softNotifyPlanner(shortages.toEntities());
        NotificationStrategy markOnPlan = (notificationsService, shortages) -> notificationsService.markOnPlan(shortages.toEntities());
    }

    public void handleWarehouseStockChanged(String productRefNo) {
        processShortages(productRefNo, NotificationStrategy.alert);
    }

    public void handleDemandChanged(String productRefNo) {
        processShortages(productRefNo, NotificationStrategy.alert);
    }

    public void handleQualityLocks(String productRefNo) {
        processShortages(productRefNo, NotificationStrategy.softNotifyPlanner);
    }

    public void handlePlanChanged(Set<String> productRefNos) {
        for (String refNo : productRefNos) {
            processShortages(refNo, NotificationStrategy.markOnPlan);
        }
    }

    private ShortagePrediction createShortagePrediction(String productRefNo, DataRange dates) {
        ProductionOutputs outputs = production.getOutputs(productRefNo, dates);
        Demands demandsPerDay = demands.get(productRefNo, dates);
        WarehouseStock level = stock.getStock(productRefNo);

        return new ShortagePrediction(productRefNo, dates, outputs, demandsPerDay, level);
    }

    private void processShortages(String productRefNo, NotificationStrategy notification) {
        LocalDate today = LocalDate.now(clock);
        ShortagePrediction prediction = createShortagePrediction(
                productRefNo,
                DataRange.of(today, confShortagePredictionDaysAhead)
        );
        Shortages shortages = prediction.predict();

        Shortages previous = shortagesRepository.get(productRefNo);
        if (shortages.isDifferentShortage(previous)) {
            shortagesRepository.save(shortages);
            notification.notifyAboutNewShortages(notificationService, shortages);
            if (shouldIncreasePriorityForLockedPartsVerification(prediction, shortages, today)) {
                jiraService.increasePriorityFor(productRefNo);
            }
        }
        if (shortages.solved(previous)) {
            shortagesRepository.delete(productRefNo);
        }
    }

    private boolean shouldIncreasePriorityForLockedPartsVerification(ShortagePrediction prediction, Shortages shortages, LocalDate today) {
        return prediction.lockedPartsOnStock() > 0 &&
               shortages.checkIncreasePriorityForLockedParts(today.plusDays(confIncreaseQATaskPriorityInDays));
    }
}

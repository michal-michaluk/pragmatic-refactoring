package shortages;

import api.AdjustDemandDto;
import dao.ShortageDao;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import external.JiraService;
import external.NotificationsService;
import external.StockService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {
    private final DemandsRepository demands;
    private final ProductionOutputsRepository production;
    private final WarehouseStockRepository stock;

    private ShortageDao shortageDao;
    private StockService stockService;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;


    public ShortagePredictionService(DemandsRepository demands, ProductionOutputsRepository production, WarehouseStockRepository stock) {
        this.demands = demands;
        this.production = production;
        this.stock = stock;
    }

    public void processShortagesLogistic(AdjustDemandDto adjustment) {
        String productRefNo = adjustment.getProductRefNo();
        LocalDate today = LocalDate.now(clock);
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = predict(
                productRefNo,
                DataRange.of(today, confShortagePredictionDaysAhead)
        ).build();
        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        // TODO REFACTOR: lookup for shortages -> ShortageFound / ShortagesGone
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            // TODO REFACTOR: policy why to increase task priority
            if (stock.getLocked() > 0 &&
                shortages.get(0).getAtDay()
                        .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortageDao.save(shortages);
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processShortagesWarehouse(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = predict(
                productRefNo,
                DataRange.of(today, confShortagePredictionDaysAhead)
        ).build();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (shortages != null && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            if (currentStock.getLocked() > 0 &&
                shortages.get(0).getAtDay()
                        .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processShortagesQuality(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = predict(
                productRefNo,
                DataRange.of(today, confShortagePredictionDaysAhead)
        ).build();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.softNotifyPlanner(shortages);
            if (currentStock.getLocked() > 0 &&
                shortages.get(0).getAtDay()
                        .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }


    public void processShortagesProduction(List<ProductionEntity> products) {
        LocalDate today = LocalDate.now(clock);

        for (ProductionEntity production : products) {
            CurrentStock currentStock = stockService.getCurrentStock(production.getForm().getRefNo());
            List<ShortageEntity> shortages = predict(
                    production.getForm().getRefNo(),
                    DataRange.of(today, confShortagePredictionDaysAhead)
            ).build();
            List<ShortageEntity> previous = shortageDao.getForProduct(production.getForm().getRefNo());
            if (!shortages.isEmpty() && !shortages.equals(previous)) {
                notificationService.markOnPlan(shortages);
                if (currentStock.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                    jiraService.increasePriorityFor(production.getForm().getRefNo());
                }
                shortageDao.save(shortages);
            }
            if (shortages.isEmpty() && !previous.isEmpty()) {
                shortageDao.delete(production.getForm().getRefNo());
            }
        }
    }

    public ShortagesBuilder predict(String productRefNo, DataRange dates) {
        ProductionOutputs outputs = production.getOutputs(productRefNo, dates);
        Demands demandsPerDay = demands.get(productRefNo, dates);
        WarehouseStock level = stock.getStock(productRefNo);

        ShortagePrediction prediction = new ShortagePrediction(productRefNo, dates, outputs, demandsPerDay, level);

        return prediction.predict();
    }
}

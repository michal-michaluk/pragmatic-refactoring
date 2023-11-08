package shortages;

import acl.ShortageFinderACL;
import api.AdjustDemandDto;
import external.CurrentStock;
import external.JiraService;
import external.NotificationsService;
import external.StockService;
import production.ProductionEntity;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {

    private final DemandRepository demandRepository;
    private final ProductionRepository productionRepository;
    private final WarehouseRepository warehouseRepository;


    private ShortageDao shortageDao;
    private StockService stockService;
    private ShortageFinderACL shortageFinder;


    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;


    public ShortagePredictionService(DemandRepository demandRepository, ProductionRepository productionRepository, WarehouseRepository warehouseRepository) {
        this.demandRepository = demandRepository;
        this.productionRepository = productionRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public Shortages predict(String productRefNo, DateRange dates) {
        ProductionOutputs outputs = productionRepository.get(productRefNo, dates.start());
        Demands demandsPerDay = demandRepository.get(productRefNo, dates.start());
        long level = warehouseRepository.get(productRefNo);

        ShortagePrediction prediction = new ShortagePrediction(productRefNo, dates, outputs, demandsPerDay, level);

        return prediction.predict();
    }


    public void processShortagesProduction(List<ProductionEntity> products) {
        LocalDate today = LocalDate.now(clock);

        for (ProductionEntity production : products) {
            CurrentStock currentStock = stockService.getCurrentStock(production.getForm().getRefNo());
            List<ShortageEntity> shortages = shortageFinder.findShortages(
                    production.getForm().getRefNo(),
                    today, confShortagePredictionDaysAhead,
                    currentStock
            );
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

    public void processShortagesLogistic(AdjustDemandDto adjustment) {
        String productRefNo = adjustment.getProductRefNo();
        LocalDate today = LocalDate.now(clock);
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = shortageFinder.findShortages(
                productRefNo,
                today, confShortagePredictionDaysAhead,
                stock
        );
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

    public void processShortagesQuality(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = shortageFinder.findShortages(
                productRefNo,
                today, confShortagePredictionDaysAhead,
                currentStock
        );

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

    public void processShortagesWarehouse(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = shortageFinder.findShortages(
                productRefNo,
                today, confShortagePredictionDaysAhead,
                currentStock
        );

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
}

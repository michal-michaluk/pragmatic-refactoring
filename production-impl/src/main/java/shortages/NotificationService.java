package shortages;

import external.JiraService;

import java.time.Clock;
import java.time.LocalDate;

public class NotificationService {

    enum NotificationTrigger {
        ProductionPlanChanged,
        DemandsChanged,
        QualityEvent,
        WarehouseStockChanged,
    }

    private final Notification notification;
    private final JiraService jiraService;
    private final ConfigurationParameters config;
    private final Clock clock;

    public NotificationService(Notification notification, JiraService jiraService, ConfigurationParameters config, Clock clock) {
        this.notification = notification;
        this.jiraService = jiraService;
        this.config = config;
        this.clock = clock;
    }

    public void notifyAboutShortages(NotificationTrigger trigger, Shortages shortages) {
        LocalDate today = LocalDate.now(clock);

        switch (trigger) {
            case ProductionPlanChanged -> notification.markOnPlan(shortages);
            case QualityEvent -> notification.softNotifyPlanner(shortages);
            case DemandsChanged, WarehouseStockChanged -> notification.alertPlanner(shortages);
        }

        if (shortages.lockedPartsBeforeDate(today.plusDays(config.increaseQATaskPriorityInDays()))) {
            jiraService.increasePriorityFor(shortages.productRedNo());
        }
    }
}

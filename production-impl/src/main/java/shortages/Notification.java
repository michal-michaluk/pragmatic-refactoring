package shortages;

public interface Notification {

    void alertPlanner(Shortages shortages);

    void softNotifyPlanner(Shortages shortages);

    void markOnPlan(Shortages shortages);
}

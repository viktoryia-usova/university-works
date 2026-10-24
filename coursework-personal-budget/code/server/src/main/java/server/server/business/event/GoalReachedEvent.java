package server.server.business.event;

import org.springframework.context.ApplicationEvent;
import server.server.business.entity.finance.Goal;

public class GoalReachedEvent extends ApplicationEvent {

    private final Goal goal;

    public GoalReachedEvent(Object source, Goal goal) {
        super(source);
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }
}

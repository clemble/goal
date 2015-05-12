package com.clemble.casino.goal.aspect.outcome;

import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.aspect.GoalAspectFactory;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.GoalEndedEvent;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import org.springframework.core.Ordered;

/**
 * Created by mavarazy on 10/9/14.
 */
public class GoalLostOutcomeAspectFactory implements GoalAspectFactory<GoalEndedEvent> {

    final private GoalLostOutcomeAspect INSTANCE;

    public GoalLostOutcomeAspectFactory(SystemNotificationService notificationService) {
        this.INSTANCE = new GoalLostOutcomeAspect(notificationService);
    }

    @Override
    public GoalAspect<GoalEndedEvent> construct(GoalConfiguration configuration, GoalState context) {
        return INSTANCE;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

}

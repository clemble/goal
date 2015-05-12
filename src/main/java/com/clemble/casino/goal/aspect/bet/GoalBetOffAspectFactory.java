package com.clemble.casino.goal.aspect.bet;

import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.aspect.GoalAspectFactory;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.GoalManagementEvent;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import org.springframework.core.Ordered;

/**
 * Created by mavarazy on 1/13/15.
 */
public class GoalBetOffAspectFactory implements GoalAspectFactory<GoalManagementEvent> {

    final private SystemNotificationService notificationService;

    public GoalBetOffAspectFactory(SystemNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public GoalAspect<GoalManagementEvent> construct(GoalConfiguration configuration, GoalState state) {
        return new GoalBetOffAspect(configuration, notificationService);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 6;
    }

}

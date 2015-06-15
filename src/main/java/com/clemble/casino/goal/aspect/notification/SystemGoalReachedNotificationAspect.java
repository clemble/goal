package com.clemble.casino.goal.aspect.notification;

import com.clemble.casino.client.event.EventSelectors;
import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.client.event.OutcomeTypeSelector;
import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.GoalEndedEvent;
import com.clemble.casino.lifecycle.management.outcome.Outcome;
import com.clemble.casino.server.event.goal.SystemGoalReachedEvent;
import com.clemble.casino.server.player.notification.SystemNotificationService;

/**
 * Created by mavarazy on 2/3/15.
 */
public class SystemGoalReachedNotificationAspect extends GoalAspect<GoalEndedEvent> {

    final private SystemNotificationService notificationService;

    public SystemGoalReachedNotificationAspect(SystemNotificationService systemNotificationService) {
        super(EventSelectors.
                where(new EventTypeSelector(GoalEndedEvent.class)).
                and(new OutcomeTypeSelector(Outcome.won)));
        this.notificationService = systemNotificationService;
    }

    @Override
    protected void doEvent(GoalEndedEvent event, GoalState state) {
        // Step 1. Publishing goal reached aspect
        notificationService.send(new SystemGoalReachedEvent(state.getGoalKey(), state.getPlayer(), state));
    }

}

package com.clemble.casino.goal.aspect.bet;

import com.clemble.casino.client.event.EventSelectors;
import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.management.GoalPhase;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.GoalEndedEvent;
import com.clemble.casino.goal.lifecycle.management.event.GoalManagementEvent;
import com.clemble.casino.goal.lifecycle.management.event.GoalStartedEvent;
import com.clemble.casino.server.event.SystemEvent;
import com.clemble.casino.server.event.goal.SystemGoalBetOffEvent;
import com.clemble.casino.server.event.schedule.SystemAddJobScheduleEvent;
import com.clemble.casino.server.event.schedule.SystemRemoveJobScheduleEvent;
import com.clemble.casino.server.player.notification.SystemNotificationService;

/**
 * Created by mavarazy on 1/13/15.
 */
public class GoalBetOffAspect extends GoalAspect<GoalManagementEvent> {

    final private int betDays;
    final private SystemNotificationService notificationService;

    public GoalBetOffAspect(GoalConfiguration configuration, SystemNotificationService notificationService) {
        super(EventSelectors.where(new EventTypeSelector(GoalStartedEvent.class)).or(new EventTypeSelector(GoalEndedEvent.class)));
        this.notificationService = notificationService;
        this.betDays = configuration.getSupporterConfiguration().getBetDays();
    }

    @Override
    protected void doEvent(GoalManagementEvent event) {
        GoalState state = event.getBody();
        String goalKey = state.getGoalKey();
        if (event instanceof GoalStartedEvent) {
            // Step 1. Generating schedule event with bet forbid action
            SystemEvent scheduleEvent = new SystemAddJobScheduleEvent(goalKey, "forbid", new SystemGoalBetOffEvent(goalKey), state.getStartDate().plusDays(betDays));
            // Step 3. Scheduling trigger
            notificationService.send(scheduleEvent);
        } else if (event instanceof GoalEndedEvent) {
            if (event.getBody().getPhase() == GoalPhase.started) {
                // Step 1. Generating schedule forbid bet event
                SystemEvent unScheduleEvent = new SystemRemoveJobScheduleEvent(goalKey, "forbid");
                // Step 3. Scheduling trigger
                notificationService.send(unScheduleEvent);
            }
        }
    }

}

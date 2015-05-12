package com.clemble.casino.goal.aspect.timeout;

import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.*;
import com.clemble.casino.lifecycle.configuration.rule.timeout.MoveTimeoutRule;
import com.clemble.casino.server.event.goal.SystemGoalTimeoutEvent;
import com.clemble.casino.server.event.schedule.SystemAddJobScheduleEvent;
import com.clemble.casino.server.event.schedule.SystemRemoveJobScheduleEvent;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import org.joda.time.DateTime;

import static com.clemble.casino.client.event.EventSelectors.not;
import static com.clemble.casino.client.event.EventSelectors.where;

/**
 * Created by mavarazy on 1/4/15.
 */
public class GoalTimeoutAspect extends GoalAspect<GoalManagementEvent>{

    final private MoveTimeoutRule moveTimeoutRule;
    final private SystemNotificationService notificationService;

    public GoalTimeoutAspect(MoveTimeoutRule moveTimeoutRule, SystemNotificationService notificationService) {
        super(
            where(new EventTypeSelector(GoalManagementEvent.class)).
            and(not(new EventTypeSelector(GoalChangedBetEvent.class)))
        );
        this.moveTimeoutRule = moveTimeoutRule;
        this.notificationService = notificationService;
    }

    @Override
    protected void doEvent(GoalManagementEvent event, GoalState state) {
        // Step 1. Preparing for processing
        String goalKey = event.getBody().getGoalKey();
        // Step 2. Process depending on event
        if (event instanceof GoalEndedEvent) {
            // Case 1. Goal ended
            notificationService.send(new SystemRemoveJobScheduleEvent(goalKey, state.getPlayer()));
        } else if (event instanceof GoalStartedEvent || event instanceof GoalChangedStatusEvent || event instanceof GoalChangedStatusUpdateMissedEvent) {
            // Case 2. Goal changed
            DateTime deadline = state.getDeadline();
            DateTime moveTimeout = moveTimeoutRule.
                getTimeoutCalculator().
                calculate(state);
            DateTime breachTime = moveTimeout.isAfter(deadline) ? deadline :  moveTimeout;
            notificationService.send(new SystemAddJobScheduleEvent(goalKey, toKey(state.getPlayer()), new SystemGoalTimeoutEvent(goalKey), breachTime));
        }
    }

    private String toKey(String player) {
        return "timeout:" + player;
    }
}

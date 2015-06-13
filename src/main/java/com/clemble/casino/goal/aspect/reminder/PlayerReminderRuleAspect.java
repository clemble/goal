package com.clemble.casino.goal.aspect.reminder;

import static com.clemble.casino.client.event.EventSelectors.*;
import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.BasicReminderRule;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.*;
import com.clemble.casino.goal.service.ReminderService;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by mavarazy on 12/10/14.
 */
public class PlayerReminderRuleAspect extends GoalAspect<GoalManagementEvent> {

    final private Logger LOG = LoggerFactory.getLogger(PlayerReminderRuleAspect.class);

    final private long hoursToReminder;
    final private BasicReminderRule reminderRule;
    final private ReminderService reminderService;

    public PlayerReminderRuleAspect(BasicReminderRule reminderRule, ReminderService reminderService) {
        super(
            where(new EventTypeSelector(GoalManagementEvent.class)).
            and(not(new EventTypeSelector(GoalChangedBetEvent.class)))
        );
        this.reminderRule = reminderRule;
        this.reminderService = reminderService;
        this.hoursToReminder = TimeUnit.MILLISECONDS.toHours(reminderRule.getReminder());
    }

    @Override
    protected void doEvent(GoalManagementEvent event, GoalState state) {
        // Step 1. Generating goal
        String goal = state.getGoal();
        // Step 2. Generating reminder dates
        long breachTime = state.getConfiguration().getBreachTime(state).getMillis();
        if (event instanceof GoalEndedEvent) {
            reminderService.cancelReminder(event.getBody().getPlayer(), event.getBody().getGoalKey());
        } else {
            // Step 2.1. Generating remind time
            long remindTime = breachTime - reminderRule.getReminder();
            // Step 2.2. Scheduling reminder
            if (remindTime > System.currentTimeMillis()) {
                reminderService.scheduleReminder(
                        event.getBody().getPlayer(),
                        event.getBody().getGoalKey(),
                        "goal_due",
                        ImmutableMap.of("text", hoursToReminder + " hours to " + goal),
                        new DateTime(remindTime)
                );
            } else {
                LOG.error("remind time is in the past {} > {}", new DateTime(breachTime), new DateTime(remindTime));
            }
        }
    }

}

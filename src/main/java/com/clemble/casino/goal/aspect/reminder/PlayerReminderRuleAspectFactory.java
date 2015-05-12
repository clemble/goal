package com.clemble.casino.goal.aspect.reminder;

import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.aspect.GoalAspectFactory;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.BasicReminderRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.NoReminderRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.ReminderRule;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.GoalManagementEvent;
import com.clemble.casino.goal.service.ReminderService;

import java.util.function.Function;

/**
 * Created by mavarazy on 12/12/14.
 */
public class PlayerReminderRuleAspectFactory implements GoalAspectFactory<GoalManagementEvent> {

    final private ReminderService reminderService;
    final private Function<GoalConfiguration, ReminderRule> roleExtractor;
    final private int order;

    // TODO not the best solution think of something better
    public PlayerReminderRuleAspectFactory(int order, ReminderService emailReminderService, Function<GoalConfiguration, ReminderRule> roleExtractor) {
        this.order = order;
        this.roleExtractor = roleExtractor;
        this.reminderService = emailReminderService;
    }

    @Override
    public GoalAspect<GoalManagementEvent> construct(GoalConfiguration configuration, GoalState state) {
        ReminderRule reminderRule = roleExtractor.apply(configuration);
        if (reminderRule == null || reminderRule instanceof NoReminderRule) {
            return null;
        } else {
            return new PlayerReminderRuleAspect((BasicReminderRule) reminderRule, reminderService);
        }
    }

    @Override
    public int getOrder() {
        return order;
    }

}

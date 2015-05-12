package com.clemble.casino.goal.listener;

import com.clemble.casino.goal.action.GoalManagerFactoryFacade;
import com.clemble.casino.goal.event.GoalEvent;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.lifecycle.configuration.rule.timeout.MoveTimeoutRule;
import com.clemble.casino.lifecycle.configuration.rule.timeout.TotalTimeoutCalculator;
import com.clemble.casino.lifecycle.configuration.rule.timeout.TotalTimeoutRule;
import com.clemble.casino.lifecycle.management.event.action.Action;
import com.clemble.casino.lifecycle.management.event.action.PlayerAction;
import com.clemble.casino.goal.action.GoalManager;
import com.clemble.casino.server.event.goal.SystemGoalTimeoutEvent;
import com.clemble.casino.server.player.notification.SystemEventListener;
import org.joda.time.DateTime;

/**
 * Created by mavarazy on 11/8/14.
 */
public class SystemGoalTimeoutEventListener implements SystemEventListener<SystemGoalTimeoutEvent> {

    final private GoalManagerFactoryFacade managerFactory;

    public SystemGoalTimeoutEventListener(GoalManagerFactoryFacade managerFactory) {
        this.managerFactory = managerFactory;
    }

    @Override
    public void onEvent(SystemGoalTimeoutEvent event) {
        // Step 1. Fetching related GameState
        GoalManager manager = managerFactory.get(event.getGoalKey());
        // Step 2. Extracting game context
        GoalState state = manager.getState();
        // Step 3. Checking total timeout rule was not breached
        TotalTimeoutRule totalTimeoutRule = state.getConfiguration().getTotalTimeoutRule();
        TotalTimeoutCalculator totalTimeoutCalculator = totalTimeoutRule.getTimeoutCalculator();
        DateTime totalTimeout = totalTimeoutCalculator.calculate(state);
        if (totalTimeout.isBeforeNow()) {
            Action punishment = totalTimeoutRule.getPunishment().toBreachEvent();
            manager.process(new PlayerAction(event.getGoalKey(), state.getPlayer(), punishment));
            return;
        }
        // Step 4. Checking move timeout rule was not breached
        MoveTimeoutRule moveTimeoutRule = state.getConfiguration().getMoveTimeoutRule();
        DateTime moveTimeout = moveTimeoutRule.getTimeoutCalculator().calculate(state);
        if (moveTimeout.isBeforeNow()) {
            Action punishment = moveTimeoutRule.getPunishment().toBreachEvent();
            manager.process(new PlayerAction(event.getGoalKey(), state.getPlayer(), punishment));
            return;
        }
    }

    @Override
    public String getChannel() {
        return SystemGoalTimeoutEvent.CHANNEL;
    }

    @Override
    public String getQueueName() {
        return SystemGoalTimeoutEvent.CHANNEL + " > goal:management";
    }
}

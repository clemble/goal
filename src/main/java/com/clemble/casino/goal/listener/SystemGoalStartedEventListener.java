package com.clemble.casino.goal.listener;

import com.clemble.casino.goal.action.GoalManagerFactoryFacade;
import com.clemble.casino.server.event.goal.SystemGoalStartedEvent;
import com.clemble.casino.server.player.notification.SystemEventListener;

/**
 * Created by mavarazy on 9/20/14.
 */
public class SystemGoalStartedEventListener implements SystemEventListener<SystemGoalStartedEvent> {

    final private GoalManagerFactoryFacade managerFactory;

    public SystemGoalStartedEventListener(
        GoalManagerFactoryFacade managerFactory
    ) {
        this.managerFactory = managerFactory;
    }

    @Override
    public void onEvent(SystemGoalStartedEvent event) {
        // Step 0. Checking goal key was not prior created
        if (managerFactory.get(event.getGoalKey()) != null)
            return;
        // Step 1. Start manager for the goal
        managerFactory.start(event.getConstruction());
    }

    @Override
    public String getChannel() {
        return SystemGoalStartedEvent.CHANNEL;
    }

    @Override
    public String getQueueName() {
        return SystemGoalStartedEvent.CHANNEL + "> goal:management";
    }
}

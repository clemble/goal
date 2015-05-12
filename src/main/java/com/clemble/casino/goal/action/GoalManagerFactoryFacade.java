package com.clemble.casino.goal.action;

import com.clemble.casino.goal.lifecycle.construction.GoalConstruction;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.repository.GoalStateRepository;
import com.clemble.casino.server.player.notification.ServerNotificationService;

/**
 * Created by mavarazy on 9/20/14.
 */
public class GoalManagerFactoryFacade {

    final private GoalStateRepository stateRepository;
    final private ShortGoalManagerRepository shortGoalManagerFactory;

    public GoalManagerFactoryFacade(
        GoalManagerFactory shortGoalManagerFactory,
        GoalStateRepository stateRepository,
        ServerNotificationService notificationService) {
        this.stateRepository = stateRepository;
        this.shortGoalManagerFactory = new ShortGoalManagerRepository(shortGoalManagerFactory, stateRepository, notificationService);
    }

    public GoalManager start(GoalConstruction initiation) {
        // Step 1. Creating manager
        GoalManager manager = shortGoalManagerFactory.start(initiation);
        // Step 2. Starting manager
        manager.start();
        // Step 3. Returning created manager
        return manager;
    }

    public GoalManager get(String goalKey) {
        GoalState state = stateRepository.findOne(goalKey);
        if (state instanceof GoalState) {
            return shortGoalManagerFactory.create((GoalState) state);
        }
        return null;
    }

}

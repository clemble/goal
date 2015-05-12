package com.clemble.casino.goal.aspect.persistence;

import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.aspect.GoalAspectFactory;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.GoalManagementEvent;
import com.clemble.casino.goal.repository.GoalStateRepository;
import org.springframework.core.Ordered;

/**
 * Created by mavarazy on 14/10/14.
 */
public class GoalStatePersistenceAspectFactory implements GoalAspectFactory<GoalManagementEvent> {

    final private GoalStatePersistenceAspect persistenceAspect;

    public GoalStatePersistenceAspectFactory(GoalStateRepository stateRepository) {
        this.persistenceAspect = new GoalStatePersistenceAspect(stateRepository);
    }

    @Override
    public GoalAspect<GoalManagementEvent> construct(GoalConfiguration configuration, GoalState context) {
        return persistenceAspect;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 4;
    }

}

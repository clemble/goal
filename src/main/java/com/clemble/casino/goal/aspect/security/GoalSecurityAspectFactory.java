package com.clemble.casino.goal.aspect.security;

import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.aspect.GoalAspectFactory;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.lifecycle.management.event.action.PlayerAction;
import org.springframework.core.Ordered;

/**
 * Created by mavarazy on 1/17/15.
 */
public class GoalSecurityAspectFactory implements GoalAspectFactory<PlayerAction<?>> {

    @Override
    public GoalAspect<PlayerAction<?>> construct(GoalConfiguration configuration, GoalState state) {
        return new GoalSecurityAspect(state.getPlayer());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE - 12;
    }
}

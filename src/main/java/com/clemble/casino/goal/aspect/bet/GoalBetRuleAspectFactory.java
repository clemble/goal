package com.clemble.casino.goal.aspect.bet;

import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.aspect.GoalAspectFactory;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.lifecycle.management.event.action.PlayerAction;
import com.clemble.casino.lifecycle.management.event.action.bet.BetAction;
import org.springframework.core.Ordered;

/**
 * Created by mavarazy on 2/26/15.
 */
public class GoalBetRuleAspectFactory implements GoalAspectFactory<PlayerAction<BetAction>> {

    @Override
    public GoalAspect<PlayerAction<BetAction>> construct(GoalConfiguration configuration, GoalState state) {
        return new GoalBetRuleAspect(configuration.getSupporterConfiguration().getBetRule());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE - 11;
    }
}

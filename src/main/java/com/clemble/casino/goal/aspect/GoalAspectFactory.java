package com.clemble.casino.goal.aspect;

import com.clemble.casino.event.Event;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import org.springframework.core.PriorityOrdered;

/**
 * Created by mavarazy on 10/8/14.
 */
public interface GoalAspectFactory<T extends Event> extends PriorityOrdered {

    public GoalAspect<T> construct(GoalConfiguration configuration, GoalState state);

}

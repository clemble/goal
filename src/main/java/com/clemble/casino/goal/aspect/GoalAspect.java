package com.clemble.casino.goal.aspect;

import com.clemble.casino.client.event.EventSelector;
import com.clemble.casino.event.Event;
import com.clemble.casino.goal.lifecycle.management.GoalState;

/**
 * Created by mavarazy on 10/8/14.
 */
abstract public class GoalAspect<T extends Event> {

    final private EventSelector selector;

    public GoalAspect(EventSelector selector){
        this.selector= selector == null ? EventSelector.TRUE : selector;
    }

    final public void onEvent(T event, GoalState state){
        if(selector.filter(event))
            doEvent(event, state);
    }

    abstract protected void doEvent(T event, GoalState state);

}

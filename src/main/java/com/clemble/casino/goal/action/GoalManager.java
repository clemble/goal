package com.clemble.casino.goal.action;

import com.clemble.casino.error.ClembleCasinoError;
import com.clemble.casino.error.ClembleCasinoException;
import com.clemble.casino.event.Event;
import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.event.GoalEvent;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by mavarazy on 10/9/14.
 */
public class GoalManager
{
    final private static Logger LOG = LoggerFactory.getLogger(GoalManager.class);

    final private ReentrantLock sessionLock = new ReentrantLock();

    final private Collection<GoalAspect<?>> listenerArray;
    final private GoalState state;

    public GoalManager(GoalState state, Collection<GoalAspect<?>> listenerArray) {
        this.listenerArray = listenerArray;
        this.state = checkNotNull(state);
    }

    public GoalState getState() {
        return state;
    }

    public GoalEvent start() {
        // Step 1. Acquiring lock for the session, to exclude parallel processing
        sessionLock.lock();
        try {
            LOG.debug("Starting {}", state);
            // Step 2. Generating start event
            GoalEvent event = state.start();
            // Step 3 After move notification
            for (GoalAspect listener : listenerArray)
                listener.onEvent(event, state);
            // Step 3. Returning game event
            return event;
        } finally {
            sessionLock.unlock();
        }

    }

    public GoalEvent process(Event action) {
        // Step 1. Sanity check
        if (action == null)
            throw ClembleCasinoException.fromError(ClembleCasinoError.GoalActionInvalid);
        // Step 1.1 Add check for ended games check
        // Step 2. Acquiring lock for the session, to exclude parallel processing
        sessionLock.lock();
        try {
            LOG.debug("Processing {}", action);
            // Step 1. Before move notification
            for (GoalAspect listener : listenerArray) {
                listener.onEvent(action, state);
            }
            // Step 2. Processing in core
            Map.Entry<GoalEvent, GoalState> event = state.process(action);
            // Step 3 After move notification
            for (GoalAspect listener : listenerArray) {
                listener.onEvent(event.getKey(), event.getValue());
            }
            // Step 3. Returning game event
            return event.getKey();
        } finally {
            sessionLock.unlock();
        }
    }

}

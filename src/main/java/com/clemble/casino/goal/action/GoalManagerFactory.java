package com.clemble.casino.goal.action;

import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.aspect.GoalAspectFactory;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.OrderComparator;

import java.util.*;

/**
 * Created by mavarazy on 10/9/14.
 */
public class GoalManagerFactory implements ApplicationListener<ContextRefreshedEvent> {

    final private Logger LOG;

    final private List<GoalAspectFactory<?>> aspectFactories = new ArrayList<>();
    final private Class<?>[] aspectFactoryClasses;

    public GoalManagerFactory(Class<?>... aspectFactoryClass) {
        this.aspectFactoryClasses = aspectFactoryClass;
        LOG = LoggerFactory.getLogger("CMF - " + aspectFactoryClass[0].getSimpleName());
    }

    public GoalManager create(GoalState state, GoalConfiguration configuration) {
        // Step 1. Constructing GameAspects
        Collection<GoalAspect<?>> aspects = new ArrayList<>(aspectFactories.size());
        for (GoalAspectFactory<?> aspectFactory : aspectFactories) {
            GoalAspect<?> aspect = aspectFactory.construct(configuration, state);
            LOG.debug("processing aspect factory {} with aspect {}", aspectFactory, aspect);
            if(aspect != null) {
                aspects.add(aspect);
            }
        }
        return new GoalManager(state, aspects);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for (GoalAspectFactory<?> aspectFactory : ((ApplicationContext) event.getSource()).getBeansOfType(GoalAspectFactory.class).values())
            check(aspectFactory);
    }

    private Object check(Object bean) {
        // Step 1. Checking bean is applicable
        for (Class<?> aspectFactoryClass: aspectFactoryClasses) {
            if (aspectFactoryClass.isAssignableFrom(bean.getClass())) {
                GoalAspectFactory<?> aspectFactory = (GoalAspectFactory<?>) bean;
                // Step 1. Checking that bean is assignable to the basic class
                aspectFactories.add(aspectFactory);
                LOG.debug("Adding aspect {} {}", aspectFactory.getOrder(), aspectFactory);
                Collections.sort(aspectFactories, OrderComparator.INSTANCE);
                checkOrder();
            }
        }
        // Step 3. Returning processed bean
        return bean;
    }

    private void checkOrder() {
        HashSet<Integer> aspectIdentifiers = new HashSet<>();
        for(GoalAspectFactory aspectFactory: aspectFactories) {
            aspectIdentifiers.add(aspectFactory.getOrder());
        }
        if(aspectIdentifiers.size() != aspectFactories.size()) {
            for(GoalAspectFactory aspectFactory: aspectFactories)
                LOG.error("{} - {}", aspectFactory.getOrder(), aspectFactory);
            throw new IllegalArgumentException("Aspect factory order overlaps, which should never happen");
        }
    }

}

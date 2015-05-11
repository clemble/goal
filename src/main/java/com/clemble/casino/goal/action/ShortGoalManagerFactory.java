package com.clemble.casino.goal.action;

import com.clemble.casino.bet.Bet;
import com.clemble.casino.bet.PlayerBet;
import com.clemble.casino.goal.event.GoalEvent;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.construction.GoalConstruction;
import com.clemble.casino.goal.lifecycle.management.GoalContext;
import com.clemble.casino.goal.lifecycle.management.GoalPhase;
import com.clemble.casino.goal.lifecycle.management.GoalPlayerContext;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.repository.GoalStateRepository;
import com.clemble.casino.lifecycle.configuration.rule.time.PlayerClock;
import com.clemble.casino.money.Money;
import com.clemble.casino.payment.Bank;
import com.clemble.casino.server.action.ClembleManager;
import com.clemble.casino.server.action.ClembleManagerFactory;
import com.clemble.casino.server.player.notification.ServerNotificationService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by mavarazy on 10/9/14.
 */
public class ShortGoalManagerFactory implements GoalManagerFactory {

    final private GoalStateRepository stateRepository;
    final private ServerNotificationService notificationService;
    final private ClembleManagerFactory<GoalConfiguration> managerFactory;

    public ShortGoalManagerFactory(
        ClembleManagerFactory<GoalConfiguration> managerFactory,
        GoalStateRepository stateRepository,
        ServerNotificationService notificationService) {
        this.managerFactory = managerFactory;
        this.stateRepository = stateRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ClembleManager<GoalEvent, ? extends GoalState> start(GoalConstruction construction, GoalContext parent) {
        GoalConfiguration goalConfiguration = (GoalConfiguration) construction.getConfiguration();
        // Step 1. Saving record
        Bank bank = new Bank(new ArrayList<PlayerBet>(), new Bet(Money.ZERO, Money.ZERO), Money.ZERO);
        bank.add(new PlayerBet(construction.getPlayer(), construction.getConfiguration().getBet()));
        // Step 2. Creating state
        GoalPlayerContext playerContext = new GoalPlayerContext(construction.getPlayer(), PlayerClock.create(construction.getConfiguration()));
        GoalContext goalContext = new GoalContext(parent, Collections.singletonList(playerContext));
        long deadlineTime = goalConfiguration.getTotalTimeoutRule().getTimeoutCalculator().calculate(construction.getTimezone(), construction.getStartDate().getMillis(), 0);
        DateTime deadline = new DateTime(deadlineTime, DateTimeZone.forID(construction.getTimezone()));
        GoalState state = new GoalState(
            construction.getGoalKey(),
            construction.getStartDate(),
            deadline,
            construction.getPlayer(),
            bank,
            construction.getGoal(),
            construction.getTimezone(),
            construction.getTag(),
            construction.getConfiguration(),
            goalContext,
            new HashSet<>(),
            "Go for it",
            GoalPhase.started,
            new TreeSet<>(),
            null,
            new TreeSet<>());
        // Step 3. Saving state
        stateRepository.save(state);
        // Step 4. Creating manager factory
        return create(state);
    }

    public ClembleManager<GoalEvent, ? extends GoalState> create(GoalState state) {
        return managerFactory.create(state, state.getConfiguration());
    }

}

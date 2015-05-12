package com.clemble.casino.goal.controller;

import static com.clemble.casino.goal.GoalWebMapping.*;

import com.clemble.casino.error.ClembleCasinoError;
import com.clemble.casino.error.ClembleCasinoException;
import com.clemble.casino.goal.action.GoalManagerFactoryFacade;
import com.clemble.casino.goal.event.GoalEvent;
import com.clemble.casino.goal.lifecycle.management.GoalInspiration;
import com.clemble.casino.goal.lifecycle.management.GoalPhase;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.service.GoalActionService;
import com.clemble.casino.goal.repository.GoalStateRepository;
import com.clemble.casino.lifecycle.management.event.action.Action;
import com.clemble.casino.lifecycle.management.event.action.PlayerAction;
import static org.springframework.http.HttpStatus.*;

import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import com.clemble.casino.payment.PaymentTransaction;
import com.clemble.casino.payment.event.PaymentFreezeEvent;
import com.clemble.casino.payment.service.PlayerAccountService;
import com.clemble.casino.server.ServerController;
import com.clemble.casino.server.event.payment.SystemPaymentFreezeRequestEvent;
import com.clemble.casino.server.event.payment.SystemPaymentTransactionRequestEvent;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.web.bind.annotation.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.Collections;
import java.util.List;

/**
 * Created by mavarazy on 10/9/14.
 */
@RestController
public class GoalActionController implements GoalActionService, ServerController {

    final private PlayerAccountService accountService;
    final private GoalManagerFactoryFacade factoryFacade;
    final private GoalStateRepository stateRepository;
    final private SystemNotificationService notificationService;

    public GoalActionController(
            GoalManagerFactoryFacade factoryFacade,
            GoalStateRepository stateRepository,
            PlayerAccountService accountService,
            SystemNotificationService notificationService) {
        this.factoryFacade = factoryFacade;
        this.stateRepository = stateRepository;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    @Override
    public List<GoalState> myActive() {
        throw new IllegalAccessError();
    }

    @RequestMapping(method = GET, value = MY_ACTIVE_GOALS, produces = PRODUCES)
    @ResponseStatus(value = OK)
    public List<GoalState> myActive(@CookieValue("player") String player) {
        return stateRepository.findByPlayerAndPhaseNot(player, GoalPhase.finished);
    }

    @Override
    @RequestMapping(method = GET, value = PLAYER_ACTIVE_GOALS, produces = PRODUCES)
    @ResponseStatus(value = OK)
    public List<GoalState> getActive(@PathVariable("player") String player) {
        return stateRepository.findByPlayerAndPhaseNot(player, GoalPhase.finished);
    }

    @Override
    public GoalEvent process(String goalKey, Action action) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = POST, value = GOAL_STATE_ACTION, produces = PRODUCES)
    @ResponseStatus(value = OK)
    public GoalEvent process(@PathVariable("goalKey") String goalKey, @CookieValue("player") String player, @RequestBody Action action) {
        PlayerAction playerAction = new PlayerAction(goalKey, player, action);
        return factoryFacade.get(goalKey).process(playerAction);
    }

    @Override
    @RequestMapping(method = GET, value = GOAL_STATE, produces = PRODUCES)
    @ResponseStatus(value = OK)
    public GoalState getState(@PathVariable("goalKey") String goalKey) {
        return stateRepository.findOne(goalKey);
    }

    @Override
    public GoalInspiration inspire(String goalKey, String inspirationText) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = POST, value = GOAL_STATE_INSPIRATIONS, produces = PRODUCES)
    @ResponseStatus(value = OK)
    public GoalInspiration inspire(@CookieValue("player") String player, @PathVariable("goalKey") String goalKey, @RequestBody String inspirationText) {
        // Step 1. Creating new inspiration
        GoalState state = stateRepository.findOne(goalKey);
        if (state.getPhase() == GoalPhase.finished)
            throw new IllegalArgumentException();
        // Step 2. Checking there are sufficient funds for this transaction
        if (!accountService.canAfford(Collections.singleton(player), Currency.inspiration, 1L).isEmpty()) {
            throw ClembleCasinoException.fromError(ClembleCasinoError.PaymentTransactionInsufficientMoney);
        }
        // Step 3. Sending freeze request
        notificationService.send(SystemPaymentFreezeRequestEvent.create(goalKey, player, Money.INSPIRATION));
        // Step 4. Adding inspiration to state
        GoalInspiration inspiration = new GoalInspiration(player, inspirationText, DateTime.now(state.getTimezone()));
        state.getInspirations().add(inspiration);
        stateRepository.save(state);
        return inspiration;
    }


}

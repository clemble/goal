package com.clemble.casino.goal.aspect.bet;

import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.aspect.GoalAspectFactory;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.GoalChangedBetEvent;
import com.clemble.casino.payment.service.PlayerAccountService;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import org.springframework.core.Ordered;

/**
 * Created by mavarazy on 1/17/15.
 */
public class GoalBetPaymentAspectFactory implements GoalAspectFactory<GoalChangedBetEvent> {

    final private PlayerAccountService accountService;
    final private SystemNotificationService notificationService;

    final private GoalBetPaymentAspect ASPECT;

    public GoalBetPaymentAspectFactory(PlayerAccountService accountService, SystemNotificationService notificationService) {
        this.accountService = accountService;
        this.notificationService = notificationService;

        this.ASPECT = new GoalBetPaymentAspect(accountService, notificationService);
    }

    @Override
    public GoalAspect<GoalChangedBetEvent> construct(GoalConfiguration configuration, GoalState state) {
        return ASPECT;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE - 10;
    }

}

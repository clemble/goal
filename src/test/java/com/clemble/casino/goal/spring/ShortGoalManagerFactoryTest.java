package com.clemble.casino.goal.spring;

import com.clemble.casino.bet.Bet;
import com.clemble.casino.goal.action.GoalManagerFactoryFacade;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.GoalRoleConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.BasicReminderRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.NoReminderRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.share.ShareRule;
import com.clemble.casino.goal.lifecycle.construction.GoalConstruction;
import com.clemble.casino.goal.repository.GoalStateRepository;
import com.clemble.casino.lifecycle.configuration.rule.bet.LimitedBetRule;
import com.clemble.casino.lifecycle.configuration.rule.breach.LooseBreachPunishment;
import com.clemble.casino.lifecycle.configuration.rule.timeout.*;
import com.clemble.casino.lifecycle.construction.ConstructionState;
import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.concurrent.TimeUnit;

/**
 * Created by mavarazy on 16/10/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { GoalManagementSpringConfiguration.class })
public class ShortGoalManagerFactoryTest {

    @Autowired
    public GoalManagerFactoryFacade managerFactory;

    @Autowired
    public GoalStateRepository recordRepository;

    final private GoalConfiguration configuration = new GoalConfiguration(
        "basic",
        "Basic",
        new Bet(Money.create(Currency.point, 500), Money.create(Currency.point, 50)),
        new BasicReminderRule(TimeUnit.HOURS.toMillis(4)),
        new BasicReminderRule(TimeUnit.HOURS.toMillis(2)),
        new MoveTimeoutRule(LooseBreachPunishment.getInstance(), new MoveTimeoutCalculatorByLimit(TimeUnit.SECONDS.toMillis(1))),
        new TotalTimeoutRule(LooseBreachPunishment.getInstance(), new TotalTimeoutCalculatorByLimit(TimeUnit.SECONDS.toMillis(3))),
        new GoalRoleConfiguration(3, LimitedBetRule.create(50, 100), 50, NoReminderRule.INSTANCE, NoReminderRule.INSTANCE),
        ShareRule.EMPTY
    );

    @Test
    public void testSimpleCreation() {
        // Step 1. Generating goal
        String goalKey = RandomStringUtils.randomAlphabetic(10);
        String player = RandomStringUtils.randomAlphabetic(10);
        GoalConstruction initiation = new GoalConstruction(
            goalKey,
            player,
            "Create goal state",
            DateTimeZone.UTC,
            "",
            DateTime.now(DateTimeZone.UTC),
            configuration,
            ConstructionState.constructed
        );
        // Step 2. Starting initiation
        managerFactory.start(initiation);
        // Step 3. Checking there is a state for the game
        Assert.assertNotEquals(managerFactory.get(goalKey), null);
    }

}

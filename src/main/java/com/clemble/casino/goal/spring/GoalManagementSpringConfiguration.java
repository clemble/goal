package com.clemble.casino.goal.spring;

import com.clemble.casino.goal.action.GoalManagerFactory;
import com.clemble.casino.goal.action.GoalManagerFactoryFacade;
import com.clemble.casino.goal.aspect.GoalAspectFactory;
import com.clemble.casino.goal.aspect.bet.GoalBetPaymentAspectFactory;
import com.clemble.casino.goal.aspect.bet.GoalBetOffAspectFactory;
import com.clemble.casino.goal.aspect.bet.GoalBetRuleAspectFactory;
import com.clemble.casino.goal.aspect.notification.GoalPlayerNotificationAspectFactory;
import com.clemble.casino.goal.aspect.notification.SystemGoalReachedNotificationAspectFactory;
import com.clemble.casino.goal.aspect.outcome.GoalLostOutcomeAspectFactory;
import com.clemble.casino.goal.aspect.outcome.GoalWonOutcomeAspectFactory;
import com.clemble.casino.goal.aspect.persistence.GoalStatePersistenceAspectFactory;
import com.clemble.casino.goal.aspect.reminder.PlayerReminderRuleAspectFactory;
import com.clemble.casino.goal.aspect.reminder.SupporterReminderRuleAspectFactory;
import com.clemble.casino.goal.aspect.security.GoalSecurityAspectFactory;
import com.clemble.casino.goal.aspect.share.ShareRuleAspectFactory;
import com.clemble.casino.goal.aspect.timeout.GoalTimeoutAspectFactory;
import com.clemble.casino.goal.controller.GoalActionController;
import com.clemble.casino.goal.lifecycle.configuration.GoalRoleConfiguration;
import com.clemble.casino.goal.listener.SystemGoalBetOffEventListener;
import com.clemble.casino.goal.listener.SystemGoalStartedEventListener;
import com.clemble.casino.goal.listener.SystemGoalTimeoutEventListener;
import com.clemble.casino.goal.repository.GoalStateRepository;
import com.clemble.casino.goal.service.EmailReminderService;
import com.clemble.casino.goal.service.PhoneReminderService;
import com.clemble.casino.payment.service.PlayerAccountService;
import com.clemble.casino.server.player.notification.ServerNotificationService;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import com.clemble.casino.server.spring.common.*;
import com.clemble.casino.social.SocialProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;

/**
 * Created by mavarazy on 9/12/14.
 */
@Configuration
@Import({
    CommonSpringConfiguration.class,
    MongoSpringConfiguration.class,
    PaymentClientSpringConfiguration.class
})
public class GoalManagementSpringConfiguration implements SpringConfiguration {

    @Bean
    public GoalStateRepository goalStateRepository(MongoRepositoryFactory repositoryFactory) {
        return repositoryFactory.getRepository(GoalStateRepository.class);
    }

    @Bean
    public GoalActionController goalActionServiceController(
        GoalManagerFactoryFacade factoryFacade,
        GoalStateRepository goalStateRepository,
        @Qualifier("playerAccountClient") PlayerAccountService accountService,
        SystemNotificationService notificationService) {
        return new GoalActionController(
            factoryFacade,
            goalStateRepository,
            accountService,
            notificationService);
    }

    @Bean
    public EmailReminderService emailReminderService(SystemNotificationService systemNotificationService) {
        return new EmailReminderService(systemNotificationService);
    }

    @Bean
    public PlayerReminderRuleAspectFactory heroEmailReminderRuleAspectFactory(EmailReminderService emailReminderService) {
        return new PlayerReminderRuleAspectFactory(
            Ordered.HIGHEST_PRECEDENCE + 4,
            emailReminderService,
            (configuration) -> configuration.getEmailReminderRule()
        );
    }

    @Bean
    public SupporterReminderRuleAspectFactory supportEmailReminderRuleAspectFactory(EmailReminderService emailReminderService) {
        return new SupporterReminderRuleAspectFactory(
            Ordered.HIGHEST_PRECEDENCE + 5,
            emailReminderService,
            (configuration) -> {
                GoalRoleConfiguration roleConfiguration = configuration.getSupporterConfiguration();
                return roleConfiguration != null ? roleConfiguration.getEmailReminderRule() : null;
            }
        );
    }

    @Bean
    public PhoneReminderService phoneReminderService(SystemNotificationService systemNotificationService) {
        return new PhoneReminderService(systemNotificationService);
    }

    @Bean
    public PlayerReminderRuleAspectFactory heroPhoneReminderRuleAspectFactory(PhoneReminderService reminderService) {
        return new PlayerReminderRuleAspectFactory(
            Ordered.HIGHEST_PRECEDENCE + 7,
            reminderService,
            (configuration) -> configuration.getPhoneReminderRule()
        );
    }

    @Bean
    public GoalSecurityAspectFactory goalSecurityAspectFactory() {
        return new GoalSecurityAspectFactory();
    }

    @Bean
    public GoalBetPaymentAspectFactory goalBetAspectFactory(
        @Qualifier("playerAccountClient") PlayerAccountService accountService,
        SystemNotificationService notificationService) {
        return new GoalBetPaymentAspectFactory(accountService, notificationService);
    }

    @Bean
    public ShareRuleAspectFactory twitterShareRuleAspectFactory(SystemNotificationService notificationService) {
        return new ShareRuleAspectFactory(SocialProvider.twitter, notificationService, Ordered.LOWEST_PRECEDENCE - 101);
    }

    @Bean
    public ShareRuleAspectFactory facebookShareRuleAspectFactory(SystemNotificationService notificationService) {
        return new ShareRuleAspectFactory(SocialProvider.facebook, notificationService, Ordered.LOWEST_PRECEDENCE - 100);
    }

    @Bean
    public SupporterReminderRuleAspectFactory supportPhoneReminderRuleAspectFactory(PhoneReminderService reminderService) {
        return new SupporterReminderRuleAspectFactory(
            Ordered.HIGHEST_PRECEDENCE + 8,
            reminderService,
            (configuration) -> {
                GoalRoleConfiguration roleConfiguration = configuration.getSupporterConfiguration();
                return roleConfiguration != null ? roleConfiguration.getPhoneReminderRule() : null;
            }
        );
    }

    @Bean
    public GoalManagerFactory shortGoalManagerFactory() {
        return new GoalManagerFactory(GoalAspectFactory.class);
    }

    @Bean
    public GoalManagerFactoryFacade goalManagerFactoryFacade(
        @Qualifier("shortGoalManagerFactory") GoalManagerFactory shortGoalManagerFactory,
        GoalStateRepository goalStateRepository,
        @Qualifier("playerNotificationService") ServerNotificationService notificationService) {
        return new GoalManagerFactoryFacade(shortGoalManagerFactory, goalStateRepository, notificationService);
    }

    @Bean
    public SystemGoalStartedEventListener systemGoalStartedEventListener(
        GoalManagerFactoryFacade goalManagerFactoryFacade) {
        SystemGoalStartedEventListener eventListener = new SystemGoalStartedEventListener(goalManagerFactoryFacade);
        return eventListener;
    }

    @Bean
    public SystemGoalTimeoutEventListener systemGoalTimeoutEventListener(
        GoalManagerFactoryFacade goalManagerFactoryFacade) {
        SystemGoalTimeoutEventListener eventListener = new SystemGoalTimeoutEventListener(goalManagerFactoryFacade);
        return eventListener;
    }

    @Bean
    public SystemGoalBetOffEventListener systemGoalForbidBetEventListener(
        GoalManagerFactoryFacade goalManagerFactoryFacade) {
        SystemGoalBetOffEventListener eventListener = new SystemGoalBetOffEventListener(goalManagerFactoryFacade);
        return eventListener;
    }

    @Bean
    public GoalTimeoutAspectFactory goalTimeAspectFactory(SystemNotificationService systemNotificationService){
        return new GoalTimeoutAspectFactory(systemNotificationService);
    }

    @Bean
    public GoalStatePersistenceAspectFactory goalPersistenceAspectFactory(GoalStateRepository stateRepository) {
        return new GoalStatePersistenceAspectFactory(stateRepository);
    }

    @Bean
    public GoalLostOutcomeAspectFactory goalMissedOutcomeAspectFactory(SystemNotificationService systemNotificationService) {
        return new GoalLostOutcomeAspectFactory(systemNotificationService);
    }

    @Bean
    public GoalWonOutcomeAspectFactory goalReachedOutcomeAspectFactory(SystemNotificationService systemNotificationService) {
        return new GoalWonOutcomeAspectFactory(systemNotificationService);
    }

    @Bean
    public GoalBetRuleAspectFactory goalBetRuleAspectFactory() {
        return new GoalBetRuleAspectFactory();
    }

    @Bean
    public GoalPlayerNotificationAspectFactory goalPlayerNotificationAspectFactory(
        @Qualifier("playerNotificationService") ServerNotificationService notificationService) {
        return new GoalPlayerNotificationAspectFactory(notificationService);
    }

    @Bean
    public GoalBetOffAspectFactory goalBetForbidAspectFactory(
        SystemNotificationService notificationService) {
        return new GoalBetOffAspectFactory(notificationService);
    }

    @Bean
    public SystemGoalReachedNotificationAspectFactory systemGoalReachedNotificationAspectFactory(SystemNotificationService notificationService) {
        return new SystemGoalReachedNotificationAspectFactory(notificationService);
    }

}

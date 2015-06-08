package com.clemble.casino.goal.spring;

import com.clemble.casino.goal.GoalKeyGenerator;
import com.clemble.casino.goal.controller.GoalConstructionController;
import com.clemble.casino.goal.repository.GoalConstructionRepository;
import com.clemble.casino.goal.service.ServerGoalConstructionService;
import com.clemble.casino.payment.service.PlayerAccountService;
import com.clemble.casino.server.key.RedisKeyFactory;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import com.clemble.casino.server.spring.common.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import redis.clients.jedis.JedisPool;

/**
 * Created by mavarazy on 9/10/14.
 */
@Configuration
@Import({
    CommonSpringConfiguration.class,
    PaymentClientSpringConfiguration.class,
    MongoSpringConfiguration.class,
    RedisSpringConfiguration.class})
public class GoalConstructionSpringConfiguration {

    @Bean
    public GoalConstructionRepository goalConstructionRepository(MongoRepositoryFactory repositoryFactory) {
        return repositoryFactory.getRepository(GoalConstructionRepository.class);
    }

    @Bean
    public ServerGoalConstructionService goalConstructionService(
        GoalKeyGenerator keyGenerator,
        @Qualifier("playerAccountClient") PlayerAccountService accountService,
        SystemNotificationService notificationService,
        GoalConstructionRepository constructionRepository) {
        return new ServerGoalConstructionService(
            keyGenerator,
            accountService,
            notificationService,
            constructionRepository);
    }

    @Bean
    public GoalConstructionController goalConstructionServiceController(ServerGoalConstructionService constructionService) {
        return new GoalConstructionController(constructionService);
    }

    @Bean
    public GoalKeyGenerator goalKeyGenerator(JedisPool jedisPool) {
        return new GoalKeyGenerator(new RedisKeyFactory("GOAL_COUNTER", "A", jedisPool));
    }

}

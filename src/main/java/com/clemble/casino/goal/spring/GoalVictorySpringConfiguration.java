package com.clemble.casino.goal.spring;

import com.clemble.casino.goal.controller.GoalVictoryController;
import com.clemble.casino.goal.repository.GoalStateRepository;
import com.clemble.casino.server.spring.common.CommonSpringConfiguration;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by mavarazy on 3/14/15.
 */

@Configuration
@Import({
    CommonSpringConfiguration.class
})
public class GoalVictorySpringConfiguration implements SpringConfiguration {

    @Bean
    public GoalVictoryController goalVictoryServiceController(GoalStateRepository stateRepository) {
        return new GoalVictoryController(stateRepository);
    }


}

package com.clemble.casino.goal.spring;

import com.clemble.casino.goal.controller.GoalConfigurationController;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfigurationChoices;
import com.clemble.casino.goal.lifecycle.configuration.IntervalGoalConfigurationBuilder;
import com.clemble.casino.json.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by mavarazy on 9/16/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = GoalConfigurationSpringConfiguration.class)
public class GoalConfigurationServiceControllerTest {

    @Autowired
    public GoalConfigurationController configurationServiceController;

    @Test
    public void testConfigurationsRead() throws IOException {
        ObjectMapper objectMapper = ObjectMapperUtils.OBJECT_MAPPER;
        String configurationSerialized = objectMapper.writeValueAsString(configurationServiceController.getConfigurations());
        Collection<GoalConfiguration> readConfigurations =  Arrays.asList(objectMapper.readValue(configurationSerialized, GoalConfiguration[].class));
        Assert.assertEquals(readConfigurations, configurationServiceController.getConfigurations());
    }

    @Test
    public void testChoiceRead() throws IOException {
        ObjectMapper objectMapper = ObjectMapperUtils.OBJECT_MAPPER;
        String choiceSerialized = objectMapper.writeValueAsString(configurationServiceController.getChoices());
        GoalConfigurationChoices readChoices =  objectMapper.readValue(choiceSerialized, GoalConfigurationChoices.class);
        Assert.assertEquals(readChoices, configurationServiceController.getChoices());

    }

    @Test
    public void testIntervalRead() throws IOException {
        ObjectMapper objectMapper = ObjectMapperUtils.OBJECT_MAPPER;
        String intervalSerialized = objectMapper.writeValueAsString(configurationServiceController.getIntervalBuilder());
        IntervalGoalConfigurationBuilder intervalConfigurations =  objectMapper.readValue(intervalSerialized, IntervalGoalConfigurationBuilder.class);
        Assert.assertEquals(intervalConfigurations, configurationServiceController.getIntervalBuilder());
    }

}

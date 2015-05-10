package com.clemble.casino.goal.controller;

import com.clemble.casino.WebMapping;
import com.clemble.casino.goal.GoalWebMapping;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.service.GoalVictoryService;
import com.clemble.casino.goal.repository.GoalStateRepository;
import com.clemble.casino.server.ExternalController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by mavarazy on 3/14/15.
 */
@RestController
public class GoalVictoryController implements GoalVictoryService, ExternalController {

    final private GoalStateRepository stateRepository;

    public GoalVictoryController(GoalStateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Override
    public List<GoalState> listMy() {
        throw new IllegalArgumentException();
    }

    @RequestMapping(method = RequestMethod.GET, value = GoalWebMapping.MY_VICTORIES, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<GoalState> listMy(@CookieValue("player") String me) {
        return stateRepository.findByPlayerOrderByDeadlineDesc(me);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = GoalWebMapping.PLAYER_VICTORIES, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<GoalState> list(@PathVariable("player") String player) {
        return stateRepository.findByPlayerOrderByDeadlineDesc(player);
    }

}


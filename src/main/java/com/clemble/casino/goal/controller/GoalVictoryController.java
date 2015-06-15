package com.clemble.casino.goal.controller;

import com.clemble.casino.WebMapping;
import com.clemble.casino.goal.GoalWebMapping;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.service.GoalVictoryService;
import com.clemble.casino.goal.repository.GoalStateRepository;
import com.clemble.casino.lifecycle.management.outcome.Outcome;
import com.clemble.casino.server.ServerController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by mavarazy on 3/14/15.
 */
@RestController
public class GoalVictoryController implements GoalVictoryService, ServerController {

    final private GoalStateRepository stateRepository;

    public GoalVictoryController(GoalStateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Override
    public List<GoalState> listMy() {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = RequestMethod.GET, value = GoalWebMapping.MY_VICTORIES, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<GoalState> listMy(@CookieValue("player") String me) {
        return stateRepository.findByPlayerOrderByDeadlineDesc(me);
    }

    @Override
    public Integer countMy() {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = RequestMethod.GET, value = GoalWebMapping.MY_VICTORIES_COUNT, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public Integer countMy(@CookieValue("player") String my) {
        return stateRepository.countWithOutcome(my, Outcome.won);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = GoalWebMapping.PLAYER_VICTORIES, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<GoalState> list(@PathVariable("player") String player) {
        return stateRepository.findByPlayerOrderByDeadlineDesc(player);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = GoalWebMapping.PLAYER_VICTORIES_COUNT, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public Integer count(@PathVariable("player") String player) {
        return stateRepository.countWithOutcome(player, Outcome.won);
    }


}


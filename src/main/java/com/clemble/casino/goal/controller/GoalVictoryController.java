package com.clemble.casino.goal.controller;

import com.clemble.casino.WebMapping;
import com.clemble.casino.goal.GoalWebMapping;
import com.clemble.casino.goal.lifecycle.management.service.GoalVictoryService;
import com.clemble.casino.goal.lifecycle.record.GoalRecord;
import com.clemble.casino.goal.repository.GoalRecordRepository;
import com.clemble.casino.server.ExternalController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by mavarazy on 3/14/15.
 */
@RestController
public class GoalVictoryController implements GoalVictoryService, ExternalController {

    final private GoalRecordRepository recordRepository;

    public GoalVictoryController(GoalRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public List<GoalRecord> listMy() {
        throw new IllegalArgumentException();
    }

    @RequestMapping(method = RequestMethod.GET, value = GoalWebMapping.MY_VICTORIES, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<GoalRecord> listMy(@CookieValue("player") String me) {
        return recordRepository.findByPlayer(me);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = GoalWebMapping.PLAYER_VICTORIES, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<GoalRecord> list(@PathVariable("player") String player) {
        return recordRepository.findByPlayer(player);
    }

}


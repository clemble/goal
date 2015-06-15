package com.clemble.casino.goal.repository;

import com.clemble.casino.goal.lifecycle.management.GoalPhase;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.lifecycle.management.outcome.Outcome;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * Created by mavarazy on 10/9/14.
 */
public interface GoalStateRepository extends MongoRepository<GoalState, String> {

    List<GoalState> findByPlayer(String player);

    List<GoalState> findByPlayerAndPhaseNot(String player, GoalPhase phase);

    List<GoalState> findByPlayerOrderByDeadlineDesc(String player);

    @Query(value = "{'player': ?0, 'outcome': ?1}", count = true)
    Integer countWithOutcome(String player, Outcome outcome);

    void delete(String id);

}

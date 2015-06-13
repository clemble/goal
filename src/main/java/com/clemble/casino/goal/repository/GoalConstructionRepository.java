package com.clemble.casino.goal.repository;

import com.clemble.casino.lifecycle.construction.ConstructionState;
import com.clemble.casino.goal.lifecycle.construction.GoalConstruction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by mavarazy on 9/10/14.
 */
public interface GoalConstructionRepository extends MongoRepository<GoalConstruction, String> {

    List<GoalConstruction> findByPlayerAndState(String player, ConstructionState state);

}

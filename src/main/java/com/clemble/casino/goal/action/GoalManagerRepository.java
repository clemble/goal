package com.clemble.casino.goal.action;

import com.clemble.casino.goal.lifecycle.construction.GoalConstruction;

/**
 * Created by mavarazy on 10/9/14.
 */
public interface GoalManagerRepository {

    public GoalManager start(GoalConstruction construction);

}

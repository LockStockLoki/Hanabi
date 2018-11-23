package com.fossgalaxy.games.fireworks.ai.RobertSalman;

import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RobertSalmanNode {

    public static final double DefaultExplorationFactor = Math.sqrt(3);

    private final RobertSalmanNode ParentNode;
    private final int AgentID;
    private final Action MoveToState;
    private final double ExplorationFactor;
    private final Collection<Action> UnexpandedActions;

    public RobertSalmanNode(RobertSalmanNode Parent, int AgentID, Action MoveToState, double ExplorationFactor,
            Collection<Action> UnexpandedActions) {

        this.ParentNode = Parent;
        this.AgentID = AgentID;
        this.MoveToState = MoveToState;
        this.ExplorationFactor = ExplorationFactor;
        this.UnexpandedActions = new ArrayList<>(UnexpandedActions);

    }
}
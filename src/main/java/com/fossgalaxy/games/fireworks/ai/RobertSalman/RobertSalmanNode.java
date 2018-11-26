package com.fossgalaxy.games.fireworks.ai.RobertSalman;

import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

import ch.qos.logback.core.pattern.parser.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RobertSalmanNode {

    int Visits = 0;
    double Score = 0;

    RobertSalmanNode Parent;
    List<RobertSalmanNode> Children;
    double ExplorationFactor;

    int AgentID;
    Collection<Action> UnexpandedActions;
    Action MoveToState;

    public RobertSalmanNode(int AgentID, Collection<Action> UnexpandedActions, double ExplorationFactor,
            RobertSalmanNode Parent) {
        this.AgentID = AgentID;
        this.Parent = Parent;
        this.UnexpandedActions = new ArrayList<>(UnexpandedActions);
        this.Children = new ArrayList<>();
        this.ExplorationFactor = ExplorationFactor;

    }

    public void AddCildNode(RobertSalmanNode Node) {
        UnexpandedActions.remove(Node.GetAction());
        Children.add(Node);
    }

    public List<RobertSalmanNode> GetChildren() {
        return Children;
    }

    public Action GetAction() {
        return MoveToState;
    }

}

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

    public static final double DefaultExplorationFactor = Math.sqrt(3);

    private final List<RobertSalmanNode> Children;

    private final RobertSalmanNode ParentNode;
    private final int AgentID;
    private final Action MoveToState;
    private final double ExplorationFactor;
    private final Collection<Action> UnexpandedActions;
    private final Random _Random;

    private double Score;
    private int Visits;

    protected final StatsSummary RollOutScores;
    protected final StatsSummary RollOutMoves;

    public RobertSalmanNode(RobertSalmanNode Parent, int AgentID, Action MoveToState, double ExplorationFactor,
            Collection<Action> UnexpandedActions) {

        this.ParentNode = Parent;
        this.AgentID = AgentID;
        this.MoveToState = MoveToState;
        this.ExplorationFactor = ExplorationFactor;
        this.UnexpandedActions = new ArrayList<>(UnexpandedActions);
        this.Children = new ArrayList<>();
        this._Random = new Random();

        this.Score = 0;
        this.Visits = 0;

        this.RollOutScores = new BasicStats();
        this.RollOutMoves = new BasicStats();

    }

    public Action GetAction() {
        return MoveToState;
    }

    public void AddChildNodes(RobertSalmanNode Node) {
        UnexpandedActions.remove(Node.GetAction());
        Children.add(Node);
    }

    public double UTCValue() {
        if (ParentNode == null) {
            return 0;
        } else
            return (Score / Visits) + ExplorationFactor * (Math.sqrt((Math.log(ParentNode.Visits) / Visits)));
    }

    public List<RobertSalmanNode> GetChildren() {
        return Children;
    }
}
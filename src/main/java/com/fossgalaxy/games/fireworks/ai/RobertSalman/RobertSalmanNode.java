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

    // Remove the action from the list of actions left.
    // If an action isn't removed from this list FullyExpanded() will never be
    // emptied.
    public void AddChildNode(RobertSalmanNode Node) {
        UnexpandedActions.remove(Node.GetAction());
        Children.add(Node);
    }

    public List<RobertSalmanNode> GetChildren() {
        return Children;
    }

    public Action GetAction() {
        return MoveToState;
    }

    public void BackPropogation() {
    }

    // check to see if a node has been fully expanded
    boolean FullyExpanded(GameState state, int playerID) {
        if (UnexpandedActions.isEmpty()) {
            return true;// there are no actions left to expand, therefore node is fully expanded.
        }

        for (Action action : UnexpandedActions) {
            if (action.isLegal(playerID, state)) {
                return false;// there are still legal actions left to make, node is not fully expanded
            }
        }
        return true;// there are actions left, but they are not legal. Therefore the node is fully
                    // expanded.

    }

    public RobertSalmanNode GetBestNodeForPlay() {

        push to my branch

    }

    public RobertSalmanNode GetBestNodeForSelectionAndExpansion(RobertSalmanNode root) {
        private RobertSalmanNode bestChild = null;
        root.Children.forEach((temp) -> {
            double highScore = 0;
            if (bestChild == null) {
                bestChild = temp;
                highScore = bestChild.Score;
            } else {
                double childUCT = 10;
                if (childUCT > highScore) {
                    bestChild = temp;
                    highScore = bestChild.Score;
                }
            }

        });
    }

}

// this is a test line

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

    int visits = 0;
    double score = 0;
    int depth;

    RobertSalmanNode parent;
    List<RobertSalmanNode> children;
    double explorationFactor;

    int agentID;
    Collection<Action> unexpandedActions;
    Action moveToState;

    public RobertSalmanNode(int agentID, Collection<Action> unexpandedActions, double explorationFactor,
            RobertSalmanNode parent) {
        this.agentID = agentID;
        this.parent = parent;
        this.unexpandedActions = new ArrayList<>(unexpandedActions);
        this.children = new ArrayList<>();
        this.explorationFactor = explorationFactor;

    }

    // Remove the action from the list of actions left.
    // If an action isn't removed from this list FullyExpanded() will never be
    // emptied.
    public void AddChildNode(RobertSalmanNode Node) {
        unexpandedActions.remove(Node.GetAction());
        children.add(Node);
    }

    public List<RobertSalmanNode> GetChildren() {
        return children;
    }

    public boolean ContainsChild(Action action) {
        for (RobertSalmanNode child : children) {
            if (child.moveToState.equals(action)) {
                return true;
            }
        }
        return false;
    }

    public Action GetAction() {
        return moveToState;
    }

    public void BackPropogation() {
    }

    // check to see if a node has been fully expanded passing in the playerID and
    // the game state.
    boolean FullyExpanded(GameState state, int playerID) {
        if (unexpandedActions.isEmpty()) {
            return true;// there are no actions left to expand, therefore node is fully expanded.
        }

        for (Action action : unexpandedActions) {
            if (action.isLegal(playerID, state)) {
                return false;// there are still legal actions left to make, node is not fully expanded
            }
        }
        return true;// there are actions left, but they are not legal. Therefore the node is fully
                    // expanded.

    }

    // Check to see if a node has been fully expanded passing in only a gamestate
    // We then call the FullyExpanded mehtod passing in both state and an agentID.
    // The second parameter of the return gives the next player in order.
    // I.e. 0 -> 1 -> 2 -> 3 -> 4 -> 0
    boolean FullyExpanded(GameState state) {
        return FullyExpanded(state, (agentID + 1) % state.getPlayerCount());
    }

    public RobertSalmanNode GetBestNodeForPlay() {
        return null;

    }

    // Method to traverse the tree of known nodes using UCT in order to find the
    // next leaf node to explore
    public RobertSalmanNode UCTTraversal() {
        RobertSalmanNode bestChild = null;// stores the currently known best child node through loop iterations
        for (RobertSalmanNode temp : children) {
            double highScore = 0;

            // if first iteration of loop make the first child the bestChild
            if (bestChild == null) {
                bestChild = temp;
                highScore = temp.DoUCT();
            } else // compare each child's UCT score with the UCT score of the bestChild. If
                   // better, make it the new bestChild
            {
                double childUCT = temp.DoUCT();
                if (childUCT > highScore) {
                    bestChild = temp;
                    highScore = childUCT;
                }
            }
        }
        return bestChild;
    }

    private double DoUCT() {
        if (parent == null)
            return 0;
        return ((score / visits) + (Math.sqrt(explorationFactor * (Math.log(parent.visits) / visits))));
    }

    int GetDepth() {
        return depth;
    }

    int GetAgentID() {
        return agentID;
    }

    public Collection<Action> GetLegalMoves(GameState stete, int nextID) {
        return unexpandedActions;
    }

}

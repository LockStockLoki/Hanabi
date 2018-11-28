package com.fossgalaxy.games.fireworks.ai.RobertSalman;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.annotations.AgentConstructor;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.events.GameEvent;
import com.fossgalaxy.games.fireworks.ai.RobertSalman.RobertSalmanNode;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple agent that performs a random move.
 *
 * <b>IMPORTANT</b> You should rename this agent to your username
 */

public class RobertSalman implements Agent {
    private static final int defaultIterations = 50000;
    private static final int defaultRolloutDepth = 18;

    private static final int defaultTreeDepthMultiplier = 1;
    private int treeDepthMultiplier = 1;

    private Random random;

    int depth;

    public RobertSalman() {
        this(defaultIterations, defaultRolloutDepth, defaultTreeDepthMultiplier);
    }

    @AgentConstructor("RobertSalman")
    public RobertSalman(int RoundLength, int RolloutDepth, int treeDepthMultiplier) {
        this.treeDepthMultiplier = treeDepthMultiplier;

    }

    @Override
    public Action doMove(int playerID, GameState gameState) {
        return null;
    }

    protected RobertSalmanNode Select(RobertSalmanNode root, GameState state) {
        RobertSalmanNode currentNode = root;

        // as long as the game isn't over and we haven't
        // spent too many cyclyes looking at this tree, let's
        // select another node
        while (!state.isGameOver()) {
            RobertSalmanNode nextNode;

            // Current node has been expansed fully, now we need to expand another node as
            // we still have time.
            if (currentNode.FullyExpanded(state)) {
                nextNode = currentNode.UCTTraversal();
            }
            // Node is not fully expanded so we expand it until it is.
            else {
                nextNode = Expand(currentNode, state);
                return nextNode;
            }
            // We are a leaf node
            if (nextNode == null) {
                return currentNode;
            }

            currentNode = nextNode;

            Action action = currentNode.GetAction();
            if (action != null) {
                List<GameEvent> events = action.apply(currentNode.GetAgentID(), state);
                events.forEach(state::addEvent);
                state.tick();
            }
        }
        return currentNode;
    }

    protected Action SelectActionForExpand(GameState state, RobertSalmanNode node, int agentID) {
        Collection<Action> actions = node.GetLegalMoves(state, agentID);
    }

    protected RobertSalmanNode Expand(RobertSalmanNode parentNode, GameState gameState) {

        return null;
    }

    public int GetNextAgentID(GameState state, int agentID) {

    }

}
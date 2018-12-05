package com.fossgalaxy.games.fireworks.ai.RobertSalman;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;
import com.fossgalaxy.games.fireworks.annotations.AgentConstructor;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A version of the MCTS agent that replaces the random rollout with policy based roll-outs.
 *
 * This variant uses standard MCTS for all agent's moves in the tree, and then uses the policy for roll-outs.
 */
public class TMCTSPolicy extends TMCTS {
    private final Logger LOG = LoggerFactory.getLogger(TMCTSPolicy.class);
    private final Agent rolloutPolicy;

    public TMCTSPolicy(Agent rolloutPolicy) {
        this.rolloutPolicy = rolloutPolicy;
    }

    @AgentConstructor("tmctsPolicy")
    public TMCTSPolicy(int roundLength, int rolloutDepth, int treeDepthMul, Agent rollout) {
        super(roundLength, rolloutDepth, treeDepthMul);
        this.rolloutPolicy = rollout;
    }

    @AgentBuilderStatic("mctsPolicyND")
    public static TMCTSPolicy buildPolicyND(Agent rolloutPolicy) {
        return new TMCTSPolicy(TMCTS.DEFAULT_ITERATIONS, TMCTS.NO_LIMIT, TMCTS.NO_LIMIT, rolloutPolicy);
    }

    /**
     * Rather than perform a random move, query a policy for one.
     *
     * Consult the policy provided when creating the agent for all agent's moves.
     *
     * @param state the current game state
     * @param playerID the current player ID
     * @return the move that the policy has selected
     */
    @Override
    protected Action selectActionForRollout(GameState state, int playerID) {
        try {
            return rolloutPolicy.doMove(playerID, state);
        } catch (IllegalArgumentException ex) {
            LOG.error("warning, agent failed to make move: {}", ex);
            return super.selectActionForRollout(state, playerID);
        }
    }

    @Override
    public String toString() {
        return String.format("policyTMCTS(%s)", rolloutPolicy);
    }
}

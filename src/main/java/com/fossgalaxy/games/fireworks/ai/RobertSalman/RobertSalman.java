package com.fossgalaxy.games.fireworks.ai.RobertSalman;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.annotations.AgentConstructor;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;

///
import com.fossgalaxy.games.fireworks.ai.rule.*;
import com.fossgalaxy.games.fireworks.ai.rule.simple.*;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;
///

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
    private static final int DefaultIterations = 50000;
    private static final int DefaultRolloutDepth = 18;
    private static final int DefaultTreeDepthMultiplier = 1;

    private Random random;

    public RobertSalman() {
        this(DefaultIterations, DefaultRolloutDepth, DefaultTreeDepthMultiplier);
    }

    @AgentConstructor("RobertSalman")
    public RobertSalman(int RoundLength, int RolloutDepth, int DefaultTreeDepthMultiplier) {
        
    }

    @Override
    public Action doMove(int playerID, GameState gameState) {
        // TODO replace this with your agent

        // get all legal moves as a list
        List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(playerID, gameState));

        // choose a random item from that list and return it

        return null;
    }

}

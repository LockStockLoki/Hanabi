package com.fossgalaxy.games.fireworks.ai.RobertSalman;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.annotations.AgentConstructor;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple agent that performs a random move.
 *
 * <b>IMPORTANT</b> You should rename this agent to your username
 */

class Node {
    Node ParentNode;
    List<Node> ChildNodes;
}

class Tree {

}

public class RobertSalman implements Agent {
    private Random random;

    public RobertSalman() {
        this.random = new Random();
    }

    public Action doMove(int playerID, GameState gameState) {
        // TODO replace this with your agent

        int _PlayerCount = gameState.getPlayerCount();

        // get all legal moves as a list
        List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(playerID, gameState));

        // choose a random item from that list and return it
        int moveToMake = random.nextInt(possibleMoves.size());
        return possibleMoves.get(moveToMake);
    }

}

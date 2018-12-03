package com.fossgalaxy.games.fireworks.ai.RobertSalman;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;
import com.fossgalaxy.games.fireworks.annotations.AgentConstructor;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.Deck;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.events.GameEvent;
import com.fossgalaxy.games.fireworks.utils.DebugUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by WebPigeon on 09/08/2016.
 */
public class TMCTS implements Agent {
    public static final int DEFAULT_ITERATIONS = 50_000;
    public static final int DEFAULT_ROLLOUT_DEPTH = 18;
    public static final int DEFAULT_TREE_DEPTH_MUL = 1;
    public static final int NO_LIMIT = 100;

    protected final int roundLength;
    protected final int rolloutDepth;
    protected final int treeDepthMul;
    protected final Random random;
    protected final Logger logger = LoggerFactory.getLogger(TMCTS.class);

    private final boolean calcTree = false;

    /**
     * Create a default TMCTS implementation.
     * <p>
     * This creates an TMCTS agent that has a default roll-out length of 50_000 iterations, a depth of 18 and a tree
     * multiplier of 1.
     */
    public TMCTS() {
        this(DEFAULT_ITERATIONS, DEFAULT_ROLLOUT_DEPTH, DEFAULT_TREE_DEPTH_MUL);
    }

    public TMCTS(int roundLength) {
        this(roundLength, DEFAULT_ROLLOUT_DEPTH, DEFAULT_TREE_DEPTH_MUL);
    }

    //@AgentConstructor("mcts")
    public TMCTS(int roundLength, int rolloutDepth, int treeDepthMul) {
        this.roundLength = roundLength;
        this.rolloutDepth = rolloutDepth;
        this.treeDepthMul = treeDepthMul;
        this.random = new Random();
    }

    @Override
    public Action doMove(int agentID, GameState state) {
        long finishTime = System.currentTimeMillis() + 1000;
        TMCTSNode root = new TMCTSNode(
                (agentID + state.getPlayerCount() - 1) % state.getPlayerCount(),
                null,
                Utils.generateAllActions(agentID, state.getPlayerCount())
        );

        Map<Integer, List<Card>> possibleCards = DeckUtils.bindCard(agentID, state.getHand(agentID), state.getDeck().toList());
        List<Integer> bindOrder = DeckUtils.bindOrder(possibleCards);

        while(System.currentTimeMillis() < finishTime){
            //find a leaf node
            GameState currentState = state.getCopy();
            IterationObject iterationObject = new IterationObject(agentID);

            Map<Integer, Card> myHandCards = DeckUtils.bindCards(bindOrder, possibleCards);

            Deck deck = currentState.getDeck();
            Hand myHand = currentState.getHand(agentID);
            for (int slot = 0; slot < myHand.getSize(); slot++) {
                Card hand = myHandCards.get(slot);
                myHand.bindCard(slot, hand);
                deck.remove(hand);
            }
            deck.shuffle();

            TMCTSNode current = select(root, currentState, iterationObject);
            int score = rollout(currentState, agentID, current);
            current.backup(score);
            if(calcTree){
                System.err.println(root.printD3());
            }
        }

        if (logger.isInfoEnabled()) {
            for (TMCTSNode level1 : root.getChildren()) {
                logger.info("rollout {} moves: max: {}, min: {}, avg: {}, N: {} ", level1.getAction(), level1.rolloutMoves.getMax(), level1.rolloutMoves.getMin(), level1.rolloutMoves.getMean(), level1.rolloutMoves.getN());
                logger.info("rollout {} scores: max: {}, min: {}, avg: {}, N: {} ", level1.getAction(), level1.rolloutScores.getMax(), level1.rolloutScores.getMin(), level1.rolloutScores.getMean(), level1.rolloutScores.getN());
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("next player's moves considerations: ");
            for (TMCTSNode level1 : root.getChildren()) {
                logger.trace("{}'s children", level1.getAction());
                level1.printChildren();
            }
        }

        Action chosenOne = root.getBestNode().getAction();
        if (logger.isTraceEnabled()) {
            logger.trace("Move Chosen by {} was {}", agentID, chosenOne);
            root.printChildren();
        }
        return chosenOne;
    }

    protected TMCTSNode select(TMCTSNode root, GameState state, IterationObject iterationObject) {
        TMCTSNode current = root;
        int treeDepth = calculateTreeDepthLimit(state);
        while (!state.isGameOver() && current.getDepth() < treeDepth) {
            TMCTSNode next;
            if (current.fullyExpanded(state)) {
                next = current.getUCTNode(state);
            } else {
                next = expand(current, state);
                return next;
            }
            if (next == null) {
                //XXX if all follow on states explored so far are null, we are now a leaf node
                return current;
            }
            current = next;

            int agent = current.getAgent();
            int lives = state.getLives();
            int score = state.getScore();

            Action action = current.getAction();
            if (action != null) {
                List<GameEvent> events = action.apply(agent, state);
                events.forEach(state::addEvent);
                state.tick();
            }

            if (iterationObject.isMyGo(agent)) {
                if (state.getLives() < lives) {
                    iterationObject.incrementLivesLostMyGo();
                }
                if (state.getScore() > score) {
                    iterationObject.incrementPointsGainedMyGo();
                }
            }
        }
        return current;
    }

    protected int calculateTreeDepthLimit(GameState state){
        return (state.getPlayerCount() * treeDepthMul) + 1;
    }

    /**
     * Select a new action for the expansion node.
     *
     * @param state   the game state to travel from
     * @param agentID the AgentID to use for action selection
     * @param node    the Node to use for expansion
     * @return the next action to be added to the tree from this state.
     */
    protected Action selectActionForExpand(GameState state, TMCTSNode node, int agentID) {
        Collection<Action> legalActions = node.getLegalMoves(state, agentID);
        if (legalActions.isEmpty()) {
            return null;
        }

        Iterator<Action> actionItr = legalActions.iterator();

        int selected = random.nextInt(legalActions.size());
        Action curr = actionItr.next();
        for (int i = 0; i < selected; i++) {
            curr = actionItr.next();
        }

        return curr;
    }

    protected TMCTSNode expand(TMCTSNode parent, GameState state) {
        int nextAgentID = (parent.getAgent() + 1) % state.getPlayerCount();
        Action action = selectActionForExpand(state, parent, nextAgentID);
        // It is possible it wasn't allowed
        if (action == null) {
            return parent;
        }
        if (parent.containsChild(action)) {
            // return the correct node instead
            return parent.getChild(action);
        }
        //XXX we may expand a node which we already visited? :S
        TMCTSNode child = new TMCTSNode(parent, nextAgentID, action, Utils.generateAllActions(nextAgentID, state.getPlayerCount()));
        parent.addChild(child);
        return child;
    }

    protected Action selectActionForRollout(GameState state, int playerID) {
        Collection<Action> legalActions = Utils.generateActions(playerID, state);

        List<Action> listAction = new ArrayList<>(legalActions);
        Collections.shuffle(listAction);

        return listAction.get(0);
    }

    protected int rollout(GameState state, final int agentID, TMCTSNode current) {

        int playerID = agentID;
        int moves = 0;

        while (!state.isGameOver() && moves < rolloutDepth) {
            Action action = selectActionForRollout(state, playerID);
            List<GameEvent> events = action.apply(playerID, state);
            events.forEach(state::addEvent);
            state.tick();
            playerID = (playerID + 1) % state.getPlayerCount();
            moves++;
        }

        current.backupRollout(moves, state.getScore());
        return state.getScore();
    }

    @Override
    public String toString() {
        return "TMCTS";
    }

    private void printCard(Map.Entry<Integer, List<Card>> entry) {
        logger.trace("{} : {}", entry.getKey(), entry.getValue());
    }

}

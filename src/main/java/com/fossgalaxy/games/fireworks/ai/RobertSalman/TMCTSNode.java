package com.fossgalaxy.games.fireworks.ai.RobertSalman;

import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by webpigeon on 22/08/16.
 */
public class TMCTSNode {

    public static final double DEFAULT_EXP_CONST = Math.sqrt(2);

    private static final int MAX_SCORE = 25;
    private static final double EPSILON = 1e-6;
    private static final boolean DISCOUNT_ENABLED = false;

    private final double expConst;
    private final Action moveToState;
    private final int agentId;
    private final TMCTSNode parent;
    private final List<TMCTSNode> children;
    private final Collection<Action> allUnexpandedActions;
    private final Random random;
    private final int depth;
    private final Logger logger = LoggerFactory.getLogger(TMCTSNode.class);

    private double score;
    private int visits;
    private int parentWasVisitedAndIWasLegal;

    protected final StatsSummary rolloutScores;
    protected final StatsSummary rolloutMoves;

    public TMCTSNode(Collection<Action> allUnexpandedActions) {
        this(null, -1, null, DEFAULT_EXP_CONST, allUnexpandedActions);
    }

    public TMCTSNode(double expConst, Collection<Action> allUnexpandedActions) {
        this(null, -1, null, expConst, allUnexpandedActions);
    }

    public TMCTSNode(int agentID, Action moveToState, Collection<Action> allUnexpandedActions) {
        this(null, agentID, moveToState, DEFAULT_EXP_CONST, allUnexpandedActions);
    }

    public TMCTSNode(int agentID, Action moveToState, double expConst, Collection<Action> allUnexpandedActions) {
        this(null, agentID, moveToState, expConst, allUnexpandedActions);
    }

    public TMCTSNode(TMCTSNode parent, int agentId, Action moveToState, Collection<Action> allUnexpandedActions) {
        this(parent, agentId, moveToState, DEFAULT_EXP_CONST, allUnexpandedActions);
    }

    public TMCTSNode(TMCTSNode parent, int agentId, Action moveToState, double expConst, Collection<Action> allUnexpandedActions) {
        this.expConst = expConst;
        this.parent = parent;
        this.agentId = agentId;
        this.moveToState = moveToState;
        this.score = 0;
        this.visits = 0;
        this.children = new ArrayList<>();
        this.allUnexpandedActions = new ArrayList<>(allUnexpandedActions);
        this.random = new Random();
        this.depth = (parent == null) ? 0 : parent.depth + 1;

        this.rolloutScores = new BasicStats();
        this.rolloutMoves = new BasicStats();

        assert (parent != null && moveToState != null) || (parent == null && moveToState == null);
    }

    public void addChild(TMCTSNode node) {
        allUnexpandedActions.remove(node.getAction());
        children.add(node);
    }

    public double getUCTValue() {
        if (parent == null) {
            return 0;
        }

        return ((score / MAX_SCORE) / visits) + (expConst * Math.sqrt(Math.log(parentWasVisitedAndIWasLegal) / visits));
    }

    public List<TMCTSNode> getChildren() {
        return children;
    }

    public void backup(double score) {
        TMCTSNode current = this;
        while (current != null) {
            if (DISCOUNT_ENABLED) {
                current.score += score * Math.pow(0.95, current.getDepth()-1.0);
            } else {
                current.score += score;
            }
            current.visits++;
            current = current.parent;
        }
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public TMCTSNode getUCTNode(GameState state) {
        double bestScore = -Double.MAX_VALUE;
        TMCTSNode bestChild = null;

        for (TMCTSNode child : children) {
            //XXX Hack to check if the move is legal in this version
            Action moveToMake = child.moveToState;
            if (!moveToMake.isLegal(child.agentId, state)) {
                continue;
            }
            child.parentWasVisitedAndIWasLegal++;
            double childScore = child.getUCTValue() + (random.nextDouble() * EPSILON);

            if (childScore > bestScore) {
                bestScore = childScore;
                bestChild = child;
            }
        }

        return bestChild;
    }

    public int getAgent() {
        return agentId;
    }

    public Action getAction() {
        return moveToState;
    }

    public TMCTSNode getBestNode() {
        double bestScore = -Double.MAX_VALUE;
        TMCTSNode bestChild = null;

        for (TMCTSNode child : children) {
            double childScore = child.score / child.visits + (random.nextDouble() * EPSILON);
            if (childScore > bestScore) {
                bestScore = childScore;
                bestChild = child;
            }
        }

        assert bestChild != null;
        return bestChild;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return String.format("NODE(%d: %s %f)", getDepth(), moveToState, score);
    }

    public int getChildSize() {
        return children.size();
    }

    public boolean containsChild(Action moveToChild) {
        for (TMCTSNode child : children) {
            if (child.moveToState.equals(moveToChild)) {
                return true;
            }
        }
        return false;
    }

    public TMCTSNode getChild(Action action) {
        for (TMCTSNode child : children) {
            if (child.moveToState.equals(action)) {
                return child;
            }
        }
        return null;
    }

    public boolean fullyExpanded(GameState state) {
        return fullyExpanded(state, (agentId + 1) % state.getPlayerCount());
    }

    public boolean fullyExpanded(GameState state, int nextId) {
        if (allUnexpandedActions.isEmpty()) {
            return true;
        }

        for (Action action : allUnexpandedActions) {
            if (action.isLegal(nextId, state)) {
                return false;
            }
        }
        return true;
    }

    public Collection<Action> getLegalMoves(GameState state, int nextId) {
        return allUnexpandedActions.stream().filter(action -> action.isLegal(nextId, state)).collect(Collectors.toList());
    }

    public Collection<Action> getAllActionsExpandedAlready() {
        ArrayList<Action> actions = new ArrayList<>();
        children.forEach(node -> actions.add(node.getAction()));
        return actions;
    }

    public void printChildren() {
        logger.trace("\t {}\t {}\t {}\t {}", "action", "visits", "score", "avg");
        for (TMCTSNode child : children) {
            logger.trace("\t{}\t{}\t{}\t{}", child.getAction(), child.visits, child.score, child.score / child.visits);
        }
    }

    public String printD3(){
        StringBuilder buffer = new StringBuilder();
        printD3Internal(buffer);
        return buffer.toString();
    }

    private void printD3Internal(StringBuilder buffer){
        buffer.append("{\"name\": \"\"");
        if(!children.isEmpty()){
            buffer.append(",\"children\":[");
            for (int i = 0; i < children.size(); i++){
                if(i != 0){
                    buffer.append(",");
                }
                children.get(i).printD3Internal(buffer);
            }
            buffer.append("]");
        }
        buffer.append("}");
    }

    /**
     * Keep track of stats for rollouts.
     *
     * @param moves The number of moves made for a given rollout
     * @param score The total score achived at the end of the rollout
     */
    public void backupRollout(int moves, int score) {
        rolloutMoves.add(moves);
        rolloutScores.add(score);
        if (parent != null) {
            parent.backupRollout(moves, score);
        }
    }
}

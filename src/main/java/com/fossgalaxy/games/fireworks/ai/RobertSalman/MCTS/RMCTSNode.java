package com.fossgalaxy.games.fireworks.ai.RobertSalman.MCTS;

import com.fossgalaxy.games.fireworks.state.BasicState;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RMCTSNode {
    private static final int maxScore = 25;
    private static final double Epsilon = 1e-6;
    private double explorationFactor;
    private Action transitionAction;
    private int agentID;
    private RMCTSNode parentNode;
    private List<RMCTSNode> children;
    private Collection<Action> allUnexpandedActions;
    private Random random;

    private double score;
    private int visits;

    StatsSummary simulateScores;
    StatsSummary simulateMoves;

    public static final double defaultExplorationFactor = Math.sqrt(2);

    public RMCTSNode(RMCTSNode parentNode, int agentID, Action transitionAction, Collection<Action> allUnexpandedActions)
    {
        this(parentNode, agentID, transitionAction, allUnexpandedActions, defaultExplorationFactor);
    }

    public RMCTSNode(RMCTSNode parentNode, int agentID, Action transitionAction, Collection<Action> allUnexpandedActions, double exploractionFactor)
    {
        this.parentNode = parentNode;
        this.agentID = agentID;
        this.transitionAction = transitionAction;
        this.allUnexpandedActions = new ArrayList<>(allUnexpandedActions);
        this.children = new ArrayList<>();
        this.explorationFactor = exploractionFactor;
        this.score = 0;
        this.visits = 0;

        simulateScores = new BasicStats();
        simulateMoves = new BasicStats();
    }

    public boolean FullyExpanded(GameState gameState)
    {
        //TODO: Implement functionality for FullyExpanded()
        return false;
    }
    
    public RMCTSNode GetNextNode(GameState gameState)
    {
        //TODO: Implement functionality for GetNextNode()
        return null;
    }

    public int GetAgentID()
    {
        return agentID;
    }

    public Action GetAction()
    {
        return transitionAction;
    }

    public RMCTSNode GetChild(Action action)
    {
        for(RMCTSNode child : children)
        {
            if(child.transitionAction.equals(action))
            {
                return child;
            }
        }
        
        return null;
    }

    public Collection<Action>GetLegalActions(GameState gameState, int nextID)
    {
        return allUnexpandedActions.stream().filter(action -> action.isLegal(nextID, gameState)).collect(Collectors.toList());
    }

    public void AddChild(RMCTSNode node)
    {
        allUnexpandedActions.remove(node.GetAction());
        children.add(node);
    }

    public boolean ContainsChild(Action action)
    {
        for(RMCTSNode child : children)
        {
            if(child.transitionAction.equals(action))
            {
                return true;
            }
        }
        
        return false;
    }

    public void ReverseSimulation(int move, int score)
    {

    }
}
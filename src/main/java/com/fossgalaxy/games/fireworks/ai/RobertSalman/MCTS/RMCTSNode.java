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
        int nextAgentID = RMCTS.NextAgentID(agentID, gameState.getPlayerCount());
        if(allUnexpandedActions.isEmpty())
        {
            return true;
        }

        for(Action action : allUnexpandedActions)
        {
            if(action.isLegal(nextAgentID, gameState))
            {
                return false;
            }
        }

        return true;//even if there are actions left they are not legal moves.
    }
    
    public RMCTSNode GetNodeForPlay()
    {
        double bestScore = -Double.MAX_VALUE;
        RMCTSNode bestChild = null;

        for(RMCTSNode child : children)
        {
            double childScore = child.score / child.visits + (random.nextDouble() * Epsilon);
            if(childScore > bestScore)
            {
                bestScore = childScore;
                bestChild = child;
            }
        }

        return bestChild;
    }

    int legal = 0;
    public RMCTSNode GetNextNode(GameState gameState)
    {
        double bestScore = -Double.MAX_VALUE;
        RMCTSNode bestChild = null;

        for(RMCTSNode child : children)
        {
            Action move = child.transitionAction;
            if(!move.isLegal(child.agentID, gameState))
            {
                continue;
            }
            
            child.legal++;
            
            double childScore = GetUCBTValue();

            if(childScore > bestScore)
            {
                bestScore = childScore;
                bestChild = child;
            }
        }

        return bestChild;
    }

    public double GetUCBTValue()
    {
        if(parentNode == null)
        {
            return 0;
        }

        return ((score/ maxScore) / visits) + (explorationFactor * Math.sqrt(Math.log(legal / visits)));
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

    public void Reverse(double score)
    {
        RMCTSNode currentNode = this;
        while(currentNode != null)
        {
            currentNode.score += score;
            currentNode.visits++;

            currentNode = currentNode.parentNode;
        }
    }

    public void ReverseSimulation(int move, int score)
    {
        simulateMoves.add(move);
        simulateScores.add(score);

        if(parentNode != null)
        {
            parentNode.ReverseSimulation(move, score);
        }
    }
}
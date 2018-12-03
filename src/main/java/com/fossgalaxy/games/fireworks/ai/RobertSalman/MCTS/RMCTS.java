package com.fossgalaxy.games.fireworks.ai.RobertSalman.MCTS;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.Deck;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.events.GameEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;

public class RMCTS implements Agent {

    private final long defaultRuntime = 1000;
    Random random;
    int roundLength;

    public RMCTS(int roundLength) 
    {
        this.roundLength = roundLength;
        random = new Random();
    }

    @Override
    public Action doMove(int playerID, GameState gameState) 
    {
        long time = System.currentTimeMillis() + 950;//we have a second to do our move, but we don't want to get disqualified.

        RMCTSNode rootNode = new RMCTSNode(null, (playerID + gameState.getPlayerCount() - 1) % gameState.getPlayerCount(), null, Utils.generateAllActions(playerID, gameState.getPlayerCount()));

        Map<Integer, List<Card>> possibleCards = DeckUtils.bindCard(playerID, gameState.getHand(playerID), gameState.getDeck().toList());
        List<Integer> bindOrder = DeckUtils.bindOrder(possibleCards);

        while(System.currentTimeMillis() < time)
        {
            GameState currentState = gameState.getCopy()
        }


        return null;
    }

    protected RMCTSNode Select(RMCTSNode rootNode, GameState gameState)
    {
        RMCTSNode currentNode = rootNode;
        
        while(!gameState.isGameOver())
        {
            RMCTSNode nextNode;

            if(currentNode.FullyExpanded(gameState))
            {
                //Current node is fully expanded, now we need to get the next node.
                nextNode = currentNode.GetNextNode(gameState);  
            }
            else
            {
                //Current node is not fully expanded, so we can expand this one.
                nextNode = Expand(currentNode, gameState);
            }
            if(nextNode == null)
            {
                //We are at a leaf node.
                return currentNode;
            }

            currentNode = nextNode;

            int agentID = currentNode.GetAgentID();

            Action action = currentNode.GetAction();
            if(action != null)
            {
                List<GameEvent> events = action.apply(agentID, gameState);
                events.forEach(gameState::addEvent);
                gameState.tick();
            }    
        }
        return currentNode;
    }

    protected RMCTSNode Expand(RMCTSNode parentNode, GameState gameState)
    {
        int nextAgentID = NextAgentID(parentNode.GetAgentID(), gameState.getPlayerCount());

        Action action = SelectActionForExpand(gameState, parentNode, nextAgentID);
        if(action == null)
        {
            return parentNode;
        }
        
        if(parentNode.ContainsChild(action))
        {
            return parentNode.GetChild(action);
        }

        RMCTSNode child = new RMCTSNode(parentNode, nextAgentID, action, Utils.generateAllActions(nextAgentID, gameState.getPlayerCount()));
        parentNode.AddChild(child);
        
        return child;
    }

    protected int Simulate(GameState gameState, final int agentID, RMCTSNode currentNode)
    {
        int playerID;
        int moves = 0;

        while(!gameState.isGameOver())
        {
            Action action = SelectActionForSimulate(gameState, agentID);
            List<GameEvent> event = action.apply(agentID, gameState);
            gameState.tick();
            playerID = NextAgentID(agentID, gameState.getPlayerCount());
            moves++;
        }

        currentNode.ReverseSimulation(moves, gameState.getScore());
        return gameState.getScore();
    }

    protected Action SelectActionForExpand(GameState gameState, RMCTSNode node, int nextAgentID)
    {
        Collection<Action> legalActions = node.GetLegalActions(gameState, nextAgentID);
        if(legalActions.isEmpty())
        {
            return null;
        }
        
        Iterator<Action> actionIterator = legalActions.iterator();

        int selected = random.nextInt(legalActions.size());
        Action action = actionIterator.next();
        for(int i = 0; i < selected; i++)
        {
            action = actionIterator.next();
        }

        return action;
    }

    protected Action SelectActionForSimulate(GameState gameState, int agentID)
    {
        Collection<Action> legalActions = Utils.generateActions(agentID, gameState);
        List<Action> actionList = new ArrayList<>(legalActions);
        Collections.shuffle(actionList);
        return actionList.get(0);
    }

    static public int NextAgentID(int agentID, int playerCount)
    {
        return (agentID + 1) % playerCount;
    }
}s
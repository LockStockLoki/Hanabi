package com.fossgalaxy.games.fireworks.ai.RobertSalman;

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

public class RobertSalman implements Agent {

    private final long defaultRuntime = 950;
    Random random;
    int roundLength;

    public RobertSalman()
    {
        random = new Random();
    }

    @Override
    public Action doMove(int agentID, GameState gameState) 
    {
        System.out.println("I am agent #"+ agentID + ".");
        
        for(int i = 0; i < gameState.getPlayerCount(); i++)
        {  
            Hand hand = gameState.getHand(i);
            System.out.print("AgentID is " + i + " ");
            System.out.println(hand.toString());
        }
        
        
        long time = System.currentTimeMillis() + defaultRuntime;//we have a second to do our move, but we don't want to get disqualified.

        RobertSalmanNode rootNode = new RobertSalmanNode(null, (agentID + gameState.getPlayerCount() - 1) % gameState.getPlayerCount(), null, Utils.generateAllActions(agentID, gameState.getPlayerCount()));

        Map<Integer, List<Card>> possibleCards = DeckUtils.bindCard(agentID, gameState.getHand(agentID), gameState.getDeck().toList());
        List<Integer> bindOrder = DeckUtils.bindOrder(possibleCards);

        while(System.currentTimeMillis() < time)
        {
            GameState currentState = gameState.getCopy();
            
            Map<Integer, Card> myHand = DeckUtils.bindCards(bindOrder, possibleCards);

            Deck deck = currentState.getDeck();
            Hand hand = currentState.getHand(agentID);
            
            for(int i = 0; i < myHand.size(); i++)
            {
                Card card = myHand.get(i);
                hand.bindCard(i, card);
                deck.remove(card);
            }
            deck.shuffle();

            RobertSalmanNode currentNode = Select(rootNode, currentState);
            int score = Simulate(currentState, agentID, currentNode);
            currentNode.Reverse(score);
        }

        Action action = rootNode.GetNodeForPlay().GetAction();
        
        return action;
    }

    protected RobertSalmanNode Select(RobertSalmanNode rootNode, GameState gameState)
    {
        RobertSalmanNode currentNode = rootNode;
        
        while(!gameState.isGameOver())
        {
            RobertSalmanNode nextNode;

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

    protected RobertSalmanNode Expand(RobertSalmanNode parentNode, GameState gameState)
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

        RobertSalmanNode child = new RobertSalmanNode(parentNode, nextAgentID, action, Utils.generateAllActions(nextAgentID, gameState.getPlayerCount()));
        parentNode.AddChild(child);
        
        return child;
    }

    protected int Simulate(GameState gameState, final int agentID, RobertSalmanNode currentNode)
    {
        int playerID = agentID;
        int moves = 0;

        while(!gameState.isGameOver())
        {
            Action action = SelectActionForSimulate(gameState, playerID);
            ///////////////////////////////////////////////////////////
            List<GameEvent> event = action.apply(playerID, gameState);// we were dumb. It wasn't this.
            /////////////////////////////////////////////////////////// 
            event.forEach(gameState::addEvent);
            gameState.tick();
            playerID = NextAgentID(agentID, gameState.getPlayerCount());
            moves++;
        }

        currentNode.ReverseSimulation(moves, gameState.getScore());
        return gameState.getScore();
    }

    protected Action SelectActionForExpand(GameState gameState, RobertSalmanNode node, int nextAgentID)
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

        List<Action> legalActionList = new ArrayList<>(legalActions);
        for(Action action : legalActionList)
        {
            if(!action.isLegal(agentID, gameState))
            {
                actionList.remove(action);
            }
        }

        Collections.shuffle(actionList);
        
        return legalActionList.get(0);
    }

    static public int NextAgentID(int agentID, int playerCount)
    {
        return (agentID + 1) % playerCount;
    }
}
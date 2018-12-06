package com.fossgalaxy.games.fireworks.ai.RobertSalman;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;
import com.fossgalaxy.games.fireworks.annotations.AgentConstructor;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.google.common.util.concurrent.Service.State;
import com.fossgalaxy.games.fireworks.ai.rule.*;
import com.fossgalaxy.games.fireworks.ai.rule.simple.*;
import com.fossgalaxy.games.fireworks.ai.osawa.rules.*;
import com.fossgalaxy.games.fireworks.ai.rule.random.*;
import com.fossgalaxy.games.fireworks.ai.rule.wrapper.IfRule;



public class RobertSalman extends RobertSalmanMCTS
{
    private final Agent simulationPolicy;

    public RobertSalman()
    {
        simulationPolicy = ruleAgent();
    }   

    public static Agent ruleAgent() {
        ProductionRuleAgent pra = new ProductionRuleAgent();
        
        //pra.addRule(new rule TellPlayableCard());
        
        pra.addRule(
                new IfRule(
                        (id, state) -> state.getLives() > 1 && !state.getDeck().hasCardsLeft(),
                        new PlayProbablySafeCard(0.0)
                )
        );
        pra.addRule(new PlaySafeCard());
        pra.addRule(
                new IfRule(
                        (id, state) -> state.getLives() > 1,
                        new PlayProbablySafeCard(.6)
                )
        );
        pra.addRule(new TellAnyoneAboutUsefulCard());
        pra.addRule(
                new IfRule(
                        (id, state) -> state.getInfomation() < 4,
                        new TellDispensable()
                )
        );
        pra.addRule(new OsawaDiscard());
        pra.addRule(new DiscardOldestFirst());
        pra.addRule(new TellRandomly());
        pra.addRule(new DiscardRandomly());
        return pra;

    }


    @Override
    protected Action SelectActionForSimulate(GameState gameState, int playerID)
    {
        try
        {
            return simulationPolicy.doMove(playerID, gameState);
        }
        catch(IllegalArgumentException ex)
        {
            System.out.println("Error: " + ex.getMessage() + ". Using default simulation rollout");
            return super.SelectActionForSimulate(gameState, playerID);
        }
    }

    @Override
    public String toString()
    {
        return String.format("policyRobertSalmanPolicy(%s)", simulationPolicy);
    }
}

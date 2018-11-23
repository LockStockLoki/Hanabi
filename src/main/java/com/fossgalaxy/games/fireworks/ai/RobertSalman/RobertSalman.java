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

public class RobertSalman {

    @AgentBuilderStatic("SampleRuleBased")
    public static Agent buildRuleBased() {
        ProductionRuleAgent pra = new ProductionRuleAgent();

        // you can add rules to your agent here
        pra.addRule(new PlayIfCertain());
        pra.addRule(new TellAnyoneAboutUsefulCard());
        pra.addRule(new DiscardIfCertain());
        pra.addRule(new DiscardOldestFirst());

        return pra;
    }

}

package com.fossgalaxy.games.fireworks.ai.RobertSalman;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.mcts.MCTSPredictor;
import com.fossgalaxy.games.fireworks.ai.osawa.rules.OsawaDiscard;
import com.fossgalaxy.games.fireworks.ai.osawa.rules.TellPlayableCardOuter;
import com.fossgalaxy.games.fireworks.ai.rule.*;
import com.fossgalaxy.games.fireworks.ai.rule.random.DiscardRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.random.PlayProbablySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.random.TellRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.simple.DiscardIfCertain;
import com.fossgalaxy.games.fireworks.ai.rule.simple.PlayIfCertain;
import com.fossgalaxy.games.fireworks.ai.rule.wrapper.IfRule;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

public class flawed
{
    @AgentBuilderStatic("flawed")
    public static Agent buildFlawedPlayer(){
        ProductionRuleAgent pra = new ProductionRuleAgent();
        pra.addRule(new PlaySafeCard());
        pra.addRule(new PlayProbablySafeCard(0.25));

        pra.addRule(new TellRandomly());

        pra.addRule(new OsawaDiscard());
        pra.addRule(new DiscardOldestFirst());
        pra.addRule(new DiscardRandomly());

        return pra;
    }



}
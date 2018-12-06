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

public class iggi2
{
    @AgentBuilderStatic("iggi2")
    public static Agent buildIGGI2Player() {
        ProductionRuleAgent pra = new ProductionRuleAgent();

        // Its yolo time
        pra.addRule(
                new IfRule(
                        (id, state) -> state.getLives() > 1 && !state.getDeck().hasCardsLeft(),
                        new PlayProbablySafeCard(0.0)
                )
        );

        pra.addRule(new PlayIfCertain());
        pra.addRule(new PlaySafeCard());

        pra.addRule(
                new IfRule(
                        (id, state) -> state.getLives() > 1,
                        new PlayProbablySafeCard(.6)
                )
        );


        pra.addRule(new OsawaDiscard());

        pra.addRule(new TellAnyoneAboutOldestUsefulCard());
        pra.addRule(new CompleteTellUsefulCard());
        //pra.addRule(new TellFives());

        pra.addRule(new DiscardOldestNoInfoFirst());
        pra.addRule(new DiscardOldestFirst());

        pra.addRule(new TellMostInformation(true));
        pra.addRule(new TellRandomly());

        return pra;
    }


}
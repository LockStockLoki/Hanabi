package com.fossgalaxy.games.fireworks.ai;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.RobertSalman.RobertSalman;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

import java.util.Random;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

/**
 * Game runner for testing.
 *
 * This will run a bunch of games with your agent so you can see how it does.
 */
public class App {
    public static void main(String[] args) throws IOException {
        int numPlayers = 5;
        int numGames = 2;
        String agentName = "RobertSalman";

        Random random = new Random();
        StatsSummary statsSummary = new BasicStats();
        int currentGame;

        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
        File file = new File("./Data/" + dateFormat.format(date) + ".txt") ;
        LocalTime time = java.time.LocalTime.now();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));

        String filePath = "./Data/"+agentName+"Data.txt";
        FileWriter dataFile = new FileWriter(filePath, true);

        for (int i = 0; i < numGames; i++) {
            GameRunner runner = new GameRunner("test-game", numPlayers);
            currentGame = i;
            System.out.println("Game #" + currentGame);
            // add your agents to the game
            for (int j = 0; j < numPlayers; j++) {
                // the player class keeps track of our state for us...
                Player player = new AgentPlayer(agentName, AgentUtils.buildAgent(agentName));
                runner.addPlayer(player);
            }

            GameStats stats = runner.playGame(random.nextLong());
            statsSummary.add(stats.score);
        }

        // print out the stats
        System.out.println(String.format("Our agent: Avg: %f, min: %f, max: %f", statsSummary.getMean(), statsSummary.getMin(), statsSummary.getMax()));
        
        out.write("Agent is: "+agentName+ ".");
        out.write(System.lineSeparator());
        if(RobertSalman.iterationsOrTime)
        {
            String string = "This agent was run with an iteration based loop of " + RobertSalman.iteration + " iterations.";
            System.out.print(string);
            out.write(string);
        }
        if(!RobertSalman.iterationsOrTime)
        {
            String string = "This agent was run with a time based loop of " + RobertSalman.defaultRuntime + " milliseconds.";
            System.out.print(string);
            out.write(string);
            
        }
        out.newLine();
        out.write("Number of players: " + numPlayers);
        out.newLine();
        out.write("Number of games played: " + numGames);
        out.newLine();
        out.write(String.format("Our agent: Avg: %f, min: %f, max: %f", statsSummary.getMean(), statsSummary.getMin(), statsSummary.getMax()));
        out.close();

        String dataAppend = "Game: " + time + " score: " + statsSummary.getMean();
        dataFile.append(dataAppend);
        dataFile.append(System.lineSeparator());
        dataFile.close();
    }
}

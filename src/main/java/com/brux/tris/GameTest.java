package com.brux.tris;

import com.brux.tris.ai.IntermediateBot;
import com.brux.tris.model.*;
import com.brux.tris.service.ScoreKeeper;

public class GameTest {
    public static void main(String[] args) {
        ScoreKeeper scoreKeeper = new ScoreKeeper();
        Player botX = new IntermediateBot(Symbol.X);
        Player botO = new IntermediateBot(Symbol.O);

        Game game = new Game(botX, botO, scoreKeeper, Mode.SINGLE_PLAYER);

        Player starter = botX; // primo round parte X

        for (int round = 1; round <= 5; round++) {
            System.out.println("=== Round " + round + " ===");
            game.startNewRound(starter);

            BoardStatus status;
            do {
                status = game.playTurn();
                printBoard(game.getBoard());
            } while (status == BoardStatus.ONGOING);

            System.out.println("Risultato: " + status);
            System.out.println("Score attuale: X=" + scoreKeeper.getScore(Symbol.X)
                    + " | O=" + scoreKeeper.getScore(Symbol.O));
            System.out.println();

            // calcola chi parte il prossimo round
            starter = game.getNextStarter(status);
        }

        System.out.println("=== Fine test ===");
        System.out.println("Punteggio finale: X=" + scoreKeeper.getScore(Symbol.X)
                + " | O=" + scoreKeeper.getScore(Symbol.O));
    }

    private static void printBoard(Board board) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Symbol s = board.getCell(r, c);
                char ch = (s == Symbol.EMPTY) ? '-' : s.name().charAt(0);
                System.out.print(ch + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}


package com.brux.tris;

import com.brux.tris.ai.IntermediateBot;
import com.brux.tris.model.*;
import com.brux.tris.service.ScoreKeeper;

import java.util.Scanner;

public class GameHumanTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ScoreKeeper scoreKeeper = new ScoreKeeper();

        // Decidi il tuo simbolo
        System.out.print("Vuoi giocare come X o O? ");
        String choice = scanner.nextLine().trim().toUpperCase();
        Symbol humanSymbol = choice.equals("O") ? Symbol.O : Symbol.X;
        Symbol botSymbol = (humanSymbol == Symbol.X) ? Symbol.O : Symbol.X;

        Player human = new HumanPlayer(humanSymbol);
        Player bot = new IntermediateBot(botSymbol);

        Game game = new Game(humanSymbol == Symbol.X ? human : bot,
                humanSymbol == Symbol.X ? bot : human,
                scoreKeeper, Mode.SINGLE_PLAYER);

        Player starter = (humanSymbol == Symbol.X) ? human : bot; // parte X di default

        // Gioca una partita singola
        game.startNewRound(starter);

        BoardStatus status;
        do {
            Player current = game.getCurrentPlayer();

            if (current instanceof HumanPlayer) {
                // Tocca all'umano: chiedi input
                printBoard(game.getBoard());
                System.out.print("Inserisci mossa (riga colonna): ");
                int row = scanner.nextInt();
                int col = scanner.nextInt();

                Move move = new Move(row, col, humanSymbol);
                ((HumanPlayer) current).setPendingMove(move);
            }

            status = game.playTurn();

        } while (status == BoardStatus.ONGOING);

        // Stampa risultato finale
        printBoard(game.getBoard());
        System.out.println("Risultato: " + status);
        System.out.println("Score: X=" + scoreKeeper.getScore(Symbol.X)
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


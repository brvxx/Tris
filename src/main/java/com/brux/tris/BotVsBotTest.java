package com.brux.tris;

import com.brux.tris.ai.IntermediateBot;
import com.brux.tris.model.*;

public class BotVsBotTest {
    public static void main(String[] args) {
        Board board = new Board();
        Player botX = new IntermediateBot(Symbol.X);
        Player botO = new IntermediateBot(Symbol.O);

        Player current = botX;
        GameResult result;

        System.out.println("Inizio partita BOT vs BOT\n");

        // Mossa strane iniziali
        /*board.applyMove(new Move(1, 0, Symbol.O));
        board.applyMove(new Move(0, 2, Symbol.X));
        board.applyMove(new Move(2, 1, Symbol.O));
        printBoard(board);*/

        while (true) {
            // Il player fa la sua mossa
            Move move = current.makeMove(board);
            if (move == null) {
                System.out.println("Errore: nessuna mossa disponibile!");
                break;
            }

            board.applyMove(move);

            System.out.println("Mossa di " + current.getSymbol() + ": " + move);
            printBoard(board);

            // Controlla stato partita
            result = board.checkWinner();
            if (result != GameResult.ONGOING) {
                System.out.println("\nPartita terminata: " + result);
                break;
            }

            // Cambio giocatore
            current = (current == botX) ? botO : botX;
        }
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


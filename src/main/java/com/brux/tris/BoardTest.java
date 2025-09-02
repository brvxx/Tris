package com.brux.tris;

import com.brux.tris.model.*;

import java.util.List;

public class BoardTest {
    public static void main(String[] args) {
        Board board = new Board();

        // Stato iniziale
        System.out.println("Board vuota, risultato atteso: ONGOING");
        System.out.println("Risultato: " + board.getStatus());
        printBoard(board);

        // Giocatore X mette al centro
        board.applyMove(new Move(1, 1, Symbol.X));
        System.out.println("\nDopo mossa X al centro:");
        printBoard(board);

        // Giocatore O mette in alto a sinistra
        board.applyMove(new Move(0, 0, Symbol.O));
        System.out.println("\nDopo mossa O in alto a sinistra:");
        printBoard(board);

        // Giocatore X completa la diagonale
        board.applyMove(new Move(0, 2, Symbol.X));
        board.applyMove(new Move(2, 0, Symbol.O));
        board.applyMove(new Move(2, 2, Symbol.X));
        board.applyMove(new Move(1, 0, Symbol.O));

        System.out.println("\nDopo altre mosse:");
        printBoard(board);
        System.out.println("Risultato atteso: WIN_O");
        System.out.println("Risultato: " + board.getStatus());

        // Mosse disponibili per O
        List<Move> availableMoves = board.getAvailableMoves(Symbol.O);
        System.out.println("\nMosse disponibili per O:");
        for (Move move : availableMoves) {
            System.out.println(move);
        }
    }

    // Funzione di supporto per stampare la board
    private static void printBoard(Board board) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Symbol s = board.getCell(r, c);
                char ch = (s == Symbol.EMPTY) ? '-' : s.name().charAt(0);
                System.out.print(ch + " ");
            }
            System.out.println();
        }
    }
}

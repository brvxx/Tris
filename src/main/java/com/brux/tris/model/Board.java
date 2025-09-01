package com.brux.tris.model;

import java.util.ArrayList;
import java.util.List;

/* Griglia 3x3 relativa ai singoli round */
public class Board {

    /* Lo stato della board viene memorizzato attraverso un array bidimensionale (matrice) di Symbol */
    private final int SIZE = 3;
    private final Symbol[][] grid;

    public Board() {
        this.grid = new Symbol[SIZE][SIZE];
        reset();
    }

    /* Metodo che setta tutti gli elementi della board a EMPTY (inizio partita) */
    public void reset() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                grid[r][c] = Symbol.EMPTY;
            }
        }
    }

    /* Metodo che ritorna il simbolo contenuto nella cella cercata */
    public Symbol getCell(int row, int col) {
        return grid[row][col];
    }

    /* Predicato che verifica se una delle celle della griglia sia utilizzabile da parte di un giocatore per una mossa */
    public boolean isCellEmpty(int row, int col) {
        return grid[row][col] == Symbol.EMPTY;
    }

    /* Metodo che inserisce la mossa dentro alla griglia e ritorna true se valida, altrimenti, se nella posizione contenuta
    * nella Move in corrispondenza della griglia è già presente un simbolo ovviamente non si fa nulla e si ritorna false */
    public boolean applyMove(Move move) {
        int row = move.getRow();
        int col = move.getCol();

        if (grid[row][col] == Symbol.EMPTY) {
            grid[row][col] = move.getSymbol();
            return true;
        }
        return false;
    }

    /* Metodo che controlla lo stato attuale della partita, dunque se qualcuno ha vinto o c'è un pareggio, o se la partita sta ancora andando */
    public GameResult checkWinner() {

        /* Controllo che non ci sia una condizione di vittoria sulle righe da parte di uno dei simboli */
        for (int r = 0; r < SIZE; r++) {
            if (grid[r][0] != Symbol.EMPTY &&
                    grid[r][0] == grid[r][1] &&
                    grid[r][1] == grid[r][2]) {
                return (grid[r][0] == Symbol.X) ? GameResult.WIN_X : GameResult.WIN_O;
            }
        }

        /* Controllo sulle colonne */
        for (int c = 0; c < SIZE; c++) {
            if (grid[0][c] != Symbol.EMPTY &&
                    grid[0][c] == grid[1][c] &&
                    grid[1][c] == grid[2][c]) {
                return (grid[0][c] == Symbol.X) ? GameResult.WIN_X : GameResult.WIN_O;
            }
        }

        /* Controllo sulle diagonali */
        if (grid[0][0] != Symbol.EMPTY &&
                grid[0][0] == grid[1][1] &&
                grid[1][1] == grid[2][2]) {
            return (grid[0][0] == Symbol.X) ? GameResult.WIN_X : GameResult.WIN_O;
        }

        if (grid[0][2] != Symbol.EMPTY &&
                grid[0][2] == grid[1][1] &&
                grid[1][1] == grid[2][0]) {
            return (grid[0][2] == Symbol.X) ? GameResult.WIN_X : GameResult.WIN_O;
        }

        /* Controllo pareggio */
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (grid[r][c] == Symbol.EMPTY) {
                    return GameResult.ONGOING;
                }
            }
        }

        /* Se non si è usciti ancora, necessariamente ci si trova in una condizione di pareggio, dunque nella quale TUTTE le celle della griglia
        *   sono occupate e non EMPTY */
        return GameResult.DRAW;
    }

    /* Metodo che ritorna una lista di mosse disponibili a seconda dello stato interno della Board. Al metodo viene passato il Symbol associato
    * al giocatore che lo chiama, così che la lista di mosse tornata, sia relativa a tale giocatore */
    public List<Move> getAvailableMoves(Symbol symbol) {
        List<Move> moves = new ArrayList<>();

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (isCellEmpty(r, c)) {
                    moves.add(new Move(r, c, symbol));
                }
            }
        }

        return moves;
    }
}

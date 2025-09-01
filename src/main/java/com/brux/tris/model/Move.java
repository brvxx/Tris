package com.brux.tris.model;

/* Singole mosse effettuate da parte dei giocatori */
public class Move {

    /* Dal momento che concettualmente una volta che una mossa è fatta, questa è anche immutabile, i campi vengono posti final */
    private final int row;
    private final int col;
    private final Symbol symbol;

    public Move(int row, int col, Symbol symbol) {
        this.row = row;
        this.col = col;
        this.symbol = symbol;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "Move{" +
                "row=" + row +
                ", col=" + col +
                ", symbol=" + symbol +
                '}';
    }
}

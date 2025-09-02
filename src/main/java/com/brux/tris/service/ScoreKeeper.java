package com.brux.tris.service;

import com.brux.tris.model.Symbol;

/* Punteggio della partita */
public class ScoreKeeper {
    private int scoreX;     // score del giocatore con X
    private int scoreO;     // score del giocatore con O

    public ScoreKeeper() {
        reset();
    }

    private void reset() {
        scoreX = 0;
        scoreO = 0;
    }

    public int getScore(Symbol symbol) {
        return (symbol == Symbol.X) ? scoreX : scoreO;
    }

    public void increment(Symbol symbol) {
        if (symbol == Symbol.X) {
            scoreX++;
        } else if (symbol == Symbol.O) {
            scoreO++;
        }
    }

    @Override
    public String toString() {
        return "ScoreKeeper{" +
                "X=" + scoreX +
                ", O=" + scoreO +
                '}';
    }
}

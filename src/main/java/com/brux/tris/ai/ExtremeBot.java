package com.brux.tris.ai;

import com.brux.tris.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class ExtremeBot extends BotPlayer {

    private final RandomGenerator rnd = RandomGenerator.getDefault();

    public ExtremeBot(Symbol symbol) {
        super(symbol);
    }

    @Override
    public Move makeMove(Board board) {
        // Fallback anche se non dovrebbe accadere che il metodo sia passato con una Board piena
        List<Move> available = board.getAvailableMoves(getSymbol());
        if (available.isEmpty()) {
            throw new IllegalStateException("makeMove called on full board");
        }

        List<Move> bestMoves = new ArrayList<>();   // Si possono avere più mosse dallo stesso valore
        int bestScore = Integer.MIN_VALUE;

        for (Move move : available) {
            Board copy = copyBoard(board);
            copy.applyMove(move);

            int score = minimax(copy, false);

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        // Scelta random tra le migliori mosse
        return bestMoves.get(rnd.nextInt(bestMoves.size()));
    }

    /**
     * Algoritmo Minimax ricorsivo.
     * @param board stato attuale della griglia
     * @param isMaximizing true se tocca al bot, false se tocca all’avversario
     * @return punteggio dell’esito previsto
     */
    private int minimax(Board board, boolean isMaximizing) {
        BoardStatus status = board.getStatus();

        // Caso base: vittoria +1, sconfitta -1, pareggio 0
        if (status == BoardStatus.WIN_X) {
            return (getSymbol() == Symbol.X) ? +1 : -1;
        }
        if (status == BoardStatus.WIN_O) {
            return (getSymbol() == Symbol.O) ? +1 : -1;
        }
        if (status == BoardStatus.DRAW) {
            return 0;
        }

        // Si determina il simbolo del giocatore corrente in base allo stato di isMaximizing, infatti se questo è false sicuraente si è in un turno
        // dell'avversario
        Symbol current = isMaximizing ? getSymbol() : opposite(getSymbol());


        // Inizializzazione del bestScore ad un valore che sarà sicurament superato da quelli delle mosse
        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // Ricorsione
        for (Move move : board.getAvailableMoves(current)) {
            Board copy = copyBoard(board);
            copy.applyMove(move);

            int score = minimax(copy, !isMaximizing);

            if (isMaximizing) {
                bestScore = Math.max(bestScore, score);
            } else {
                bestScore = Math.min(bestScore, score);
            }
        }

        return bestScore;
    }

    // Ritorna il simbolo opposto di quello passato per parametro
    private Symbol opposite(Symbol s) {
        return (s == Symbol.X) ? Symbol.O : Symbol.X;
    }
}

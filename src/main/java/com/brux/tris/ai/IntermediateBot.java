package com.brux.tris.ai;

import com.brux.tris.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class IntermediateBot extends BotPlayer {

    /* Generatore randomico che permette la scelta randomica tra le celle "buone" qualora ce ne sia la possibilità */
    private final RandomGenerator rnd = RandomGenerator.getDefault();

    /* Costruttore che si appoggia a quello definito nella superclasse astratta BotPlayer */
    public IntermediateBot(Symbol symbol) {
        super(symbol);
    }

    /**
     * Metodo che data una certa Board sfrutta un algortimo semi-intelligente per scegliere la prossima mossa da fare. La strategia consiste nel: <p>
     * <li> tentare di vincere con la mossa qualora ce ne fosse la possibilità </li> <p>
     * <li> se non è possibile vincere con la seguente mossa, allora si tenta di bloccare la vittoria dell'avversario, studiando la Board in corrispondenza
     *   delle celle dell'altro giocatore </li> <p>
     * <li> se anche il secondo giocatore non può vincere con la prossima mossa, l'algoritmo si limita a giocare in ordine: </li>
     * <ul>
     *  <li> Sul centro o i corners in modo randomico </li>
     *  <li> Se non disponibili allora sui lati in modo randomico </li>
     * </ul>
     * @param board Board che mantiene lo stato attuale della partita
     * @return La mossa che l'algoritmo ritiene più vantaggiosa per far vincere il Bot
     */
    @Override
    public Move makeMove(Board board) {
        /* 1. Prova a vincere */
        Move winningMove = findWinningMove(board, getSymbol());
        if (winningMove != null) {
            return winningMove;
        }


        /* 2. Prova a fermare l'avversario */
        Symbol opponent = (getSymbol() == Symbol.X) ? Symbol.O : Symbol.X;  // ottenimento simbolo dell'avversario
        Move blockingMove = findWinningMove(board, opponent);               // ricerca posizione di vittoria dell'avversario
        if (blockingMove != null) {
            return new Move(blockingMove.getRow(), blockingMove.getCol(), getSymbol());  // si utilizza tale mossa ma con simbolo del bot
        }


        /* 3. Gioca random su centro + corners */
        List<int[]> priorityCells = new ArrayList<>();

        if (board.isCellEmpty(1, 1)) { // Aggiunta centro se disponibile
            priorityCells.add(new int[]{1, 1});
        }

        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        for (int[] c : corners) {   // Aggiunta corners disponibili
            if (board.isCellEmpty(c[0], c[1])) {
                priorityCells.add(c);
            }
        }

        if (!priorityCells.isEmpty()) {
            int[] choice = priorityCells.get(rnd.nextInt(priorityCells.size()));
            return new Move(choice[0], choice[1], getSymbol());
        }


        /* 4. Gioca su una casella randomica di quelle available */
        List<Move> available = board.getAvailableMoves(getSymbol());
        if (available.isEmpty()) {
            /* Avvenuto errore da qualche parte, se si chiama makeMove su un player, la board non può essere piena */
            throw new IllegalStateException("makeMove called on full board");
        }
        return available.get(rnd.nextInt(available.size()));
    }
}

package com.brux.tris.ai;

import com.brux.tris.model.*;

import java.util.List;
import java.util.random.RandomGenerator;

public class EasyBot extends BotPlayer {

    private final RandomGenerator rnd = RandomGenerator.getDefault();

    public EasyBot(Symbol symbol) {
        super(symbol);
    }

    /**
     * Metodo che data una certa Board sfrutta un algortimo completamente randomico per scegliere la prossima mossa da fare. La strategia consiste nel:
     * <li> Tentare di vincere con la mossa qualora ce ne fosse la possibilità </li>
     * <li> Se non è possibile vincere con la seguente mossa, allora gioca in modo casuale su una delle caselle libere
     *
     * @param board Board che mantiene lo stato attuale della partita
     * @return La mossa generata dall'algoritmo
     */
    @Override
    public Move makeMove(Board board) {
        // 1. Prova a vincere
        Move winningMove = findWinningMove(board, getSymbol());
        if (winningMove != null) {
            return winningMove;
        }

        // 2. Altrimenti, gioca random su una delle celle libere
        List<Move> available = board.getAvailableMoves(getSymbol());
        if (available.isEmpty()) {
            throw new IllegalStateException("makeMove called on full board");
        }
        return available.get(rnd.nextInt(available.size()));
    }
}
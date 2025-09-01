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
     *  <li> Sul centro </li>
     *  <li> Su un corner randomico, con l'accortezza di non prendere un corner opposto ad uno già posseduto se il centro è posseduto dall'avversario </li>
     *  <li> Su una casella randomica di quelle disponibili </li>
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


        /* 3. Gioca al centro */
        if (board.isCellEmpty(1, 1)) {
            return new Move(1, 1, getSymbol());
        }


        /* 4. Gioca in modo intelligente sui corners */
        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        List<int[]> smartCorners = new ArrayList<>();
        List<int[]> availableCorners = new ArrayList<>();

        /* Filtraggio dei corner "furbi" */
        for (int[] c : corners) {
            /* Se il corner corrente è vuoto */
            if (board.isCellEmpty(c[0], c[1])) {
                availableCorners.add(c);

                /* Se al centro c'è l'avversario, evita corner se l'opposto è già posseduto */
                if (board.getCell(1, 1) == opponent && isOppositeCornerOccupiedByBot(board, c)) {
                    continue;
                }
                smartCorners.add(c);
            }
        }

        /* Scelta randomica tra i corners "furbi" */
        if (!smartCorners.isEmpty()) {
            int[] choice = smartCorners.get(rnd.nextInt(smartCorners.size()));
            return new Move(choice[0], choice[1], getSymbol());
        }


        /* 5. Gioca su un corner lo stesso anche se non è furbo se è l'unico rimanente */
        if (!availableCorners.isEmpty()) {
            int[] choice = availableCorners.get(rnd.nextInt(availableCorners.size()));
            return new Move(choice[0], choice[1], getSymbol());
        }


        /* 6. Gioca su una casella randomica di quelle available */
        List<Move> available = board.getAvailableMoves(getSymbol());
        if (available.isEmpty()) {
            /* Avvenuto errore da qualche parte, se si chiama makeMove su un player, la board non può essere piena */
            throw new IllegalStateException("makeMove called on full board");
        }
        return available.get(rnd.nextInt(available.size()));
    }

    /**
     * Metodo di ausilio per makeMove, che prova tutte le mosse disponibili, simulando la board con una sua copia, e controlla se almeno
     * una di queste porta alla vittoria. Se trovata questa mossa, viene subito restituita.
     * @param board Board che mantiene lo stato attuale della partita
     * @param symbol Simbolo del giocatore per cui si sta cercando la posizione vincente
     * @return Mossa che porta alla vittoria se trovata, se no null
     */
    private Move findWinningMove(Board board, Symbol symbol) {
        /* Si itera sulle mosse disponibili, sfruttando il metodo getAvailableMoves della classe Board. Si noti che al metodo viene passato symbol,
        * dunque tutte le mosse appartenenti alla lista risultante avranno anche il simbolo corretto associato */
        for (Move move : board.getAvailableMoves(symbol)) {

            /* Creazione della copia della board per poter aggiungere le varie mosse disponibili e verificare la condizione di vittoria */
            Board copy = copyBoard(board);
            copy.applyMove(move);

            if (copy.checkWinner() == (symbol == Symbol.X ? GameResult.WIN_X : GameResult.WIN_O)) {
                return move;
            }
        }
        return null;
    }

    /**
     * Metodo di ausilio per makeMove, per quanto riguarda la giocata intelligente sui corner. Il metodo si limita a dire se dato un corner della
     * griglia, quello opposto sia posseduto dal bot stesso
     * @param board Board relativo allo stato corrente della partita
     * @param candidate Corner studiato
     * @return true se il corner opposto è posseduto dal bot, false altrimenti
     */
    private boolean isOppositeCornerOccupiedByBot(Board board, int[] candidate) {
        /* Array di array di int la cui sequenza numerica rappresenta una coppia di corner opposti */
        int[][] opposites = {
                {0,0, 2,2},
                {2,2, 0,0},
                {0,2, 2,0},
                {2,0, 0,2}
        };
        for (int[] pair : opposites) {
            if (candidate[0] == pair[0] && candidate[1] == pair[1]) {
                return board.getCell(pair[2], pair[3]) == getSymbol();
            }
        }
        return false;
    }

    /**
     * Metodo di ausilio per findWinningMove, quello che fa è limitarsi a fare una copia della Board attuale, per poter aggiungere le varie mosse
     * di test, su cui testare la vincita
     * @param original Board originale associata alla mossa corrente
     * @return Copia della Board originale
     */
    private Board copyBoard(Board original) {
        Board copy = new Board();   // Per costruzione impostata a tutti EMPTY

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                /* Ha senso aggiungere la cella nella copia solo se questa non era vuota, infatti di default la copia parte con tutte caselle
                *  vuote */
                Symbol s = original.getCell(r, c);
                if (s != Symbol.EMPTY) {
                    copy.applyMove(new Move(r, c, s));
                }
            }
        }
        return copy;
    }

}

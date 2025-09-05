package com.brux.tris.model;

/* Classe astratta che rappresenta i vari tipi di bot, a seconda della difficoltà */
public abstract class BotPlayer implements Player {

    /* Campo posseduto da tutti i bot a prescindere dalla bravura, rappresenta il simbolo associato */
    private final Symbol symbol;

    /* Costruttore */
    public BotPlayer(Symbol symbol) {
        this.symbol = symbol;
    }

    /* Implementazione del metodo getSymbol dell'interfaccia player, che si limita a tornare il valore contenuto nel campo symbol */
    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    /* Il metodo makeMove, viene ridichiarato come astratto per chiarezza, ma senza essere reimplementato, infatti verrà implementato dai singoli
    *  Bot che estenderanno questa classe astratta */
    @Override
    public abstract Move makeMove(Board board);


    /* -------------- UTILITIES per bots ------------- */
    /**
     * Metodo di ausilio per makeMove, che prova tutte le mosse disponibili, simulando la board con una sua copia, e controlla se almeno
     * una di queste porta alla vittoria. Se trovata questa mossa, viene subito restituita.
     * @param board Board che mantiene lo stato attuale della partita
     * @param symbol Simbolo del giocatore per cui si sta cercando la posizione vincente
     * @return Mossa che porta alla vittoria se trovata, se no null
     */
    protected Move findWinningMove(Board board, Symbol symbol) {
        /* Si itera sulle mosse disponibili, sfruttando il metodo getAvailableMoves della classe Board. Si noti che al metodo viene passato symbol,
         * dunque tutte le mosse appartenenti alla lista risultante avranno anche il simbolo corretto associato */
        for (Move move : board.getAvailableMoves(symbol)) {

            /* Creazione della copia della board per poter aggiungere le varie mosse disponibili e verificare la condizione di vittoria */
            Board copy = copyBoard(board);
            copy.applyMove(move);

            if (copy.getStatus() == (symbol == Symbol.X ? BoardStatus.WIN_X : BoardStatus.WIN_O)) {
                return move;
            }
        }
        return null;
    }

    /**
     * Metodo di ausilio per findWinningMove, quello che fa è limitarsi a fare una copia della Board attuale, per poter aggiungere le varie mosse
     * di test, su cui testare la vincita
     * @param original Board originale associata alla mossa corrente
     * @return Copia della Board originale
     */
    protected Board copyBoard(Board original) {
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
    /* ------------------------------------------------------ */
}

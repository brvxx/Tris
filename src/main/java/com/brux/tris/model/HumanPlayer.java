package com.brux.tris.model;

public class HumanPlayer implements Player{

    private final Symbol symbol;

    /* I giocatori umani non calcolano la loro mossa come fa il bot, ma si limitano a riceverla tramite il controller, in base a quanto
    * selezionato dall'utente sulla UI */
    private Move pendingMove;

    /* Costruttore inizializza solamente il simbolo associato al giocatore, la mossa verrà settata di volta in volta dal controller */
    public HumanPlayer(Symbol symbol) {
        this.symbol = symbol;
    }

    /* Setter che viene chiamato dal controller, e salva la mossa corrente effettuata dall'utente */
    public void setPendingMove(Move pendingMove) {
        this.pendingMove = pendingMove;
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    /* Come detto sopra, qui non viene effettivamente calcolato nulla, ci si limita a tornare la mossa fatta dall'utente contenuta nel campo
    *  pendingMove */
    @Override
    public Move makeMove(Board board) {
        /* Salviamo la mossa dentro un riferimento temporaneo, che sarà anche quello ritornato */
        Move move = pendingMove;

        /* Puliamo la pendingMove per poter accettare quella successiva */
        pendingMove = null;

        return move;
    }
}

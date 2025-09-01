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
    *   Bot che estenderanno questa classe astratta */
    @Override
    public abstract Move makeMove(Board board);
}

package com.brux.tris.model;

/* Interfaccia che definisce i metodi che dovranno avere i vari player, che siano umani o bot */
public interface Player {

    /* Restituisce il simbolo associato al giocatore */
    Symbol getSymbol();

    /* Restituisce la mossa proposta dal giocatore */
    Move makeMove(Board board);
}

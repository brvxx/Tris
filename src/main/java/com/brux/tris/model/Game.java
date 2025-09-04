package com.brux.tris.model;

import com.brux.tris.service.ScoreKeeper;

/*
* Si noti che all'interno di questo file quando si utilizza:
* - round: si fa riferimento ai singoli game di tris all'interno della serie "infinita"
* - parita: si fa riferimento appunto a questa serie "infinita" di round
* */

/* Gestore delle partite */
public class Game {

    private final Player playerX;
    private final Player playerO;
    private final Board board;
    private final Mode mode;
    private Player currentPlayer;
    private final ScoreKeeper scoreKeeper;      // tiene traccia dello score della partita attuale

    /* Costruttore */
    public Game(Player playerX, Player playerO, ScoreKeeper scoreKeeper, Mode mode) {
        this.playerX = playerX;
        this.playerO = playerO;
        this.scoreKeeper = scoreKeeper;
        this.mode = mode;

        this.board = new Board();
        this.currentPlayer = playerX;   // Di default a partire nel primo round della partita è il simbolo X
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public Mode getMode() {
        return mode;
    }

    /**
     * Metodo che effettua la mossa del giocatore corrente.
     * <ul>
     *     <li> Se il giocatore umano non ha la mossa pronta non fa nulla </li>
     *     <li> Se il bot o l'umano hanno la mossa pronta, la applica, aggiornando la Board </li>
     *     <li> Se necessario (fine round) aggiorna lo score </li>
     *     <li> Se il round è ancora in corso (ONGOING) effettua il cambio del giocatore corrente </li>
     * </ul>
     * @return Lo stato corrente della Board
     */
    public BoardStatus playTurn() {
        Move move = currentPlayer.makeMove(board);  // salvataggio della mossa fatta dal player corrente

        if (move == null) {
            /* Caso in cui il player umano non aveva la mossa pronta (in attesa della mossa) */
            return board.getStatus();   // fallback
        }

        board.applyMove(move);
        BoardStatus status = board.getStatus();   // nuovo stato della board

        if (status == BoardStatus.ONGOING) {
            /* Partita non finita, si switcha il player */
            switchPlayer();
        } else {
            /* Partita terminata, si aggiorano gli score */
            updateScore(status);
        }

        return status;
    }

    /**
     * Effettua il cambio turno - metodo di ausiolio per playTurn
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
    }

    /**
     * Aggiorna lo score della partita attuale - metodo di ausilio per playTurn
     * @param status Stato in cui si è conclusa il round
     */
    private void updateScore(BoardStatus status) {
        if (status == BoardStatus.WIN_O) {
            scoreKeeper.increment(Symbol.O);
        } else if (status == BoardStatus.WIN_X) {
            scoreKeeper.increment(Symbol.X);
        }
    }

    /**
     * Avvia un nuovo round all'interno della partita attuale, impostando dunque il player che inizia
     * @param starter Giocatore che inizierà il prossimo round
     */
    public void startNewRound(Player starter) {
        board.reset();
        currentPlayer = starter;
    }

    /**
     * Stabilisce chi deve cominciare il prossimo round in funzione di come è terminato il precedente secondo questa logica:
     * <li>Se qualcuno ha perso inizia lui </li>
     * <li>Se è avvenuto un pareggio, inizia quello che non ha iniziato il round precedente </li>
     * @param lastStatus Stato in cui è terminato il round precedente
     * @return Giocatore che inizierà il prossimo round
     */
    public Player getNextStarter(BoardStatus lastStatus) {
        if (lastStatus == BoardStatus.WIN_O) {  // sconfitta di X, dunque inizia lui
            return playerX;
        } else if (lastStatus == BoardStatus.WIN_X) { // sconfitta di O, dunque inzia lui
            return playerO;
        } else { // pareggio
            /* Si noti che in condizione di pareggio, dunque nella quale tutte e 9 le caselle sono state occupate, ad effettuare l'ultima mossa
            *  (currentPlayer) è anche colui che ha iniziato la partita */
            return (currentPlayer == playerX) ? playerO : playerX;
        }
    }
}

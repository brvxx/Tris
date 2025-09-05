package com.brux.tris.controller;

import com.brux.tris.model.*;
import com.brux.tris.ai.*;
import com.brux.tris.service.*;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.function.Function;

/* Controller che funge da ponte tra UI e logica del gioco */
public class GameController {

    private static final String DEFAULT_CELL_STYLE = "-fx-background-color: rgba(0, 0, 0, 0.15);";
    private static final String WIN_CELL_STYLE     = "-fx-background-color: rgba(0, 0, 0, 0.40);";

    /* Collegamenti coi controlli definiti nella GUI. Si noti che non verranno collegati qui i singoli bottoni delle caselle della griglia, per
    *  evitare di avere 9 riferimenti differenti, ma si utilizzerà un array bidimensionale per rendere il tutto più pulito */
    @FXML
    private ChoiceBox<String> modeChoice;

    @FXML
    private ChoiceBox<String> diffChoice;

    @FXML
    private ChoiceBox<String> symbolChoice;

    @FXML
    private Label modeLabel;

    @FXML
    private Label diffLabel;

    @FXML
    private Label symbolLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button clearButton;

    @FXML
    private Button newRoundButton;

    @FXML
    private GridPane boardGrid;

    /* Matrice di Bottoni che utilizzeremo per riferire le varie celle in modo uniforme (settata dentro initialize()) */
    private Button[][] cells;

    /* Campi necessari alla logica delle partite */
    private Game game;
    private ScoreKeeper scoreKeeper;

    /* Giocatori correnti */
    private Player playerX;
    private Player playerO;

    /* Risultato del round precedente. Serve per startare il successivo */
    private BoardStatus lastRoundStatus;

    /* Stato interno per ricordare la difficoltà scelta */
    private enum Difficulty {
        EASY {
            @Override
            public String toString() {
                return "Facile";
            }
        },
        INTERMEDIATE {
            @Override
            public String toString() {
                return "Intermedio";
            }
        },
        EXTREME {
            @Override
            public String toString() {
                return "Estremo";
            }
        }
    }
    private Difficulty selectedDifficulty;

    /* Metodo chiamato da JavaFX una volta che la GUI è stata creata a seguito del loading del FXML per prepararla ad essere utilizzata */
    @FXML
    private void initialize() {

        // 1. Collegamento dei bottoni alla matrice cells
        cells = new Button[3][3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                // Cerca il bottone con fx:id tipo "cell00", "cell01", ecc. per poi linkarlo al corrispettivo bottone della matrice
                String id = "cell" + r + c;
                Button cell = (Button) boardGrid.lookup("#" + id);
                cells[r][c] = cell;

                int row = r;
                int col = c;

                // Aggiunge il listener al click dei vari bottoni
                cell.setOnAction(e -> handleCellClick(row, col));
            }
        }

        // 2. Popolamento delle ChoiceBox
        modeChoice.getItems().addAll("Single Player", "Scontro");
        diffChoice.getItems().addAll("Facile", "Intermedio", "Estremo");
        symbolChoice.getItems().addAll("X", "O");

        // 3. Inizializzazione del punteggio iniziale
        scoreKeeper = new ScoreKeeper();
        scoreLabel.setText("X: 0  |  O: 0");

        // 4. Messaggio iniziale
        statusLabel.setText("Benvenuto! Scegli modalità di gioco");

        // 5. Listener scelta della modalità
        modeChoice.setOnAction(e -> {
            String mode = modeChoice.getValue();

            if (mode == null) return;   // per evitare di incorrere in NPE qualora si resetti la modeChoice (azione triggera il OnAction)

            if (mode.equals("Single Player")) {
                // Si rende visibile la scelta della difficoltà
                diffChoice.setVisible(true);
                diffLabel.setVisible(true);

                statusLabel.setText("Scegli la difficoltà del bot");
            } else if (mode.equals("Scontro")) {
                diffChoice.setVisible(false);
                diffLabel.setVisible(false);
                symbolChoice.setVisible(false);
                symbolLabel.setVisible(false);

                startMultiplayerGame();
            }
        });

        // 6. Listener scelta della difficoltà del bot
        diffChoice.setOnAction(e -> {
            String choice = diffChoice.getValue();
            if (choice == null) return;

            switch (choice) {
                case "Facile"     -> selectedDifficulty = Difficulty.EASY;
                case "Intermedio" -> selectedDifficulty = Difficulty.INTERMEDIATE;
                case "Estremo"    -> selectedDifficulty = Difficulty.EXTREME;
                default           -> selectedDifficulty = Difficulty.INTERMEDIATE;  // fallback, non dovrebbe succedere
            }

            // Si rende visibile la scelta del simbolo
            symbolChoice.setVisible(true);
            symbolLabel.setVisible(true);

            statusLabel.setText("Scegli se vuoi giocare con X o O");
        });

        // 7. Listener scelta del simbolo (solo per single-player)
        symbolChoice.setOnAction(e -> {
            String choice = symbolChoice.getValue();
            if (choice != null && selectedDifficulty != null) { // per evitare di incorrere in NPE qualora si resetti la symbolChoice (azione triggera il OnAction)
                startSinglePlayerGame(choice.equals("X") ? Symbol.X : Symbol.O);
            }
        });

        // 8. Listener bottone di clearing della partita
        clearButton.setOnAction(e -> handleClear());

        // 9. Listener bottone di nuovo round
        newRoundButton.setOnAction(e -> handleNewRound());
    }

    /*----------------- LISTENERS -----------------*/
    private void handleCellClick(int row, int col) {
        // Se non c'è alcuna partita in corso, il click delle celle viene ignorato
        if (game == null) return;

        // Se si è in game e l'utente prova a cliccare su una cella già occupata, il click viene ignorato
        if (!game.getBoard().isCellEmpty(row, col)) return;

        Player current = game.getCurrentPlayer();

        // Se è il turno di un umano allora si crea la Mossa in base alla cella cliccata e la si inserisce dentro al player. Il controllo
        // sul tipo del Player viene fatto per evitare che in Single Player un umano tenti di giocare durante il turno del Bot
        if (current instanceof HumanPlayer) {
            ((HumanPlayer) current).setPendingMove(new Move(row, col, current.getSymbol()));
            playTurnAndUpdate();
        }
    }

    private void handleClear() {
        // 1. Reset logica
        scoreKeeper.reset();        // reset dello score
        updateScore();              // mostra sulla scoreLabel lo score aggiornato
        game = null;                // annulla la partita corrente
        lastRoundStatus = null;     // resetta lo stato del round precedente
        selectedDifficulty = null;  // resetta la scelta della difficoltà del bot


        // 2. Reset interfaccia
        modeChoice.setVisible(true);
        modeLabel.setVisible(true);
        diffChoice.setVisible(false);
        diffLabel.setVisible(false);
        symbolChoice.setVisible(false);
        symbolLabel.setVisible(false);
        clearButton.setVisible(false);  // clear torna invisibile
        newRoundButton.setVisible(false);

        // Reset della griglia (svuota i bottoni)
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                cells[r][c].setText("");
                cells[r][c].setDisable(false);  // riattiva i bottoni se erano bloccati
            }
        }

        // Pulizia Higlights dell'ultima vittoria (se clear chiamato prima di un nuovo round con vincita)
        clearHighlight();

        // Reset scelta nelle ChoiceBox
        modeChoice.getSelectionModel().clearSelection();
        diffChoice.getSelectionModel().clearSelection();
        symbolChoice.getSelectionModel().clearSelection();


        statusLabel.setText("Benvenuto! Scegli modalità di gioco.");
    }

    private void handleNewRound() {
        // Precauzione: se non si ha un game oppure se non si ha lo stato del round precedente non si fa nulla
        if (game == null || lastRoundStatus == null) return;

        // Scelta nuovo giocatore ad iniziare fatta tramite il metodo
        Player starter = game.getNextStarter(lastRoundStatus);

        game.startNewRound(starter);  // Reset board + set currentPlayer
        refreshBoard();               // Pulizia GUI
        clearHighlight();             // Pulizia eventuali highlights
        setBoardEnabled(true);        // Riabilita i click umani

        newRoundButton.setVisible(false);

        statusLabel.setText("Nuovo round! Tocca a " + starter.getSymbol());

        // Se si è in Single Player e parte il bot si effettua la sua prima mossa col delay
        if (game.getMode() == Mode.SINGLE_PLAYER && starter instanceof BotPlayer) {
            triggerBotTurnWithDelay(); // usa l’helper che hai già aggiunto
        }
    }
    /*-----------------------------------------------*/


    /*------------------- LOGICS --------------------*/
    private void startSinglePlayerGame(Symbol humanSymbol) {
        scoreKeeper.reset();
        updateScore();

        Function<Symbol, Player> botFor = (sym) -> switch (selectedDifficulty) {
            case EASY         -> new EasyBot(sym);
            case INTERMEDIATE -> new IntermediateBot(sym);
            case EXTREME      -> new ExtremeBot(sym);
            default           -> new IntermediateBot(sym); // fallback, non dovrebbe succedere
        };

        if (humanSymbol == Symbol.X) {
            playerX = new HumanPlayer(Symbol.X);
            playerO = botFor.apply(Symbol.O);
        } else {
            playerX = botFor.apply(Symbol.X);
            playerO = new HumanPlayer(Symbol.O);
        }

        game = new Game(playerX, playerO, scoreKeeper, Mode.SINGLE_PLAYER);
        game.startNewRound(playerX);    // Al primo round parte sempre PlayerX

        // Nascosti controlli di scelta
        modeChoice.setVisible(false);
        modeLabel.setVisible(false);
        diffChoice.setVisible(false);
        diffLabel.setVisible(false);
        symbolChoice.setVisible(false);
        symbolLabel.setVisible(false);

        // Reso visibile il pulsante di clearing
        clearButton.setVisible(true);

        statusLabel.setText("Modalità Single Player " + selectedDifficulty + ". Tocca a X!");

        // Se il primo player a dover giocare la partita è il bot, si avvia la sua mossa
        if (game.getCurrentPlayer() instanceof BotPlayer) {
            triggerBotTurnWithDelay();
        }
    }

    private void startMultiplayerGame() {
        scoreKeeper.reset();
        updateScore();

        playerX = new HumanPlayer(Symbol.X);
        playerO = new HumanPlayer(Symbol.O);

        game = new Game(playerX, playerO, scoreKeeper, Mode.SCONTRO);
        game.startNewRound(playerX);

        // Nascondi controlli di scelta
        modeChoice.setVisible(false);
        modeLabel.setVisible(false);
        diffChoice.setVisible(false);
        diffLabel.setVisible(false);
        symbolChoice.setVisible(false);
        symbolLabel.setVisible(false);

        // Reso visibile il pulsante di clearing
        clearButton.setVisible(true);

        statusLabel.setText("Modalità Scontro. Tocca a X!");
    }

    /* Metodo di update degli score */
    private void updateScore() {
        int scoreX = scoreKeeper.getScore(Symbol.X);
        int scoreO = scoreKeeper.getScore(Symbol.O);
        scoreLabel.setText("X: " + scoreX + "  |  O: " + scoreO);
    }

    private void playTurnAndUpdate() {
        BoardStatus status = game.playTurn();   // Player corrente effettua la sua mossa e viene tornato il NUOVO stato della board
        refreshBoard();                         // Refresh della GUI (grid)

        if (status != BoardStatus.ONGOING) {    // Se la partita è finita
            handleRoundEnd(status);
        } else {    // se la partita sta ancora continuando A PRESCINDERE DALLA MODALITA'
            statusLabel.setText("Tocca a " + game.getCurrentPlayer().getSymbol());

            // Se si è in single player a questo punto si fa giocare il bot
            if (game.getMode() == Mode.SINGLE_PLAYER) {
                triggerBotTurnWithDelay();
            }
        }
    }

    private void handleRoundEnd(BoardStatus status) {
        lastRoundStatus = status;   // memorizza l’esito per decidere chi parte il round successivo

        String message;
        switch (status) {
            case WIN_X -> {
                message = "Ha vinto X!";
                highlightWinningLine(game.getBoard(), Symbol.X);
            }
            case WIN_O -> {
                message = "Ha vinto O!";
                highlightWinningLine(game.getBoard(), Symbol.O);
            }
            default    -> message = "Pareggio!";
        }
        statusLabel.setText(message);

        updateScore();                      // legge dallo ScoreKeeper e aggiorna la label
        setBoardEnabled(false);             // blocca i bottoni della griglia
        newRoundButton.setVisible(true);    // mostra il pulsante per ripartire
    }
    /*-----------------------------------------------*/



    /*------------------ HELPERS --------------------*/
    /* Aggiorna la UI a seguito di una qualche azione sulla board */
    private void refreshBoard() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Symbol s = game.getBoard().getCell(r, c);
                if (s == Symbol.EMPTY) {
                    cells[r][c].setText("");
                } else {

                    if (s == Symbol.O) {    // Simbolo O
                        cells[r][c].setTextFill(Color.web("#f2ebd3"));
                    } else {    // Simbolo X
                        cells[r][c].setTextFill(Color.web("#545454"));
                    }

                    cells[r][c].setText(s.toString());
                }
            }
        }
    }

    /* Effettua il turno del bot con del delay per dare possibilità al player di avere del tempo tra i vari turni, simulando dunque
    *  un comportamento umano */
    private void triggerBotTurnWithDelay() {

        // Precauzione: se non c’è partita o non è un bot a dover giocare, non fare nulla
        if (game == null || !(game.getCurrentPlayer() instanceof BotPlayer)) return;

        // Aggiornamento Label di stato della partita: turno del bot
        statusLabel.setText("Tocca a " + game.getCurrentPlayer().getSymbol());

        // Creazione della pausa e settaggio della azione post-pausa, ossia il turno del bot
        PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
        pause.setOnFinished(e -> {
            BoardStatus afterBot = game.playTurn();
            refreshBoard();

            if (afterBot != BoardStatus.ONGOING) {
                handleRoundEnd(afterBot);
            } else {
                statusLabel.setText("Tocca a " + game.getCurrentPlayer().getSymbol());
            }
        });

        // Avvio effettivo della pausa
        pause.play();
    }

    /* Abilita/Disabilita tutti i tasti della Grid per evitare che vengano cliccati quando non devono esserlo */
    private void setBoardEnabled(boolean enabled) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                cells[r][c].setDisable(!enabled);
            }
        }
    }

    /* Highlight delle caselle che hanno portato alla vittoria nel round corrente */
    private void highlightWinningLine(Board board, Symbol winner) {
        // Reset eventuali highlight precedenti
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                cells[r][c].setStyle("-fx-background-color: rgba(0, 0, 0, 0.15);");
            }
        }

        // Possibili combinazioni di celle che compongono le win conditions nel tris
        int[][][] lines = {
                {{0,0},{0,1},{0,2}}, // righe
                {{1,0},{1,1},{1,2}},
                {{2,0},{2,1},{2,2}},
                {{0,0},{1,0},{2,0}}, // colonne
                {{0,1},{1,1},{2,1}},
                {{0,2},{1,2},{2,2}},
                {{0,0},{1,1},{2,2}}, // diagonali
                {{0,2},{1,1},{2,0}}
        };

        // Scorrimento delle varie linee che rappresentano le win conditions
        for (int[][] line : lines) {
            Symbol a = board.getCell(line[0][0], line[0][1]);
            Symbol b = board.getCell(line[1][0], line[1][1]);
            Symbol c = board.getCell(line[2][0], line[2][1]);

            if (a == winner && b == winner && c == winner) {
                // Le varie posizioni della linea vincente vengono evidenziate
                for (int[] pos : line) {
                    cells[pos[0]][pos[1]].setStyle(WIN_CELL_STYLE);
                }
                break;
            }
        }
    }

    /* Pulizia degli highlights */
    private void clearHighlight() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                cells[r][c].setStyle(DEFAULT_CELL_STYLE);
            }
        }
    }
    /*-----------------------------------------------*/
}

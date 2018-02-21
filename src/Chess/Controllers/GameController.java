package Chess.Controllers;

import Chess.Models.Board;
import Chess.Types.PlayerColor;
import Chess.Views.BoardPanel;
import Chess.Views.GameControlPanel;
import Chess.Views.GameControlPanelDelegate;
import Chess.Views.OnboardingPanel;

import javax.swing.*;
import java.awt.*;

/**
 * The game controller that will be the entry to the chess game.
 * This controller manages the GameControlPanel and let it communicates with the ChessGameController, which is
 * a child controller of GameController. Therefore, this controller will manage the lifecycle of a game, eg. restart,
 * forfeit, start, funky mode, etc..
 */
public class GameController implements GameControlPanelDelegate, ChessGameControllerDelegate {

    GameControllerState state = GameControllerState.ONBOARDING;

    JFrame rootWindow;
    OnboardingPanel onboardingPanel = new OnboardingPanel();
    GameControlPanel controlPanel = new GameControlPanel(state);

    ChessGameController chessController;

    /**
     * variables for holding the scores, since no consistent storage is required, letting the controller
     * manages the scoring is sufficient.
      */
    String blackPlayerName;
    String whitePlayerName;
    int blackPlayerScore = 0;
    int whitePlayerScore = 0;

    boolean isFunky = false;

    /**
     * Initilizer for GameController. Upon initialization, it subscribe to the user-side change a control panel will make
     * and initialize the layout of its panels(onboarding, controlPanel and ChessGameController's boardPanel).
     */
    GameController() {
        controlPanel.setDelegate(this);
        initializeViews();
    }

    /**
     * View builder
     */
    void initializeViews() {
        rootWindow = new JFrame("Chess");
        rootWindow.setVisible(true);
        rootWindow.getContentPane().setLayout(null);
        Dimension boardSize = new Dimension(
                BoardPanel.TILE_SIZE.width * 8,
                BoardPanel.TILE_SIZE.width * 8 + BoardPanel.MENU_BAR_HEIGHT);
        controlPanel.setSize(new Dimension(GameControlPanel.PANEL_WIDTH,boardSize.height));
        onboardingPanel.setBounds(GameControlPanel.PANEL_WIDTH,0,boardSize.width,boardSize.height);
        rootWindow.getContentPane().add(controlPanel);
        rootWindow.getContentPane().add(onboardingPanel);
        Dimension windowSize = new Dimension(boardSize.width + GameControlPanel.PANEL_WIDTH, boardSize.height);
        rootWindow.setSize(windowSize);
        rootWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rootWindow.setResizable(false);
    }

    /**
     * startNewGame() will initialize a new game by initializing a new instance of the ChessGameControoler and
     * update the current UI.
     */
    void startNewGame() {
        if (chessController != null) {
            rootWindow.getContentPane().remove(chessController.boardPanel);
        }
        chessController = new ChessGameController(isFunky);
        chessController.setDelegate(this);
        onTurnChange(PlayerColor.WHITE);
        chessController.boardPanel.setBounds(100,0,512,530);
        rootWindow.getContentPane().remove(onboardingPanel);
        rootWindow.getContentPane().add(chessController.boardPanel);
        rootWindow.validate();
    }

    // Delegate methods for GameControlPanelDelegate.
    // They respond to the UI events taken place on BoardControlPanel
    public void onClickStart(String blackPlayerName, String whitePlayerName) {
        this.blackPlayerName = blackPlayerName;
        this.whitePlayerName = whitePlayerName;
        this.setState(GameControllerState.IN_GAME);
    }
    public void onClickRestart() {
        startNewGame();
    }

    public void onClickReset() {
        setState(GameControllerState.ONBOARDING);
    }

    public void onClickForfeit() {
        if (chessController.turnColor == PlayerColor.BLACK) {
            this.setWhitePlayerScore(whitePlayerScore + 1);
        } else {
            this.setBlackPlayerScore(blackPlayerScore + 1);
        }
        startNewGame();
    }

    public void onClickUndo() {
        chessController.undoLastTurn();
        controlPanel.setUndoEnabled(chessController.isUndoAvailable());
    }

    public void onClickFunky() {
        isFunky = true;
    }

    // Delegate methods for ChessGameControllerDelegate.
    // They respond to the game events on the board
    @Override
    public void onWin(PlayerColor winnerColor) {
        if (winnerColor == PlayerColor.BLACK) {
            this.setBlackPlayerScore(blackPlayerScore + 1);
        } else {
            this.setBlackPlayerScore(whitePlayerScore + 1);
        }
        JOptionPane.showMessageDialog(null, winnerColor.toString() + " player won");
    }

    @Override
    public void onStalemate() {
        JOptionPane.showMessageDialog(null,  "Stalemate");
    }

    @Override
    public void onTurnChange(PlayerColor newColor) {
        controlPanel.setCurrentTurnColor(newColor);
        controlPanel.setUndoEnabled(chessController.isUndoAvailable());
    }

    /** Setter for state of type GameControllerState
    * When ONBOARDING, the UI will show Onboarding panel, reset the player's score and nickname.
     * and for both cases, we will notify the controlPanel to show corresponding UI state.
     */
    public void setState(GameControllerState state) {
        this.state = state;
        if (state == GameControllerState.ONBOARDING) {
            this.setBlackPlayerName(null);
            this.setBlackPlayerScore(0);
            this.setWhitePlayerName(null);
            this.setWhitePlayerScore(0);
            isFunky = false;
            if (chessController != null) {
                rootWindow.getContentPane().remove(chessController.boardPanel);
                onboardingPanel.setBounds(100,0,512,530);
                rootWindow.getContentPane().add(onboardingPanel);
                rootWindow.revalidate();
                rootWindow.repaint();
            }
        } else {
            startNewGame();
        }
        this.controlPanel.setState(state);
    }

    public void setBlackPlayerName(String blackPlayerName) {
        this.blackPlayerName = blackPlayerName;
        this.controlPanel.setPlayerName(blackPlayerName, PlayerColor.BLACK);
    }

    public void setWhitePlayerName(String whitePlayerName) {
        this.whitePlayerName= whitePlayerName;
        this.controlPanel.setPlayerName(whitePlayerName, PlayerColor.WHITE);
    }

    public void setBlackPlayerScore(int blackPlayerScore) {
        this.blackPlayerScore = blackPlayerScore;
        this.controlPanel.setPlayerScore(blackPlayerScore, PlayerColor.BLACK);
    }

    public void setWhitePlayerScore(int whitePlayerScore) {
        this.whitePlayerScore = whitePlayerScore;
        this.controlPanel.setPlayerScore(whitePlayerScore, PlayerColor.WHITE);
    }

    public static void main(String args[]) {
        new GameController();
    }

}

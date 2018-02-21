package Chess.Views;

import Chess.Controllers.GameController;
import Chess.Controllers.GameControllerState;
import Chess.Types.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GameControlPanel for the main game interface. This panel consists of game control buttons and
 * also displays turn status, current score and players' nickname.
 */
public class GameControlPanel extends JPanel {
    public static int PANEL_WIDTH = 100;
    public static int ELEMENT_HEIGHT  = 30;

    public GameControlPanelDelegate delegate;

    //UI elements:
    JLabel blackPlayerLabel = new JLabel("Black:", SwingConstants.CENTER);
    JLabel whitePlayerLabel = new JLabel("White:", SwingConstants.CENTER);
    JTextField blackPlayerNameField = new JTextField();
    JTextField whitePlayerNameField  = new JTextField();
    JLabel blackPlayerScoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
    JLabel whitePlayerScoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
    JLabel turnDisplayLabel = new JLabel("", SwingConstants.CENTER);
    JLabel funkyLabel = new JLabel("Get funky?", SwingConstants.CENTER);
    JButton startButton = new JButton("Start");
    JButton restartButton = new JButton("Restart");
    JButton forfeitButton = new JButton("Forfeit");
    JButton resetButton = new JButton("Reset");
    JButton undoButton = new JButton("Undo");
    JButton funkyButton = new JButton("HELL YEAH");

    public GameControlPanel(GameControllerState gameState){
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setupViewElements();
        setupActions();
        setState(gameState);
    }

    // UI elements setup and layout
    private void setupViewElements() {
        Dimension elementSize = new Dimension(PANEL_WIDTH, ELEMENT_HEIGHT);
        blackPlayerLabel.setPreferredSize(elementSize);
        whitePlayerLabel.setPreferredSize(elementSize);
        blackPlayerNameField.setPreferredSize(elementSize);
        whitePlayerNameField.setPreferredSize(elementSize);
        blackPlayerScoreLabel.setPreferredSize(elementSize);
        whitePlayerScoreLabel.setPreferredSize(elementSize);
        startButton.setPreferredSize(elementSize);
        restartButton.setPreferredSize(elementSize);
        forfeitButton.setPreferredSize(elementSize);
        resetButton.setPreferredSize(elementSize);
        undoButton.setPreferredSize(elementSize);
        turnDisplayLabel.setPreferredSize(elementSize);
        funkyLabel.setPreferredSize(elementSize);
        funkyButton.setPreferredSize(elementSize);

        blackPlayerNameField.setBackground(this.getBackground());
        whitePlayerNameField.setBackground(this.getBackground());

        this.add(blackPlayerLabel);
        this.add(blackPlayerNameField);
        this.add(blackPlayerScoreLabel);
        this.add(whitePlayerLabel);
        this.add(whitePlayerNameField);
        this.add(whitePlayerScoreLabel);
        this.add(startButton);
        this.add(restartButton);
        this.add(resetButton);
        this.add(Box.createRigidArea(new Dimension(PANEL_WIDTH, 20))); // some padding for visual cues
        this.add(turnDisplayLabel);
        this.add(forfeitButton);
        this.add(undoButton);
        this.add(Box.createRigidArea(new Dimension(PANEL_WIDTH, 20))); // ditto
        this.add(funkyLabel);
        this.add(funkyButton);
    }

    // Setup triggers for buttons and link them to their corresponding delegate methods.
    // startButton will also do input validation before triggering the delegate methods.
    private void setupActions() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (delegate != null) {
                    String blackPlayerName = blackPlayerNameField.getText();
                    String whitePlayerName = whitePlayerNameField.getText();
                    if (blackPlayerName.length() > 0 &&
                            whitePlayerName.length() > 0) {
                        delegate.onClickStart(blackPlayerName, whitePlayerName);
                    } else {
                        JOptionPane.showMessageDialog(null,  "Please enter player's name");
                    }
                }
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (delegate != null) {
                    delegate.onClickRestart();
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (delegate != null) {
                    delegate.onClickReset();
                }
            }
        });

        forfeitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (delegate != null) {
                    delegate.onClickForfeit();
                }
            }
        });

        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (delegate != null) {
                    delegate.onClickUndo();
                }
            }
        });

        funkyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (delegate != null) {
                    delegate.onClickFunky();
                    funkyButton.setEnabled(false); //funky button is one off
                }
            }
        });
    }

    public void setDelegate(GameControlPanelDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Update the corresponding UI state according to GameControllerState.
     * @param state GameControllerState of the parent controller.
     */
    public void setState(GameControllerState state) {
        boolean isInGame = state == GameControllerState.IN_GAME;
        this.blackPlayerNameField.setEnabled(!isInGame);
        this.whitePlayerNameField.setEnabled(!isInGame);
        this.startButton.setEnabled(!isInGame);
        this.restartButton.setEnabled(isInGame);
        this.resetButton.setEnabled(isInGame);
        this.forfeitButton.setEnabled(isInGame);
        this.funkyButton.setEnabled(!isInGame);
    }

    public void setPlayerName(String name, PlayerColor color) {
        if (color == PlayerColor.WHITE) {
            whitePlayerNameField.setText(name);
        } else {
            blackPlayerNameField.setText(name);
        }
    }

    public void setPlayerScore(int score, PlayerColor color) {
        if (color == PlayerColor.WHITE) {
            whitePlayerScoreLabel.setText("Score: " + Integer.toString(score));
        } else {
            blackPlayerScoreLabel.setText("Score: " + Integer.toString(score));
        }
    }

    public void setCurrentTurnColor(PlayerColor color) {
        if (color == PlayerColor.BLACK) {
            turnDisplayLabel.setText("Black's turn");
        } else {
            turnDisplayLabel.setText("White's turn");
        }
    }

    public void setUndoEnabled(boolean enabled) {
        this.undoButton.setEnabled(enabled);
    }

}

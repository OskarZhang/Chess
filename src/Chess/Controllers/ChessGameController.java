package Chess.Controllers;

import Chess.Models.Board;
import Chess.Models.Coordinate;
import Chess.Models.Piece;
import Chess.Types.PlayerColor;
import Chess.Views.BoardPanel;
import Chess.Views.BoardPanelDelegate;

import javax.swing.*;
import java.util.Arrays;

/**
 * The controller of the chess game. The lifecycle of the controller is one game of chess.
 */
public class ChessGameController implements BoardPanelDelegate {
    public ChessGameControllerDelegate delegate;
    public Board boardModel;
    public BoardPanel boardPanel;
    public PlayerColor turnColor = PlayerColor.WHITE;

    //Used to store valid moves for current turn so that user does not move to an invalid location
    Coordinate currentPieceLocation;
    Coordinate[] validMoves = new Coordinate[]{};

    // variables used for caching the last move for undo function
    Coordinate lastPieceFromLocation;
    Coordinate lastPieceToLocation;
    Piece lastToPiece;
    Piece lastFromPiece;

    /**
     * The initializer of ChessGameController. Upon initialization, it creates an instance of the Board JPanel and renders
     * the board UI.
     * @param isFunky if true, uses the starting board with custom pieces
     */
    public ChessGameController(boolean isFunky) {
        this.boardModel = new Board(isFunky ? getFunkyStartingBoard() : getStartingBoard());
        boardPanel = new BoardPanel(boardModel);
        boardPanel.setDelegate(this);
    }

    static String getStartingBoard() {
        String blackFirstLine = "#R1#H1#B1#Q1#K1#B1#H1#R1";
        String blackPawns=      "#P1#P1#P1#P1#P1#P1#P1#P1";
        String emptyLine =      "#E2#E2#E2#E2#E2#E2#E2#E2";
        String whitePawns=      "#P0#P0#P0#P0#P0#P0#P0#P0";
        String whiteFirstLine = "#R0#H0#B0#Q0#K0#B0#H0#R0";
        String startingBoardSerializer = blackFirstLine + blackPawns + emptyLine + emptyLine + emptyLine + emptyLine + whitePawns + whiteFirstLine;
        System.out.println(startingBoardSerializer);
        return startingBoardSerializer;
    }

    static String getFunkyStartingBoard() {
        String blackFirstLine = "#R1#Y1#X1#Q1#K1#B1#Y1#R1";
        String blackPawns=      "#P1#P1#P1#P1#P1#P1#P1#P1";
        String emptyLine =      "#E2#E2#E2#E2#E2#E2#E2#E2";
        String whitePawns=      "#P0#P0#P0#P0#P0#P0#P0#P0";
        String whiteFirstLine = "#R0#Y0#X0#Q0#K0#B0#Y0#R0";
        String startingBoardSerializer = blackFirstLine + blackPawns + emptyLine + emptyLine + emptyLine + emptyLine + whitePawns + whiteFirstLine;
        System.out.println(startingBoardSerializer);
        return startingBoardSerializer;
    }

    /**
     * Delegate methods for the triggering of clicking on a tile on board UI.
     * Upon triggering, two scenarios are possible:
     * 1. a click on the current turn player's piece, this will show all the possible moves a player can make
     * 2. a click on a legal move, this will make the move
     * @param coord
     */
    public void onClickTile(Coordinate coord) {
        Piece piece = boardModel.getPieceAtCoordinate(coord);
        if (validMoves != null && Arrays.asList(validMoves).contains(coord)) {
            onMakeMove(coord);
        } else if (piece != null && piece.color == turnColor) {
            onShowReachableMoves(coord);
        }
    }

    void onMakeMove(Coordinate toLocation) {
        Piece currentPiece = boardModel.getPieceAtCoordinate(currentPieceLocation);

        // Check stalemate before the move and notify the delegate if there is one
        if (boardModel.isPlayerInStalemate(currentPiece.color)) {
            if (delegate != null) {
                delegate.onStalemate();
            }
            return;
        }

        // for undo
        this.lastPieceFromLocation = this.currentPieceLocation;
        this.lastPieceToLocation = toLocation;
        //This is nullable. Code will be easy and elegant if java has optional like Kotlin or Swift, oh well..
        this.lastToPiece = boardModel.getPieceAtCoordinate(toLocation);
        if (this.lastToPiece != null) {
            this.lastToPiece = new Piece(this.lastToPiece);
        }
        this.lastFromPiece = new Piece(boardModel.getPieceAtCoordinate(currentPieceLocation));

        //make a move on the board model
        boardModel.moveTo(toLocation,currentPieceLocation);

        //notify the delegate of this class that turn ownership has changed
        turnColor = turnColor.oppositeColor();
        if (delegate != null) {
            delegate.onTurnChange(turnColor);
        }

        // update UI
        boardPanel.updateViewForMove(toLocation, currentPieceLocation, currentPiece);
        this.currentPieceLocation = null;
        this.validMoves = null;

        // check winning at the end of the move.
        if (boardModel.isPlayerWinning(currentPiece.color)) {
            if (delegate != null) {
                delegate.onWin(currentPiece.color);
            }
            return;
        }

        // king check at the end of the move and notify the user. No need to go through the delegate here.
        if (boardModel.isKingInCheck(currentPiece.color.oppositeColor())) {
            JOptionPane.showMessageDialog(null, currentPiece.color.oppositeColor() + " King is in check");
            return;
        }
    }

    /**
     * Get all legal moves a piece can make at coord and update the UI to highlight all legal moves
     */
    void onShowReachableMoves(Coordinate coord) {
        this.validMoves = boardModel.computeReachableMoves(coord);
        this.boardPanel.setTileHighlighted(this.validMoves, turnColor);
        this.currentPieceLocation = coord;
    }

    /**
     * Undo the last turn on board and notifies the delegate of turn change.
     */
    public void undoLastTurn() {
        boardModel.setPieceAtCoordinate(lastToPiece, lastPieceToLocation);
        boardModel.setPieceAtCoordinate(lastFromPiece, lastPieceFromLocation);
        this.turnColor = this.turnColor.oppositeColor();
        boardPanel.updateViewForUndo(lastPieceFromLocation, lastFromPiece, lastPieceToLocation, lastToPiece);
        this.lastToPiece = null;
        this.lastFromPiece = null;
        this.lastPieceToLocation = null;
        this.lastPieceFromLocation = null;
        if (delegate != null) {
            delegate.onTurnChange(turnColor);
        }
    }

    public boolean isUndoAvailable() {
        return this.lastPieceToLocation != null;
    }

    public void setDelegate(ChessGameControllerDelegate delegate) {
        this.delegate = delegate;
    }
}

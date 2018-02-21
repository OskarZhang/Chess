package Chess.Models;

import Chess.Types.PieceType;
import Chess.Types.PlayerColor;

import java.util.ArrayList;
import java.util.Arrays;

// Board manages the board, check for rule compliance.
public class Board {
    private Piece[][] boardData;

    /** Board constructor
     * @param serializer Serializer have 192 characters total, every 3 bits represent a tile on the board. '#' marks the start of a tile, followed by a
     *                   letter such as 'K' representing the type of piece, usually encoded with the piece's first letter, with the excepyion of 'H' for
     *                   Knight. The last bit is a number representing color, '1' for black, '0' for white, '2' for empty. Eg. #H0#R0#K0....
     * **/
    public Board(String serializer) {
        assert serializer.length() == 192;
        assert serializer.charAt(0) == '#';
        boardData = new Piece[8][8];
        for(int i = 0; i < 192; i+=3) {
            char pieceBit = serializer.charAt(i+1);
            char directionBit = serializer.charAt(i+2);
            int row = (i/3 ) / 8;
            int col = (i /3) - row * 8;
            if (pieceBit != 'E') {
                PlayerColor color = directionBit == '0' ? PlayerColor.WHITE : PlayerColor.BLACK;
                boardData[row][col] = new Piece(pieceBit, color);
            }
        }
    }

    /** Prints the board. For debug use.
     * **/
    public void printBoard() {
        printBoard(this.boardData);
    }

    private void printBoard(Piece[][] boardData) {
        for(int i = 0; i < boardData.length; i++) {
            Piece[] row = boardData[i];
            for(int j = 0; j < row.length; j++) {
                if (row[j] != null) {
                    System.out.print(row[j].type.toString().charAt(0));
                } else {
                    System.out.print("E");
                }
            }
            System.out.println();
        }
    }

    /** Returns the name of the piece at given coordinate
     * **/
    public String getPieceNameAtCoordinate(Coordinate coord) {
        return this.boardData[coord.row][coord.column].type.toString();
    }

    public Piece getPieceAtCoordinate(Coordinate coord) {
        return this.boardData[coord.row][coord.column];
    }

    public void setPieceAtCoordinate(Piece piece, Coordinate coord) {
        boardData[coord.row][coord.column] = piece;
    }

    /** Compute the eligible spots a piece can move from a given location
     * @param pieceLocation the coordinate of the piece
     * **/
    public Coordinate[] computeReachableMoves(Coordinate pieceLocation) {
        return computeReachableMoves(pieceLocation, false, this.boardData);
    }

    /** Compute the eligible spots a piece can move from a given location
     * @param pieceLocation the coordinate of the piece.
     * @param isSimulated flag for simulation run, if turned on, it will not run checks for king for each move.
     * @param boardData the board data in this computation.
     * @return an array of coordinates eligible for moves
     * **/
    private Coordinate[] computeReachableMoves(Coordinate pieceLocation, boolean isSimulated, Piece[][] boardData) {
        Piece piece = boardData[pieceLocation.row][pieceLocation.column];
        ArrayList<Coordinate> validMoveLocations = new ArrayList<Coordinate>();
        assert piece != null;
        if (!isSimulated) {
            System.out.println("Current piece: " + piece.type.toString() + " at (" + pieceLocation.row + ", " + pieceLocation.column + ")");
        }

        for (MoveType moveType : piece.moveTypes) {
            ArrayList<Coordinate> validMoves = scanMoves(pieceLocation, moveType, isSimulated, boardData);
            validMoveLocations.addAll(validMoves);
        }
        Coordinate[] arrayHolder = new Coordinate[validMoveLocations.size()];
        return validMoveLocations.toArray(arrayHolder);
    }

    /** Internal methods for scanning the board in the search of spots that the current piece can move to.
     */
    private ArrayList<Coordinate> scanMoves(Coordinate pieceLocation, MoveType moveType, boolean isSimulated, Piece[][] boardData) {
        ArrayList<Coordinate> validMoveLocations = new ArrayList<Coordinate>();
        Piece sourcePiece = boardData[pieceLocation.row][pieceLocation.column];
        assert  sourcePiece != null;
        Coordinate[] directionFactors = moveType.getDirectionFactors(sourcePiece.color);
        for (Coordinate directionFactor : directionFactors ) {
            Coordinate nextLocation = pieceLocation.getNextCoordinate(directionFactor);
            while (nextLocation.row < 8 && nextLocation.row >= 0 &&
                    nextLocation.column < 8 && nextLocation.column >= 0) {
                Coordinate currentLocation = nextLocation;
                Piece curPiece = boardData[currentLocation.row][currentLocation.column];
                if (curPiece == null && moveType == MoveType.SIDE_ATTACK) {
                    // nothing to do here for a sad pawn.
                    break;
                } else if (curPiece == null) {
                    // the sky is limit.. the piece can keep move further.
                    nextLocation = currentLocation.getNextCoordinate(directionFactor);
                } else if (sourcePiece.color == curPiece.color ||
                        moveType == MoveType.UP_ONCE ||
                        moveType == MoveType.UP_TWICE) {
                    // the piece on the current player's side is in the way, nothing to do here for the piece of same color or for pawns of any color.
                    break;
                }
                if (isSimulated) {
                    // we only need to check one level deep for checkmate
                    validMoveLocations.add(currentLocation);
                } else {
                    Piece[][] simulatedBoardData = getSimulatedBoardDataWithMove(pieceLocation, currentLocation);
                    if (!isKingInCheck(sourcePiece.color, simulatedBoardData)) {
                        validMoveLocations.add(currentLocation);
                    } else {
                        break;
                    }
                }
                if (sourcePiece.rangeType == RangeType.ONE_MOVE || curPiece != null) {
                    // For ONE_MOVE type like pawns, knights, they can't move more than one step
                    // or if there is a piece from the opposing side in the way, we cannot move further
                    break;
                }

            }
        }
        return validMoveLocations;
    }

    /**
     * Moves piece from one location to another. If there exists an opponent's piece at destination, KILL.
     * @param toLocation the location of the source
     * @param fromLocation the destination location
     */
    public void moveTo(Coordinate toLocation, Coordinate fromLocation) {
        Piece destinationPiece = boardData[toLocation.row][toLocation.column];
        if (destinationPiece != null) {
            //broadcast kill
        }
        Piece sourcePiece = boardData[fromLocation.row][fromLocation.column];
        assert  sourcePiece != null;
        boardData[toLocation.row][toLocation.column] = sourcePiece;
        boardData[fromLocation.row][fromLocation.column] = null;

        if (sourcePiece.type == PieceType.PAWN) {
            sourcePiece.setHasMadeFirstMoveForPawn();
        }
    }

    /** Internal methods for checking if the king is in check.
     */
    private boolean isKingInCheck(PlayerColor kingColor, Piece[][] newBoardData) {
        Coordinate kingLocation = null;
        for (int row = 0; row < newBoardData.length; row++) {
            for (int col = 0; col < newBoardData[row].length; col++) {
                Piece piece = newBoardData[row][col];
                if (piece != null && piece.type == PieceType.KING && piece.color == kingColor) {
                    kingLocation = new Coordinate(row, col);
                }
            }
        }
        assert kingLocation != null;
        for (int row = 0; row < newBoardData.length; row++) {
            for (int col = 0; col < newBoardData[row].length; col++) {
                Piece piece = newBoardData[row][col];
                if (piece != null && piece.color != kingColor) {
                   Coordinate[] potentialMoves = computeReachableMoves(new Coordinate(row, col), true, newBoardData);
                   if (Arrays.asList(potentialMoves).contains(kingLocation)) {
                       return true;
                   }
                }
            }
        }
        return false;
    }


    /**
     * Checks if the king is in check
     * @param kingColor
     * @return a boolean indicating if the king is in check
     */
    public boolean isKingInCheck(PlayerColor kingColor) {
        return isKingInCheck(kingColor, this.boardData);
    }

    public boolean isPlayerInStalemate(PlayerColor color) {
        return !isKingInCheck(color) && getCountForReachableMoves(color) == 0;
    }

    /**
     * Checks if a player has won the game
     * @param color
     * @return a boolean indicating if the player has won
     */
    public boolean isPlayerWinning(PlayerColor color) {
        int validMovesCount = getCountForReachableMoves(color.oppositeColor());
        return validMovesCount == 0;
    }

    private int getCountForReachableMoves(PlayerColor color) {
        int validMovesCount = 0;
        for (int row = 0; row < this.boardData.length; row++) {
            for (int col = 0; col < this.boardData[row].length; col++) {
                Piece piece = this.boardData[row][col];
                if (piece != null && piece.color == color) {
                    Coordinate[] potentialMoves = computeReachableMoves(new Coordinate(row, col));
                    if (potentialMoves.length > 0) {
                        for (Coordinate move : potentialMoves) {
                            System.out.println("r: " + move.row + ", c: " + move.column);
                        }
                    }
                    validMovesCount += potentialMoves.length;
                }
            }
        }
        return validMovesCount;
    }

    /** Internal method for generating a simulated board for detecting checks for king.
     */
    private Piece[][] getSimulatedBoardDataWithMove(Coordinate fromLocation, Coordinate toLocation) {
        Piece[][] newBoardData = java.util.Arrays.stream(boardData).map(el -> el.clone()).toArray($ -> boardData.clone());
        Piece sourcePiece = newBoardData[fromLocation.row][fromLocation.column];
        newBoardData[toLocation.row][toLocation.column] = sourcePiece;
        newBoardData[fromLocation.row][fromLocation.column] = null;
        return newBoardData;
    }

}

/**
 * MoveType: The type of moves a piece can perform on the board.
 * Each type of pieces can have multiple types of moves.
 */
enum MoveType {
    STRAIGHT, DIAGONAL, UP_ONCE, SIDE_ATTACK, KNIGHT, UP_TWICE, ELEPHANT, YOLO;
    public Coordinate[] getDirectionFactors(PlayerColor color) {
        if (this == MoveType.DIAGONAL) {
            return new Coordinate[] {
                    new Coordinate(1,1),new Coordinate(1,-1),
                    new Coordinate(-1,1),new Coordinate(-1,-1)};
        } else if (this == MoveType.STRAIGHT){
            return new Coordinate[] {
                    new Coordinate(1,0),new Coordinate(0,1),
                    new Coordinate(-1,0),new Coordinate(0,-1)};
        } else if (this == MoveType.UP_ONCE) {
            int verticalDirection = color == PlayerColor.WHITE ? -1 : 1;
            return new Coordinate[] {new Coordinate(verticalDirection,0)};
        } else if (this == MoveType.KNIGHT){
            return new Coordinate[] {
                    new Coordinate(2,1),new Coordinate(2,-1),
                    new Coordinate(-2,1), new Coordinate(-2,-1),
                    new Coordinate(1,2),new Coordinate(1,-2),
                    new Coordinate(-1,2),new Coordinate(-1,-2)};
        } else if (this == MoveType.SIDE_ATTACK) {
            int verticalDirection = color == PlayerColor.WHITE ? -1 : 1;
            return new Coordinate[] {
                    new Coordinate(verticalDirection,1), new Coordinate(verticalDirection,-1)};
        } else if (this == MoveType.UP_TWICE){
            int verticalDirection = color == PlayerColor.WHITE ? -2 : 2;
            return new Coordinate[] {new Coordinate(verticalDirection,0)};
        } else if (this == MoveType.ELEPHANT) {
            return new Coordinate[]{
                    new Coordinate(1,1), new Coordinate(1,-1),
                    new Coordinate(-1,1), new Coordinate(-1,-1),
                    new Coordinate(-1,0), new Coordinate(1,0),
                    new Coordinate(0,1), new Coordinate(0,-1),
            };
        } else {
            assert false;
            return new Coordinate[]{};
        }
    }
}
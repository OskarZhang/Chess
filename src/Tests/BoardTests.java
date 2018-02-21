package Tests;

import Chess.Models.Board;
import Chess.Models.Coordinate;
import Chess.Types.PlayerColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

public class BoardTests extends TestCase{
    public void testConstruction() {
        Board board = new Board(getStartingBoard());
        assertEquals("KING", board.getPieceNameAtCoordinate(new Coordinate(7,4)));
    }

    public void testComputeReachableMovesForPawns() {
        Board board = new Board(getBoardWithPawns());
        Coordinate[] reachableMoves = board.computeReachableMoves(new Coordinate(6,3));
        reachableMoves = sortCoordinateArray(reachableMoves);
        Coordinate[] correctReachableMoves = new Coordinate[]{new Coordinate(4,3), new Coordinate(5,3), new Coordinate(5,4)};
        assert Arrays.deepEquals(correctReachableMoves, reachableMoves);
    }

    public void testComputeReachableMovesForKing() {
        Board board = new Board(getBoardWithKing());
        Coordinate[] reachableMoves = board.computeReachableMoves(new Coordinate(4,3));
        Coordinate[] correctReachableMoves = new Coordinate[]{
                new Coordinate(3,2), new Coordinate(3,3),
                new Coordinate(4,4), new Coordinate(5,2),
                new Coordinate(5,3)};
        reachableMoves = sortCoordinateArray(reachableMoves);
        assert Arrays.deepEquals(correctReachableMoves, reachableMoves);
    }

    public void testComputeReachableMovesForQueen() {
        Board board = new Board(getBoardWithQueen());
        Coordinate[] reachableMoves = board.computeReachableMoves(new Coordinate(4,3));
        List<Coordinate> correctReachableMovesList = new ArrayList<Coordinate>();
        for (int i = 0; i < 8; i ++) {
            if (i != 4) {
                correctReachableMovesList.add(new Coordinate(i,3));
                if (i != 0) correctReachableMovesList.add(new Coordinate(i,i-1)); // doesn't exist
            }
            if (i != 3) {
                if (i != 7) correctReachableMovesList.add(new Coordinate(4, i)); // there is a king
                correctReachableMovesList.add(new Coordinate(7-i,i));
            }
        }
        correctReachableMovesList.sort((Coordinate c1, Coordinate c2)-> (c1.row * 8 + c1.column - c2.row * 8 - c2.column));
        reachableMoves = sortCoordinateArray(reachableMoves);
        assert Arrays.deepEquals(correctReachableMovesList.toArray(), reachableMoves);
    }

    public void testComputeReachableMovesForKnight() {
        Board board = new Board(getBoardWithKnight());
        Coordinate[] reachableMoves = board.computeReachableMoves(new Coordinate(2,3));
        Coordinate[] correctReachableMoves = new Coordinate[]{
                new Coordinate(0,2), new Coordinate(0,4),
                new Coordinate(1,1), new Coordinate(1,5),
                new Coordinate(3,1), new Coordinate(3,5),
                new Coordinate(4,2), new Coordinate(4,4)};
        reachableMoves = sortCoordinateArray(reachableMoves);
        assert Arrays.deepEquals(correctReachableMoves, reachableMoves);
    }

    public void testComputeReachableMovesForBishop() {
        Board board = new Board(getBoardWithBishop());
        Coordinate[] reachableMoves = board.computeReachableMoves(new Coordinate(4,3));
        List<Coordinate> correctReachableMovesList = new ArrayList<Coordinate>();
        for (int i = 0; i < 8; i ++) {
            if (i != 4 && i != 0)
                correctReachableMovesList.add(new Coordinate(i,i-1)); // doesn't exist
            if (i != 3)
                correctReachableMovesList.add(new Coordinate(7-i,i));
        }
        correctReachableMovesList.sort((Coordinate c1, Coordinate c2)-> (c1.row * 8 + c1.column - c2.row * 8 - c2.column));
        reachableMoves = sortCoordinateArray(reachableMoves);
        assert Arrays.deepEquals(correctReachableMovesList.toArray(), reachableMoves);
    }

    public void testComputeReachableMovesForRook() {
        Board board = new Board(getBoardWithRook());
        Coordinate[] reachableMoves = board.computeReachableMoves(new Coordinate(4,3));
        List<Coordinate> correctReachableMovesList = new ArrayList<Coordinate>();
        for (int i = 0; i < 8; i ++) {
            if (i != 4) correctReachableMovesList.add(new Coordinate(i,3));
            if (i != 3) correctReachableMovesList.add(new Coordinate(4,i));
        }
        correctReachableMovesList.sort((Coordinate c1, Coordinate c2)-> (c1.row * 8 + c1.column - c2.row * 8 - c2.column));
        reachableMoves = sortCoordinateArray(reachableMoves);
        assert Arrays.deepEquals(correctReachableMovesList.toArray(), reachableMoves);
    }

    public void testMoveTo() {
        Board board = new Board(getRandomBoard());
        Coordinate sourceLocation = new Coordinate(7,5); //bishop
        String correctPieceType = board.getPieceNameAtCoordinate(sourceLocation);
        Coordinate[] reachableMoves = board.computeReachableMoves(sourceLocation);
        Coordinate lastMove = reachableMoves[reachableMoves.length - 1];
        board.moveTo(lastMove,sourceLocation);
        assert board.getPieceNameAtCoordinate(lastMove).equals(correctPieceType);
    }

    public void testIsPlayerWinning() {
        Board board = new Board(getFoolsMate());
        assert board.isPlayerWinning(PlayerColor.BLACK);

        board = new Board(getCheckmatedBoard());
        assert board.isPlayerWinning(PlayerColor.WHITE);
    }

    public void testIsPlayerInStalemate() {
        Board board = new Board(getStalematedBoard());
        board.printBoard();
        assert board.isPlayerInStalemate(PlayerColor.BLACK);
    }


    static String getStartingBoard() {
        String blackFirstLine = "#R1#H1#B1#Q1#K1#B1#H1#R1";
        String blackPawns=      "#P1#P1#P1#P1#P1#P1#P1#P1";
        String emptyLine =      "#E2#E2#E2#E2#E2#E2#E2#E2";
        String whitePawns=      "#P0#P0#P0#P0#P0#P0#P0#P0";
        String whiteFirstLine = "#R0#H0#B0#Q0#K0#B0#H0#R0";
        String startingBoardSerializer = blackFirstLine + blackPawns + emptyLine + emptyLine + emptyLine + emptyLine + whitePawns + whiteFirstLine;
        return startingBoardSerializer;
    }

    static String getRandomBoard() {
        String blackFirstLine = "#R1#H1#B1#Q1#K1#B1#H1#R1";
        String blackPawns=      "#P1#P1#P1#E2#P1#P1#P1#P1";
        String line2 =          "#E2#E2#E2#P1#E2#E2#E2#E2";
        String emptyLine =      "#E2#E2#E2#E2#E2#E2#E2#E2";
        String line4 =          "#E2#E2#E2#E2#E2#E2#E2#E2";
        String line5 =          "#E2#E2#E2#E2#P0#P0#E2#E2";
        String whitePawns =     "#P0#P0#P0#P0#E2#E2#P0#P0";
        String whiteFirstLine = "#R0#H0#B0#Q0#K0#B0#H0#R0";
        String boardSerializer = blackFirstLine + blackPawns + line2 + emptyLine + line4 + line5 + whitePawns + whiteFirstLine;
        return boardSerializer;
    }

    static String getBoardWithPawns() {
        String emptyLine = "#E2#E2#E2#E2#E2#E2#E2#E2";
        String line5 =     "#E2#E2#E2#E2#P1#E2#E2#E2";
        String line6 =     "#K1#E2#E2#P0#E2#E2#E2#K0";
        String boardSerializer = emptyLine + emptyLine + emptyLine + emptyLine + emptyLine + line5 + line6 + emptyLine;
        return boardSerializer;
    }

    static String getBoardWithKing() {
        String emptyLine = "#E2#E2#E2#E2#E2#E2#E2#E2";
        String line4 =     "#K1#E2#E2#K0#R1#E2#E2#E2";
        String boardSerializer = emptyLine + emptyLine + emptyLine + emptyLine + line4 + emptyLine + emptyLine + emptyLine;
        return boardSerializer;
    }

    static String getBoardWithQueen() {
        String line1 =     "#K1#E2#E2#E2#E2#E2#E2#E2";
        String emptyLine = "#E1#E2#E2#E2#E2#E2#E2#E2";
        String line4 =     "#E2#E2#E2#Q0#E0#E2#E2#K0";
        String boardSerializer = line1 + emptyLine + emptyLine + emptyLine + line4 + emptyLine + emptyLine + emptyLine;
        return boardSerializer;
    }

    static String getBoardWithKnight() {
        String line0 =     "#E2#E2#E2#E2#K1#E2#E2#K0";
        String line2 =     "#E2#E2#E2#H0#E2#E2#E2#E2";
        String emptyLine = "#E2#E2#E2#E2#E2#E2#E2#E2";
        String boardSerializer = line0 + emptyLine + line2+ emptyLine + emptyLine + emptyLine + emptyLine + emptyLine;
        return boardSerializer;
    }

    static String getBoardWithElephant() {
        String line0 =     "#E2#E2#E2#E2#K1#E2#E2#K0";
        String line2 =     "#E2#E2#E2#X0#E2#E2#E2#E2";
        String emptyLine = "#E2#E2#E2#E2#E2#E2#E2#E2";
        String boardSerializer = line0 + emptyLine + line2+ emptyLine + emptyLine + emptyLine + emptyLine + emptyLine;
        return boardSerializer;
    }

    static String getBoardWithBishop() {
        String line0 = "#K1#E2#E2#E2#E2#E2#E2#R1";
        String line4 =     "#E2#E2#E2#B0#E2#E2#K0#E2";
        String emptyLine = "#E2#E2#E2#E2#E2#E2#E2#E2";
        String boardSerializer = line0 + emptyLine + emptyLine + emptyLine + line4 + emptyLine + emptyLine + emptyLine;
        return boardSerializer;
    }

    static String getBoardWithRook() {
        String line0 = "#K0#E2#E2#B1#E2#E2#E2#K1";
        String line4 =     "#E2#E2#E2#R0#E2#E2#E2#E2";
        String emptyLine = "#E2#E2#E2#E2#E2#E2#E2#E2";
        String boardSerializer = line0 + emptyLine + emptyLine + emptyLine + line4 + emptyLine + emptyLine + emptyLine;
        return boardSerializer;
    }

    static String getFoolsMate() {
        String blackFirstLine = "#R1#H1#B1#E2#K1#B1#H1#R1";
        String blackPawns =     "#P1#P1#P1#P1#E2#P1#P1#P1";
        String emptyLine =      "#E2#E2#E2#E2#E2#E2#E2#E2";
        String line3 =          "#E2#E2#E2#E2#P1#E2#E2#E2";
        String line4 =          "#E2#E2#E2#E2#E2#E2#P0#Q1";
        String line5 =          "#E2#E2#E2#E2#E2#P0#E2#E2";
        String whitePawns=      "#P0#P0#P0#P0#P0#E2#E2#P0";
        String whiteFirstLine = "#R0#H0#B0#Q0#K0#B0#H0#R0";

        String startingBoardSerializer = blackFirstLine + blackPawns + emptyLine + line3 + line4 + line5 + whitePawns + whiteFirstLine;
        return startingBoardSerializer;
    }

    static String getCheckmatedBoard() {
        String emptyLine = "#E2#E2#E2#E2#E2#E2#E2#E2";
        String kingLine = "#E2#E2#E2#E2#E2#K0#E2#K1";
        String rookLine = "#E2#E2#E2#E2#E2#E2#E2#R0";
        String boardSerializer = emptyLine + emptyLine + emptyLine + kingLine + emptyLine + emptyLine + emptyLine + rookLine;
        return boardSerializer;
    }

    static String getKingInCheckBoard() {
        String blackFirstLine = "#R1#H1#B1#E2#K1#B1#H1#R1";
        String blackPawns =     "#P1#P1#P1#P1#E2#P1#P1#P1";
        String emptyLine =      "#E2#E2#E2#E2#E2#E2#E2#E2";
        String line3 =          "#E2#E2#E2#E2#P1#E2#E2#E2";
        String line4 =          "#E2#E2#E2#E2#E2#E2#P0#Q1";
        String line5 =          "#E2#E2#E2#E2#E2#E2#E2#P0";
        String whitePawns=      "#P0#P0#P0#P0#P0#P0#E2#E2";
        String whiteFirstLine = "#R0#H0#B0#Q0#K0#B0#H0#R0";
        String boardSerializer = blackFirstLine + blackPawns + emptyLine + line3 + line4 + line5 + whitePawns + whiteFirstLine;
        return boardSerializer;
    }

    static String getStalematedBoard() {
        String line0 =      "#E2#E2#E2#E2#E2#E2#E2#K1";
        String emptyLine =  "#E2#E2#E2#E2#E2#E2#E2#E2";
        String line1 =      "#E2#E2#E2#E2#E2#K0#E2#E2";
        String line2 =      "#E2#E2#E2#E2#E2#E2#Q0#E2";
        String boardSerializer = line0 + line1 + line2 + emptyLine + emptyLine + emptyLine + emptyLine + emptyLine;
        return boardSerializer;
    }

    static Coordinate[] sortCoordinateArray(Coordinate[] coordinates) {
        List<Coordinate> coordinateList = Arrays.asList(coordinates);
        coordinateList.sort((Coordinate c1, Coordinate c2)-> (c1.row * 8 + c1.column - c2.row * 8 - c2.column));
        return (Coordinate[])coordinateList.toArray();
    }

}

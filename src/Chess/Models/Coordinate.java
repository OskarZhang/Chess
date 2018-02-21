package Chess.Models;

/**
 * The coordinate used on the board. (0,0) starts at top-left corner and (7,7) ends at the bottom-right corner.
 */
public final class Coordinate {
    public int row;
    public int column;
    public Coordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Computes the next coordinate in the direction.
     * @param directionFactor of the same type Coordinate, it defines the direction the user wants to explore from the current location. Eg. (1,0) is downward, (0,1) is rightward
     * @return the result coordinate
     */
    Coordinate getNextCoordinate(Coordinate directionFactor) {
        return new Coordinate(this.row + directionFactor.row, this.column + directionFactor.column);
    }

    @Override
    public boolean equals(Object obj) {
        return this.row == ((Coordinate)obj).row && this.column == ((Coordinate)obj).column;
    }
}
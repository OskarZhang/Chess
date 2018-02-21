package Chess.Models;
import Chess.Types.PieceType;
import Chess.Types.PlayerColor;

/**
 * A piece has the following attributes:
 * MoveType: the types of move it can perform on the board
 * PieceType: One of the six chess pieces
 * RangeType: Either UNLIMITED or ONE_MOVE, eg. King is ONE_MOVE and Queen is UNLIMITED.
 * PlayerColor: the color of the piece
 */
public class Piece {
    public MoveType[] moveTypes;
    public PieceType type;
    public RangeType rangeType = RangeType.UNLIMITED;
    public PlayerColor color;

    /**
     * The constructor for Piece class.
     * @param serializerCode The code
     * @param color color of the piece
     */
    Piece(char serializerCode,PlayerColor color) {
        this.color = color;
        switch (serializerCode) {
            case 'K':
                this.type = PieceType.KING;
                this.moveTypes = new MoveType[]{MoveType.STRAIGHT, MoveType.DIAGONAL};
                this.rangeType = RangeType.ONE_MOVE;
                break;
            case 'Q':
                this.type = PieceType.QUEEN;
                this.moveTypes = new MoveType[]{MoveType.STRAIGHT, MoveType.DIAGONAL};
                break;
            case 'B':
                this.type = PieceType.BISHOP;
                this.moveTypes = new MoveType[]{MoveType.DIAGONAL};
                break;
            case 'H':
                this.type = PieceType.KNIGHT;
                this.rangeType = RangeType.ONE_MOVE;
                this.moveTypes = new MoveType[]{MoveType.KNIGHT};
                break;
            case 'R':
                this.type = PieceType.ROOK;
                this.moveTypes = new MoveType[]{MoveType.STRAIGHT};
                break;
            case 'P':
                this.type = PieceType.PAWN;
                this.rangeType = RangeType.ONE_MOVE;
                this.moveTypes = new MoveType[]{MoveType.UP_ONCE, MoveType.SIDE_ATTACK, MoveType.UP_TWICE};
                break;
            case 'X':
                this.type = PieceType.ELEPHANT;
                this.rangeType = RangeType.ONE_MOVE;
                this.moveTypes = new MoveType[]{MoveType.ELEPHANT};
                break;
            case 'Y':
                this.type = PieceType.DRUNKEN_KNIGHT;
                this.rangeType = RangeType.UNLIMITED;
                this.moveTypes = new MoveType[]{MoveType.KNIGHT};
                break;

                default:
                    assert false;
                    break;
        }
    }

    public Piece(Piece another) {
        this.moveTypes = another.moveTypes;
        this.type = another.type;
        this.rangeType = another.rangeType;
        this.color = another.color;
    }

    void setHasMadeFirstMoveForPawn() {
        assert this.type == PieceType.PAWN;
        this.moveTypes = new MoveType[]{MoveType.UP_ONCE, MoveType.SIDE_ATTACK};
    }
}


enum RangeType {
    ONE_MOVE, UNLIMITED
}

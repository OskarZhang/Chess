package Chess.Types;

import java.awt.*;

public enum PlayerColor {
    BLACK, WHITE;
    public PlayerColor oppositeColor() {
        return (this == PlayerColor.WHITE) ? PlayerColor.BLACK : PlayerColor.WHITE;
    }
    public Color getSwingColor() {
        return (this == PlayerColor.WHITE) ? Color.white : Color.black;
    }
}

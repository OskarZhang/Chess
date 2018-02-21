package Chess.Controllers;

import Chess.Types.PlayerColor;

public interface ChessGameControllerDelegate {
    public void onTurnChange(PlayerColor newColor);
    public void onStalemate();
    public void onWin(PlayerColor winnerColor);
}

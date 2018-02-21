package Chess.Views;

import Chess.Models.Coordinate;

public interface BoardPanelDelegate {
    public void onClickTile(Coordinate coord);
}

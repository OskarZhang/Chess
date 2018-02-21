package Chess.Views;

public interface GameControlPanelDelegate {
    public void onClickStart(String blackPlayerName, String whitePlayerName);
    public void onClickRestart();
    public void onClickReset();
    public void onClickForfeit();
    public void onClickUndo();
    public void onClickFunky();
}

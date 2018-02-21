package Chess.Views;

import Chess.Models.Board;
import Chess.Models.Coordinate;
import Chess.Models.Piece;
import Chess.Types.PlayerColor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;


/**
 * The view component for the game.
 */
public class BoardPanel extends JPanel {
    public static Dimension TILE_SIZE = new Dimension(64,64);
    public static int MENU_BAR_HEIGHT = 18;
    static Color darkBackgroundColor = new Color(175,138,104);
    static Color lightBackgroundColor = new Color(237,217,185);
    Map<String, Image> chessPieceImageMap = new HashMap<String, Image>();
    JButton[][] pieceButtons;
    public BoardPanelDelegate delegate;

    /**
     * Initializer for BoardPanel, a JPanel instance.
     * Upon initialization, boardPanel prepares the image assets and renders the chess board and
     * the pieces according to the current state of boardModel.
     * @param boardModel the board data model of type Board, used to load and retrieve game state.
     */
    public BoardPanel(Board boardModel) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.setPreferredSize(new Dimension( TILE_SIZE.width * 8,TILE_SIZE.height * 8 + MENU_BAR_HEIGHT));
        createSlicedImages();
        renderPieces(boardModel);
    }

    /**
     * This will highlight the tile with appropriate color
     * @param coords the list of coordinates that will be highlited
     * @param color the color for highlighting
     */
    public void setTileHighlighted(Coordinate[] coords, PlayerColor color) {
        removeHighlightedBorderFromAllTiles();
        for (Coordinate coord : coords) {
            JButton currentButton = pieceButtons[coord.row][coord.column];
            assert currentButton != null;
            Border coloredBorder = BorderFactory.createLineBorder(color.getSwingColor(), 4);
            currentButton.getBorder().getClass();
            currentButton.setBorder(coloredBorder);
        }
    }

    /**
     * One call to remove all highlights on the board
     */
    public void removeHighlightedBorderFromAllTiles() {
        for (JButton[] row : pieceButtons) {
            for (JButton button: row) {
                Border emptyBorder = BorderFactory.createEmptyBorder();
                button.setBorder(emptyBorder);
            }
        }
    }

    public void setDelegate(BoardPanelDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Update the move on the UI.
     */
    public void updateViewForMove(Coordinate toLocation, Coordinate fromLocation, Piece sourcePiece) {
        removeHighlightedBorderFromAllTiles();
        setTileEmpty(fromLocation);
        setTilePiece(toLocation, sourcePiece);
    }

    /**
     * Update the move for undo on the UI
     * This method is similar to the method above, but with one additional parameter because there are two pieces involved potentially.
     */
    public void updateViewForUndo(Coordinate toLocation, Piece toPiece, Coordinate fromLocation, Piece fromPiece) {
        removeHighlightedBorderFromAllTiles();
        if (fromPiece == null) {
            setTileEmpty(fromLocation);
        } else {
            setTilePiece(fromLocation, fromPiece);
        }
        if (toPiece == null) {
            setTileEmpty(toLocation);
        } else {
            setTilePiece(toLocation, toPiece);
        }

    }
    /**
     * Private helper method for rendering chess pieces.
     */
    private void renderPieces(Board boardModel) {
        pieceButtons = new JButton[8][8];
        for (int i = 0; i < pieceButtons.length; i++) {
            boolean isLightColor = i % 2 == 0;
            for (int j = 0; j < pieceButtons[i].length; j++) {
                Piece piece = boardModel.getPieceAtCoordinate(new Coordinate(i,j));
                JButton button = new JButton();
                if (piece == null) {
                    ImageIcon clearIcon = new ImageIcon(
                            new BufferedImage(TILE_SIZE.width, TILE_SIZE.height, BufferedImage.TYPE_INT_ARGB));
                    button.setIcon(clearIcon);
                } else {
                    String pieceColor = piece.color.toString();
                    String pieceName = piece.type.toString();
                    Image image = chessPieceImageMap.get(pieceColor + pieceName);
                    ImageIcon icon = new ImageIcon(image);
                    button.setIcon(icon);
                }
                pieceButtons[i][j] = button;
                Border emptyBorder = BorderFactory.createEmptyBorder();
                button.setBorder(emptyBorder);
                button.setMargin(new Insets(0,0,0,0));
                button.setOpaque(true);
                button.setPreferredSize(new Dimension(TILE_SIZE.width,TILE_SIZE.height));
                button.setBackground(isLightColor ? lightBackgroundColor : darkBackgroundColor);
                final Coordinate coord = new Coordinate(i,j);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (delegate != null) {
                            delegate.onClickTile(coord);
                        }
                    }
                });
                this.add(button);
                isLightColor = !isLightColor;
            }
        }
    }

    private void setTileEmpty(Coordinate coord) {
        ImageIcon clearIcon = new ImageIcon(
                new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
        pieceButtons[coord.row][coord.column].setIcon(clearIcon);
    }

    private void setTilePiece(Coordinate coord, Piece piece) {
        System.out.println("setting " + coord.row + coord.column);
        String pieceColor = piece.color.toString();
        String pieceName = piece.type.toString();
        Image image = chessPieceImageMap.get(pieceColor + pieceName);
        ImageIcon icon = new ImageIcon(image);
        pieceButtons[coord.row][coord.column].setIcon(icon);
    }

    /**
     * Prepare the image asset for rendering.
     * Referenced from https://stackoverflow.com/questions/21077322/create-a-chess-board-with-jpanel
     */
    private void createSlicedImages() {
        try {
            String[] piecesName = new String[] {"PAWN","BISHOP","KNIGHT","ROOK","QUEEN","KING"};
            BufferedImage chessPieceImage = ImageIO.read(new File("Assets/chesspieces.png"));
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 6; col++) {
                    String color = row == 0 ? "WHITE" : "BLACK";
                    Image icon = chessPieceImage.getSubimage(col * TILE_SIZE.width, row * TILE_SIZE.height, TILE_SIZE.width, TILE_SIZE.height);
                    chessPieceImageMap.put(color + piecesName[col], icon);
                    if (piecesName[col].equals("PAWN")) {
                        //temp for elephant and yolo todo: make icons

                    }
                }
            }
            Image whiteElephantIcon = ImageIO.read(new File("Assets/white_elephant.png"));
            Image blackElephantIcon = ImageIO.read(new File("Assets/black_elephant.png"));
            Image whiteDrunken = ImageIO.read(new File("Assets/white_drunken_knight.png"));
            Image blackDrunken= ImageIO.read(new File("Assets/black_drunken_knight.png"));
            chessPieceImageMap.put("WHITE" + "DRUNKEN_KNIGHT", whiteDrunken);
            chessPieceImageMap.put("BLACK" + "DRUNKEN_KNIGHT", blackDrunken);
            chessPieceImageMap.put("WHITE" + "ELEPHANT", whiteElephantIcon);
            chessPieceImageMap.put("BLACK" + "ELEPHANT", blackElephantIcon);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}


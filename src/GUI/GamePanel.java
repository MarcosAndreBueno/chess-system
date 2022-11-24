package GUI;

import javax.swing.*;
import java.awt.*;

import static GUI.GUIBoard.*;
import static GUI.MouseInputs.*;

public class GamePanel extends JPanel {
    private MouseInputs mouseInputs;

    public GamePanel() {
        mouseInputs = new MouseInputs(this);
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    public void render(Graphics g) {
        drawBoard(g);
        drawPieces(g);
        if (guiPromotedPiece != null && clickedPromotedPiece == null)
            drawPromotionPiecesList(g);
        else if (guiPromotedPiece != null && clickedPromotedPiece != null)
            drawChoosedPromotedPiece(g);
    }

    public void drawBoard(Graphics g) {
        //draw tabuleiro
        boolean white = true;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (white) {
                    g.setColor(new Color(235, 235, 208));
                } else {
                    g.setColor(new Color(119, 148, 85));
                }
                g.fillRect(x * 64, y * 64, 64, 64);
                white = !white;
            }
            white = !white;
        }
    }

    public void drawPieces(Graphics g) {
        //draw peças
        for (GUIPiece p : ps) {
            int ind = 0;
            if (p.name.equalsIgnoreCase("king")) {
                ind = 0;
            }
            if (p.name.equalsIgnoreCase("queen")) {
                ind = 1;
            }
            if (p.name.equalsIgnoreCase("bishop")) {
                ind = 2;
            }
            if (p.name.equalsIgnoreCase("knight")) {
                ind = 3;
            }
            if (p.name.equalsIgnoreCase("rook")) {
                ind = 4;
            }
            if (p.name.equalsIgnoreCase("pawn")) {
                ind = 5;
            }
            if (!p.isWhite) {
                ind += 6;
            }
            g.drawImage(imgs[ind], p.x, p.y, this);
        }
    }

    public void drawPromotionPiecesList(Graphics g) {
        int idx = 1;
        if (!guiPromotedPiece.isWhite)
            idx = 7;

        for (GUIPiece p : piecesPromotion) {
            if (p.isWhite == guiPromotedPiece.isWhite) {
                g.drawImage(imgs[idx], p.x, p.y, this);
                idx += 1;
            }
        }
    }

    public void drawChoosedPromotedPiece(Graphics g) {
        //        System.out.println("printar: drawChoosedPromotedPiece " + piece.getName() + " " + piece.isWhite);
        GUIPiece piece = new GUIPiece(guiPromotedPiece.xp, guiPromotedPiece.yp, clickedPromotedPiece.isWhite, clickedPromotedPiece.name, ps);
        ps.add(piece);
        g.drawImage(imgs[getImageIndex(clickedPromotedPiece.name)], guiPromotedPiece.x, guiPromotedPiece.y, this);
    }
}

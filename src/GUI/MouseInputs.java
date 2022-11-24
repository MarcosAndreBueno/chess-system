package GUI;

import application.UI;
import boardgame.Position;
import chess.ChessPiece;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import static GUI.GUIBoard.*;

public class MouseInputs implements MouseListener, MouseMotionListener {
    GUIPosition source;
    GUIPosition target;
    public static GUIPiece clickedPiece, clickedPromotedPiece;
    public static GUIPiece selectedPiece;
    GUIChessMatch guiChessMatch;
    private GamePanel gamePanel;

    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        guiChessMatch = new GUIChessMatch(gamePanel);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedPiece != null) {
            selectedPiece.x = e.getX()-32;
            selectedPiece.y = e.getY()-32;
            gamePanel.repaint();
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        if (getPiece(e.getX()/64,e.getY()/64) != null)
//            System.out.println("printar: clicked piece: " + (getPiece(e.getX()/64,e.getY()/64).isWhite ? "white" : "black") + " " + getPiece(e.getX()/64, e.getY()/64).name);
        if (guiPromotedPiece != null) {
            clickedPromotedPiece = getPromotionPiece(e.getX() / 64, e.getY() / 64);
            guiChessMatch.setPromotedPieceInMatrix(clickedPromotedPiece);
            guiChessMatch.placePiece(new GUIPiece(clickedPromotedPiece.xp, clickedPromotedPiece.yp, clickedPromotedPiece.isWhite, clickedPromotedPiece.name, ps), new Position(guiPromotedPiece.yp, guiPromotedPiece.xp));
            gamePanel.repaint();
            guiChessMatch.resetPromotedPieces();
        }
        if (guiPromotedPiece == null)
            clickedPiece = getPiece(e.getX()/64, e.getY()/64);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (guiPromotedPiece == null || (guiPromotedPiece != null && clickedPromotedPiece != null)) {
            source = new GUIPosition(e.getX() / 64, e.getY() / 64);
            selectedPiece = getPiece(e.getX() / 64, e.getY() / 64);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (guiPromotedPiece == null || (guiPromotedPiece != null && clickedPromotedPiece != null)) {
            target = new GUIPosition(e.getX() / 64, e.getY() / 64);
            guiChessMatch.performMove(source, target);
            gamePanel.repaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

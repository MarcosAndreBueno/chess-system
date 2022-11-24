package GUI;

import application.UI;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.Color;

import javax.swing.*;
import javax.swing.plaf.nimbus.State;

import java.awt.*;

import static GUI.GUIBoard.*;
import static GUI.MouseInputs.clickedPromotedPiece;


public class GUIChessMatch {
    protected static ChessMatch chessMatch;
    private GamePanel gamePanel;
    private boolean check = false;

    public GUIChessMatch(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        chessMatch = new ChessMatch(this);
    }

    public void performMove(GUI.GUIPosition source, GUI.GUIPosition target){
        // from gui board to matrix
        source = source.toMatrixPosition();
        target = target.toMatrixPosition();
        chessMatch.performChessMove(new ChessPosition(source.getColumnChar(), source.getRow()),
                                    new ChessPosition(target.getColumnChar(), target.getRow()));
    }

    public void placePiece(GUIPiece piece, Position pos) {
//        System.out.println("place piece: " + (piece.isWhite ? "white" : "black") + " " + piece.name + " " + pos.getColumn() + " " + pos.getRow());
        if (piece != null) {
            piece.xp = (pos.getColumn());
            piece.yp = (pos.getRow());
            piece.x = piece.xp * 64;
            piece.y = piece.yp * 64;
        }
    }

    public void undoEnPassant(GUIPiece removedPiece, Position target, GUIPiece movedPiece, Position source){
        ps.add(removedPiece);
        placePiece(removedPiece, target);
//        ps.add(movedPiece);
        placePiece(movedPiece, source);
    }

    public GUIPiece kill(Position position){
        GUIPiece piece = GUIBoard.getPiece(position.getColumn(), position.getRow());
        if (piece != null) {
            guiPiecesKill(piece);
        }
        return piece;
    }

    public GUIPiece saveSourcePiece(Position position){
        GUIPiece piece = GUIBoard.getPiece(position.getColumn(),position.getRow());
        return piece;
    }

    public void undoMove(Position source, Position target, GUIPiece guiCapturedPiece) {
        if (chessMatch.isEnPassant()) {
            chessMatch.setEnPassant(false);
        }else {
            if (target != null) {
                GUIPiece targetPiece = GUIBoard.getPiece(target.getColumn(), target.getRow());
                GUIPiece sourcePiece = GUIBoard.getPiece(source.getColumn(), source.getRow());
                placePiece(targetPiece, target);
                placePiece(sourcePiece, source);
            } else {
                GUIPiece movedPiece = GUIBoard.getPiece(source.getColumn(), source.getRow());
                placePiece(movedPiece, source);
            }
            if (guiCapturedPiece != null)
                guiCapturedPiece = new GUIPiece(target.getColumn(), target.getRow(), guiCapturedPiece.isWhite, guiCapturedPiece.name, ps);
        }
        gamePanel.repaint();
    }

    public void undoCastle(Color color, Position sourceT, Position targetT) {
        GUIPiece movedRook = GUIBoard.getPiece(targetT.getColumn(), targetT.getRow());
        while (movedRook != null) {
            kill(targetT);
            movedRook = GUIBoard.getPiece(targetT.getColumn(), targetT.getRow());
        }
        boolean bool = color.name().equals("WHITE");
        GUIPiece rook = new GUIPiece(sourceT.getColumn(), sourceT.getRow(), bool , "rook", ps);
        ps.add(rook);
    }

    //prepara ação de promoção
    public void promotedPiece(Position position) {
        guiPromotedPiece = GUIBoard.getPiece(position.getColumn(), position.getRow());
        kill(position);
        //posição que a lista de escolha irá aparecer
        for (GUIPiece p : piecesPromotion){
            if (p.isWhite == guiPromotedPiece.isWhite) {
                p.xp = position.getColumn();
                p.x = position.getColumn() * 64;
            }
        }
    }

    public void setPromotedPieceInMatrix(GUIPiece piece) {
//        System.out.println("printar: setPromotedPieceInMatrix, name: " + piece.name + " | color: " + (piece.isWhite ? Color.WHITE : Color.BLACK));
        ChessPiece newPiece = chessMatch.newPiece(piece.name, (piece.isWhite ? Color.WHITE : Color.BLACK));
        chessMatch.getBoard().placePiece(newPiece, new Position(guiPromotedPiece.yp, guiPromotedPiece.xp));
        chessMatch.getPiecesOnTheBoard().add(newPiece);
    }

    public void resetPromotedPieces() {
//        System.out.println("printar: resetPromotedPieces: guiPromotedPiece: " + guiPromotedPiece.name + " | clickedPromotedPiece: " + clickedPromotedPiece.name);
        guiPromotedPiece = null;
        clickedPromotedPiece = null;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}

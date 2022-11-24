/* Reference: https://www.youtube.com/watch?v=LivX1XKpSQA&t=1453s */

package GUI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class GUIBoard {
    public static LinkedList<GUIPiece> ps = new LinkedList<>();
    public static LinkedList<GUIPiece> piecesPromotion = new LinkedList<>();
    public static GUIPiece guiPromotedPiece;
    public static boolean guiColor = false;
    BufferedImage all;
    public static Image imgs[];


    public GUIBoard() {
        imgs = new Image[12];
    }

    public void loadImage() {
        try {
            all = ImageIO.read(new File("src/PNG/chess-pieces.png"));
            int ind = 0;
            for (int y = 0; y < 400; y += 200) {
                for (int x = 0; x < 1200; x += 200) {
                    imgs[ind] = all.getSubimage(x, y, 200, 200).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH);
                    ind++;
                }
            }
        } catch (IOException e) {
            System.out.println("Image not found");
        }

        GUIPiece brook=new GUIPiece(0, 0, false, "rook", ps);
        GUIPiece bknight=new GUIPiece(1, 0, false, "knight", ps);
        GUIPiece bbishop=new GUIPiece(2, 0, false, "bishop", ps);
        GUIPiece bqueen=new GUIPiece(3, 0, false, "queen", ps);
        GUIPiece bking=new GUIPiece(4, 0, false, "king", ps);
        GUIPiece bbishop2=new GUIPiece(5, 0, false, "bishop", ps);
        GUIPiece bkight2=new GUIPiece(6, 0, false, "knight", ps);
        GUIPiece brook2=new GUIPiece(7, 0, false, "rook", ps);
        GUIPiece bpawn1=new GUIPiece(0, 1, false, "pawn", ps);
        GUIPiece bpawn2=new GUIPiece(1, 1, false, "pawn", ps);
        GUIPiece bpawn3=new GUIPiece(2, 1, false, "pawn", ps);
        GUIPiece bpawn4=new GUIPiece(3, 1, false, "pawn", ps);
        GUIPiece bpawn5=new GUIPiece(4, 1, false, "pawn", ps);
        GUIPiece bpawn6=new GUIPiece(5, 1, false, "pawn", ps);
        GUIPiece bpawn7=new GUIPiece(6, 1, false, "pawn", ps);
        GUIPiece bpawn8=new GUIPiece(7, 1, false, "pawn", ps);

        GUIPiece wrook=new GUIPiece(0, 7, true, "rook", ps);
        GUIPiece wknight=new GUIPiece(1, 7, true, "knight", ps);
        GUIPiece wbishop=new GUIPiece(2, 7, true, "bishop", ps);
        GUIPiece wqueen=new GUIPiece(3, 7, true, "queen", ps);
        GUIPiece wking=new GUIPiece(4, 7, true, "king", ps);
        GUIPiece wbishop2=new GUIPiece(5, 7, true, "bishop", ps);
        GUIPiece wkight2=new GUIPiece(6, 7, true, "knight", ps);
        GUIPiece wrook2=new GUIPiece(7, 7, true, "rook", ps);
        GUIPiece wpawn1=new GUIPiece(0, 6, true, "pawn", ps);
        GUIPiece wpawn2=new GUIPiece(1, 6, true, "pawn", ps);
        GUIPiece wpawn3=new GUIPiece(2, 6, true, "pawn", ps);
        GUIPiece wpawn4=new GUIPiece(3, 6, true, "pawn", ps);
        GUIPiece wpawn5=new GUIPiece(4, 6, true, "pawn", ps);
        GUIPiece wpawn6=new GUIPiece(5, 6, true, "pawn", ps);
        GUIPiece wpawn7=new GUIPiece(6, 6, true, "pawn", ps);
        GUIPiece wpawn8=new GUIPiece(7, 6, true, "pawn", ps);



        //pieces promotion
        GUIPiece piece1 = new GUIPiece(0, 0, true,"queen",piecesPromotion);
        GUIPiece piece2 = new GUIPiece(0, 1, true,"bishop",piecesPromotion);
        GUIPiece piece3 = new GUIPiece(0, 2, true,"knight",piecesPromotion);
        GUIPiece piece4 = new GUIPiece(0, 3, true,"rook",piecesPromotion);
        GUIPiece piece5 = new GUIPiece(7, 7, false,"queen",piecesPromotion);
        GUIPiece piece6 = new GUIPiece(7, 6, false,"bishop",piecesPromotion);;
        GUIPiece piece7 = new GUIPiece(7, 5, false,"knight",piecesPromotion);;
        GUIPiece piece8 = new GUIPiece(7, 4, false,"rook",piecesPromotion);
    }

    public static void guiPiecesKill(GUIPiece piece) {
        ps.remove(piece);
    }

    public static GUIPiece getPiece (int x, int y) {
        for (GUIPiece p : ps) {
            if (p.xp==x && p.yp == y) {
                return p;
            }
        }
        return null;
    }

    public static GUIPiece getPromotionPiece (int x, int y) {
        for (GUIPiece p : piecesPromotion) {
            if (p.xp==x && p.yp == y) {
                return p;
            }
        }
        return null;
    }

    public static int getImageIndex(String namePiece) {
        int ind = 0;
        switch (namePiece) {
            case "king":
                ind = 0;
                break;
            case "queen":
                ind = 1;
                break;
            case "bishop":
                ind = 2;
                break;
            case "knight":
                ind = 3;
                break;
            case "rook":
                ind = 4;
                break;
            case "pawn":
                ind = 5;
                break;
        }
        ind = (guiPromotedPiece.isWhite ? (ind) : ind + 6);

        return ind;
    }
}
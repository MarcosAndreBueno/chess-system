package GUI;

import java.util.LinkedList;


public class GUIPiece {
    int xp; int yp; // piece matrix position: xp=column; yp=row
    int x; int y;   // piece image  position: xp/yp -> 0 to 8 * 64
    boolean isWhite;
    public static LinkedList<GUIPiece> guiPieces;
    String name;



    public GUIPiece(int xp, int yp, boolean isWhite,String n, LinkedList<GUIPiece> ps) {
        this.xp = xp; // referência da peça na matriz
        this.yp = yp; // y linha, x coluna
        x = xp*64;    // referência da peça na imagem
        y = yp*64;    // y linha, x coluna
        this.isWhite = isWhite;
        this.guiPieces = ps;
        name = n;
        guiPieces.add(this);
    }

    public static void printPieces(LinkedList<GUIPiece> list) {
        System.out.println("======================================");
        System.out.println("Pecas no tabuleiro");
        for (GUIPiece p: list){
            System.out.println(p.isWhite + " " + p.getName());
        }
        System.out.println("======================================");
    }

    public String getName() {
        return name;
    }


}
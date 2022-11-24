package chess;

import boardgame.Position;

public class ChessPosition {
    private char column;
    private int row;

    public ChessPosition(char column, int row) {
        if (column < 'a' || column > 'h' || row < 1 || row > 8) {
            throw new ChessException("Error instantiating ChessPosition. Valid values are from a1 to h8");
        }
        this.column = column;
        this.row = row;
    }

    public char getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    // transformar coordenadas de xadrez para matriz (ex: a8 = [0][0]; c7 = [1][3])
    protected Position toPosition() {
        // char 'a' equivale a 1
        return new Position(8 - row, column - 'a');
    }

    // transformar coordenadas de matriz para xadrez (ex: [0][0] = a8; [1][3] = c7)
    protected static ChessPosition fromPosition(Position position) {
        return new ChessPosition((char)('a' + position.getColumn()), 8 - position.getRow());
    }

    @Override
    public String toString() {
        return "" + column + row;
    }

}

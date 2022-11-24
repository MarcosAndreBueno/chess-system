package boardgame;

public abstract class Piece {

    public Position position;
    private Board board;

    public Piece(Board board) {
        this.board = board;
    }

    // só pacote boardgame e subclasses de Piece podem acessar
    protected Board getBoard() {
        return board;
    }

    public abstract boolean[][] possibleMoves();

    // hook methods
    public boolean possibleMove(Position position) {
        return possibleMoves()[position.getRow()][position.getColumn()];
    }

    // hook methods
    public boolean isThereAnyPossibleMove() {
        boolean[][] mat = possibleMoves();
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat.length; j++) {
                if (mat [i][j]) {
                    return true;
                }
            }
        }
        return false;
    }
}

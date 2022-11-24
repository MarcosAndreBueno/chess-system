package chess;

import boardgame.BoardException;

public class ChessException extends BoardException {
    // default ID para RuntimeException
    private static final long serialVersionUID = 1L;

    public ChessException(String  msg) {
        super(msg);
    }
}

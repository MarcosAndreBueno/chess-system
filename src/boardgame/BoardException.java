package boardgame;

public class BoardException extends  RuntimeException{
    // default ID para RuntimeException
    private static final long serialVersionUID = 1L;

    public BoardException(String msg) {
        super(msg);
    }
}

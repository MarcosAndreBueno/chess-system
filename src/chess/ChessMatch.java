package chess;


import application.UI;
import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;
import GUI.GUIChessMatch;
import GUI.GUIPiece;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private Board board;
    private GUIChessMatch guiChessMatch;
    private int turn;
    private Color currentPlayer;
    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();
    private boolean check;
    private boolean checkMate;
    private boolean testingCheckMate = false;
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;
    private GUIPiece guiCapturedPiece;
    private boolean isEnPassant = false;

    public ChessMatch(GUIChessMatch guiChessMatch) {
        board = new Board(8,8); initialSetup();
        turn = 1;
        currentPlayer = Color.WHITE;
        this.guiChessMatch = guiChessMatch;
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean getCheck() {
        return check;
    }

    public boolean getCheckMate() {
        return checkMate;
    }

    public List<Piece> getPiecesOnTheBoard() {
        return piecesOnTheBoard;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isEnPassant() {
        return isEnPassant;
    }

    public void setEnPassant(boolean enPassant) {
        isEnPassant = enPassant;
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i=0; i< board.getRows(); i++) {
            for (int j=0; j< board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
    }


    // trabalha a peça selecionada e a posição que ela foi enviada
    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition(); // convert chess position to matrix position
        Position target = targetPosition.toPosition(); // convert chess position to matrix position
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source,target);

        // se rei estava em check, desfaz movimentos
        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            guiChessMatch.undoMove(source, target, guiCapturedPiece);
            throw new ChessException("You can't put yourself in check mate!");
        }

        ChessPiece movedPiece = (ChessPiece)board.piece(target);
        // #specialmove promotion
        promoted = null;
        if (movedPiece instanceof Pawn) {
            if ((movedPiece.getColor() == Color.WHITE && target.getRow() == 0) || (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
                promoted = (ChessPiece)board.piece(target);
                promoted = replacePromotedPiece("Q");
            }
        }

        // checar se movimento foi um castle
        if (movedPiece instanceof King && (target.getColumn() == source.getColumn() - 2 || target.getColumn() == source.getColumn() + 2)) {
            if (target.getColumn() > 4 && testCheckCastlingKingSide(currentPlayer, sourcePosition.toPosition())) {
                undoMove(source, target, null);
                guiChessMatch.undoMove(source,target,null);
                throw new ChessException("You are not allowed to castle through check!");
            }
            if (target.getColumn() < 4 && testCheckCastlingQueenSide(currentPlayer, sourcePosition.toPosition())) {
                undoMove(source, target, null);
                guiChessMatch.undoMove(source,target,null);
                throw new ChessException("You are not allowed to castle through check!");
            }
        }

        // testa se oponente está em check (para ser usado pela classe UI)
        check = (testCheck(opponent(currentPlayer))) ? true : false;

        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        }
        else {
            nextTurn();
        }

        // #specialmove en passant
        if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
            enPassantVulnerable = movedPiece;
        }
        else {
            enPassantVulnerable = null;
        }

        //print matriz
        List<ChessPiece> captured = new ArrayList<>();
        UI.printMatch(this, captured);
        return (ChessPiece) capturedPiece;
    }

    public ChessPiece replacePromotedPiece(String type) {
        // defensive programming
        if (promoted == null) {
            throw new IllegalStateException("There is no piece to be promoted");
        }
        if (!type.equals("B") && !type.equals("N") && !type.equals("R") & !type.equals("Q")) {
            return promoted;
        }
        Position pos = promoted.getChessPosition().toPosition();
        Piece p = board.removePiece(pos);
        piecesOnTheBoard.remove(p);

        guiChessMatch.promotedPiece(pos);

//        ChessPiece newPiece = newPiece(newGuiPiece.getName(), promoted.getColor());
//        board.placePiece(newPiece, pos);
//        piecesOnTheBoard.add(newPiece);

        return null;
    }

    public ChessPiece newPiece(String type, Color color) {
        if (type.equals("bishop")) return new Bishop(board, color);
        if (type.equals("knight")) return new Knight(board, color);
        if (type.equals("queen")) return new Queen(board, color);
        return new Rook(board, color);
    }

    // Após escolher peça, imprimi movimentos possíveis
    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    private void validateSourcePosition(Position position) {
        if (!board.thereIsAPiece(position)) {
            guiChessMatch.undoMove(position, null, null);
            throw new ChessException("There is no piece on source position");
        }
        if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
            guiChessMatch.undoMove(position, null, null);
            throw new ChessException("The chosen piece is not yours");
        }
        if (!board.piece(position).isThereAnyPossibleMove()) {
            guiChessMatch.undoMove(position, null, null);
            throw new ChessException("There is no possible moves for the chosen piece");
        }
    }

    // testar se a peça pode se mover para o local indicado
    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            guiChessMatch.undoMove(source, target, null);
            throw new ChessException("The chosen piece can't move to target position");
        }
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece)board.removePiece(source); // downcasting de piece para chesspiece
        p.increaseMoveCount();

        GUIPiece gp = guiChessMatch.saveSourcePiece(source);
        Piece capturedPiece = board.removePiece(target);
        if (!testingCheckMate)
            guiCapturedPiece = guiChessMatch.kill(target);
        board.placePiece(p, target); // upcasting é feito naturalmente

        if (!testingCheckMate)
            guiChessMatch.placePiece(gp, target);

        // se peça capturada, salvar na lista de peças capturadas
        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        // #specialmove castling kingside rook
        if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
            // posição da torre
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
            Position targetT = new Position(source.getRow(), source.getColumn() + 1);
            // remover torre e adicionar torre
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            GUIPiece guiRook = guiChessMatch.saveSourcePiece(sourceT);
            board.placePiece(rook, targetT);
            if (!testingCheckMate)
                guiChessMatch.placePiece(guiRook, targetT);
            // impede outro castling
            rook.increaseMoveCount();
        }

        // #specialmove castling queenside rook
        if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            GUIPiece guiRook = guiChessMatch.saveSourcePiece(sourceT);
            board.placePiece(rook, targetT);
            if (!testingCheckMate)
                guiChessMatch.placePiece(guiRook, targetT);
            rook.increaseMoveCount();
        }

        // #specialmove en passant
        if (p instanceof Pawn) {
            // se peão se moveu para uma casa vazia na diagonal, en passant ocorreu
            if (source.getColumn() != target.getColumn() && capturedPiece == null) {
                isEnPassant = true;
                Position pawnPosition;
                // se en passant foi peça branca, remove peça abaixo
                if (p.getColor() == Color.WHITE) {
                    pawnPosition = new Position(target.getRow() + 1, target.getColumn());
                }
                // se en passant foi peça preta, remove peça acima
                else {
                    pawnPosition = new Position(target.getRow() - 1, target.getColumn());
                }
                capturedPiece = board.removePiece(pawnPosition);
                if (!testingCheckMate)
                    guiCapturedPiece = guiChessMatch.saveSourcePiece(pawnPosition);
                capturedPieces.add(capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
                if (!testingCheckMate)
                    guiChessMatch.kill(pawnPosition);
            }
        }

        return capturedPiece;
    }

    // remove tudo de Move em caso de movimento ilegal
    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece p = (ChessPiece) board.removePiece(target); //upcasting
        GUIPiece gp = guiChessMatch.saveSourcePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);
        if (!testingCheckMate)
            guiChessMatch.placePiece(gp, source);
        guiChessMatch.resetPromotedPieces();

        if (capturedPiece != null) {
            board.placePiece(capturedPiece, target);
            if (!testingCheckMate)
                guiChessMatch.placePiece(guiCapturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }

        // #specialmove castling kingside rook
        if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
            Position targetT = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetT);
//            guiChessMatch.kill(targetT);
            board.placePiece(rook, sourceT);
            if (!testingCheckMate)
                guiChessMatch.undoCastle(rook.getColor(), sourceT, targetT);
            rook.decreaseMoveCount();
        }

        // #specialmove castling queenside rook
        if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetT);
            if (!testingCheckMate)
                guiChessMatch.kill(targetT);
            board.placePiece(rook, sourceT);
            if (!testingCheckMate)
                guiChessMatch.undoCastle(rook.getColor(), sourceT, targetT);
            rook.decreaseMoveCount();
        }

        // #specialmove en passant
        if (p instanceof Pawn) {
            if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
                ChessPiece pawn = (ChessPiece)board.removePiece(target);
                if (!testingCheckMate)
                    guiChessMatch.kill(target);
                Position pawnPosition;
                if (p.getColor() == Color.WHITE) {
                    pawnPosition = new Position(3, target.getColumn());
                }
                else {
                    pawnPosition = new Position(4, target.getColumn());
                }
                board.placePiece(pawn, pawnPosition);
                if (!testingCheckMate)
                    guiChessMatch.undoEnPassant(guiCapturedPiece, pawnPosition, gp, source);
            }
        }

    }

    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    // procura rei no tabuleiro
    private ChessPiece king(Color color) {
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list) {
            if (p instanceof King) {
                return (ChessPiece)p;
            }
        }
        throw new IllegalStateException("There is no " + color + " king on the board");
    }

    // em caso de alguma peça oponente puder se mover onde nosso rei está, então é check
    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckCastlingKingSide(Color color, Position source) {
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();
            if ((mat[source.getRow()][source.getColumn() + 1]) || mat[source.getRow()][source.getColumn() + 2]) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckCastlingQueenSide(Color color, Position source) {
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();
            if ((mat[source.getRow()][source.getColumn() - 1]) || mat[source.getRow()][source.getColumn() - 2] || mat[source.getRow()][source.getColumn() - 3]) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Color color) {
        if (!testCheck(color)) {
            return false;
        }
        testingCheckMate = true;
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list) {
            boolean[][] mat = p.possibleMoves();
            // testar cada peça e procurar se alguma tem lance que tira do check
            for (int i=0; i<board.getRows(); i++) {
                for (int j=0; j<board.getColumns(); j++) {
                    if (mat[i][j]) {
                        // pegar peça atual da list e colocar na posição [i][j], checar se continua em check
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if (!testCheck) {
                            testingCheckMate = false;
                            return false;
                        }
                    }
                }
            }
        }
        testingCheckMate = false;
        return true;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private void initialSetup() {
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));


        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
    }
}

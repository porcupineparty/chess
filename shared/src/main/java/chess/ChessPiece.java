package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {

        this.pieceColor = pieceColor;
        this.type = type;

    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING -> calculateKingMoves(board, myPosition);
            case QUEEN -> calculateQueenMoves(board, myPosition);
            case BISHOP -> calculateBishopMoves(board, myPosition);
            case KNIGHT -> calculateKnightMoves(board, myPosition);
            case ROOK -> calculateRookMoves(board, myPosition);
            case PAWN -> calculatePawnMoves(board, myPosition);
            default -> new ArrayList<>();
        };


        //gonna have to change this later.
    }
    private void findDiagonalHorizontal(ChessBoard board, ChessPosition curPosition, Collection<ChessMove> validMoves, int rowDirect, int colDirect){
        findDiagonalHorizontalRecursive(board, curPosition, curPosition, validMoves, rowDirect, colDirect);
    }
    private void findDiagonalHorizontalRecursive(ChessBoard board, ChessPosition originalPosition, ChessPosition curPosition, Collection<ChessMove> validMoves, int rowDirect, int colDirect){
        ChessPosition newPosition = new ChessPosition(curPosition.getRow() + rowDirect, curPosition.getColumn() + colDirect);

        if (isPositionValid(newPosition)) {
            ChessPiece possiblePiece = board.getPiece(newPosition);

            if (possiblePiece == null) {
                validMoves.add(new ChessMove(originalPosition, newPosition, null));
                if(getPieceType() != PieceType.KNIGHT && getPieceType() != PieceType.KING){
                    findDiagonalHorizontalRecursive(board, originalPosition, newPosition, validMoves, rowDirect, colDirect);
                }
            }
            else if (possiblePiece.pieceColor != getTeamColor()) {

                validMoves.add(new ChessMove(originalPosition, newPosition, null));

            }
        }
    }
    private void findPawnWhite(ChessBoard board, ChessPosition curPosition, Collection<ChessMove> validMoves, int rowDirect, int colDirect) {
        ChessPosition newPosition = new ChessPosition(curPosition.getRow() + rowDirect, curPosition.getColumn() + colDirect);
        if (isPositionValid(newPosition)) {
            ChessPiece possiblePiece = board.getPiece(newPosition);
            if (colDirect == -1 || colDirect == +1) {
                if (newPosition.getRow() == 8) {
                    if (possiblePiece != null && possiblePiece.pieceColor != getTeamColor()) {
                        validMoves.add(new ChessMove(curPosition, newPosition, PieceType.ROOK));
                        validMoves.add(new ChessMove(curPosition, newPosition, PieceType.BISHOP));
                        validMoves.add(new ChessMove(curPosition, newPosition, PieceType.KNIGHT));
                        validMoves.add(new ChessMove(curPosition, newPosition, PieceType.QUEEN));
                    }
                } else {
                    if (possiblePiece != null && possiblePiece.pieceColor != getTeamColor()) {
                        validMoves.add(new ChessMove(curPosition, newPosition, null));
                    }
                }

            } else {
                if (rowDirect == 2) {
                    if(curPosition.getRow() == 2){
                        ChessPosition blocked = new ChessPosition(curPosition.getRow() + 1, curPosition.getColumn());
                        ChessPiece blockedPiece = board.getPiece(blocked);
                        if (blockedPiece == null && possiblePiece == null) {
                            validMoves.add(new ChessMove(curPosition, newPosition, null));
                        }
                    }
                }
                else{
                    if(possiblePiece == null){
                        if(newPosition.getRow() == 8 || newPosition.getRow() == 1){
                            validMoves.add(new ChessMove(curPosition, newPosition, PieceType.ROOK));
                            validMoves.add(new ChessMove(curPosition, newPosition, PieceType.BISHOP));
                            validMoves.add(new ChessMove(curPosition, newPosition, PieceType.KNIGHT));
                            validMoves.add(new ChessMove(curPosition, newPosition, PieceType.QUEEN));
                        }
                        else{
                            validMoves.add(new ChessMove(curPosition, newPosition, null));
                        }
                    }
                }
            }
        }
    }

    private void findPawnBlack(ChessBoard board, ChessPosition curPosition, Collection<ChessMove> validMoves, int rowDirect, int colDirect) {
        ChessPosition newPosition = new ChessPosition(curPosition.getRow() + rowDirect, curPosition.getColumn() + colDirect);
        if (isPositionValid(newPosition)) {
            ChessPiece possiblePiece = board.getPiece(newPosition);
            if (colDirect == -1 || colDirect == +1) {
                if (newPosition.getRow() == 1) {
                    if (possiblePiece != null && possiblePiece.pieceColor != getTeamColor()) {
                        validMoves.add(new ChessMove(curPosition, newPosition, PieceType.ROOK));
                        validMoves.add(new ChessMove(curPosition, newPosition, PieceType.BISHOP));
                        validMoves.add(new ChessMove(curPosition, newPosition, PieceType.KNIGHT));
                        validMoves.add(new ChessMove(curPosition, newPosition, PieceType.QUEEN));
                    }
                } else {
                    if (possiblePiece != null && possiblePiece.pieceColor != getTeamColor()) {
                        validMoves.add(new ChessMove(curPosition, newPosition, null));
                    }
                }

            } else {
                if (rowDirect == -2) {
                    if(curPosition.getRow() == 7){
                        ChessPosition blocked = new ChessPosition(curPosition.getRow() - 1, curPosition.getColumn());
                        ChessPiece blockedPiece = board.getPiece(blocked);
                        if (blockedPiece == null && possiblePiece == null) {
                            validMoves.add(new ChessMove(curPosition, newPosition, null));
                        }
                    }
                }
                else{
                    if(possiblePiece == null){
                        if(newPosition.getRow() == 1){
                            validMoves.add(new ChessMove(curPosition, newPosition, PieceType.ROOK));
                            validMoves.add(new ChessMove(curPosition, newPosition, PieceType.BISHOP));
                            validMoves.add(new ChessMove(curPosition, newPosition, PieceType.KNIGHT));
                            validMoves.add(new ChessMove(curPosition, newPosition, PieceType.QUEEN));
                        }
                        else{
                            validMoves.add(new ChessMove(curPosition, newPosition, null));
                        }
                    }
                }
            }
        }
    }





    private Boolean isPositionValid(ChessPosition position){
        return position.getRow() >= 1 && position.getColumn() >= 1 && position.getRow() < 9 && position.getColumn() < 9;
    }

    private Collection<ChessMove>calculateKingMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();

        findDiagonalHorizontal(board, myPosition, validMoves, 1, 1);
        findDiagonalHorizontal(board, myPosition, validMoves, 1, 0);
        findDiagonalHorizontal(board, myPosition, validMoves, 1, -1);
        findDiagonalHorizontal(board, myPosition, validMoves, 0, -1);
        findDiagonalHorizontal(board, myPosition, validMoves, 0, 1);
        findDiagonalHorizontal(board, myPosition, validMoves, -1, 1);
        findDiagonalHorizontal(board, myPosition, validMoves, -1, 0);
        findDiagonalHorizontal(board, myPosition, validMoves, -1, -1);

        return validMoves;
    }
    private Collection<ChessMove>calculateQueenMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();

        findDiagonalHorizontal(board, myPosition, validMoves, 1, 1);
        findDiagonalHorizontal(board, myPosition, validMoves, -1, 1);
        findDiagonalHorizontal(board, myPosition, validMoves, -1, -1);
        findDiagonalHorizontal(board, myPosition, validMoves, 1, -1);
        findDiagonalHorizontal(board, myPosition, validMoves, 1, 0);
        findDiagonalHorizontal(board, myPosition, validMoves, -1, 0);
        findDiagonalHorizontal(board, myPosition, validMoves, 0, 1);
        findDiagonalHorizontal(board, myPosition, validMoves, 0, -1);

        return validMoves;
    }
    private Collection<ChessMove>calculateBishopMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();

        findDiagonalHorizontal(board, myPosition, validMoves, 1, 1);
        findDiagonalHorizontal(board, myPosition, validMoves, -1, 1);
        findDiagonalHorizontal(board, myPosition, validMoves, -1, -1);
        findDiagonalHorizontal(board, myPosition, validMoves, 1, -1);

        return validMoves;
    }

    private Collection<ChessMove>calculateKnightMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        findDiagonalHorizontal(board, myPosition, validMoves, 1, 2);
        findDiagonalHorizontal(board, myPosition, validMoves, 1, -2);
        findDiagonalHorizontal(board, myPosition, validMoves, 2, 1);
        findDiagonalHorizontal(board, myPosition, validMoves, 2, -1);
        findDiagonalHorizontal(board, myPosition, validMoves, -1, 2);
        findDiagonalHorizontal(board, myPosition, validMoves, -1, -2);
        findDiagonalHorizontal(board, myPosition, validMoves, -2, 1);
        findDiagonalHorizontal(board, myPosition, validMoves, -2, -1);
        return validMoves;
    }
    private Collection<ChessMove>calculateRookMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();

        findDiagonalHorizontal(board, myPosition, validMoves, 1, 0);
        findDiagonalHorizontal(board, myPosition, validMoves, -1, 0);
        findDiagonalHorizontal(board, myPosition, validMoves, 0, 1);
        findDiagonalHorizontal(board, myPosition, validMoves, 0, -1);

        return validMoves;
    }
    private Collection<ChessMove>calculatePawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        if(getTeamColor() == ChessGame.TeamColor.WHITE){
            findPawnWhite(board, myPosition, validMoves, 1, 0);
            findPawnWhite(board, myPosition, validMoves, 2, 0);
            findPawnWhite(board, myPosition, validMoves, 1, -1);
            findPawnWhite(board, myPosition, validMoves, 1, 1);
            return validMoves;
        }
        else{
            findPawnBlack(board, myPosition, validMoves, -1, 0);
            findPawnBlack(board, myPosition, validMoves, -2, 0);
            findPawnBlack(board, myPosition, validMoves, -1, -1);
            findPawnBlack(board, myPosition, validMoves, -1, 1);
            return validMoves;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
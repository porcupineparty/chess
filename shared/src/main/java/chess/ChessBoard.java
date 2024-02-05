package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() -1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public ChessPosition getKing(ChessGame.TeamColor color){
        for(int column = 0; column < 8; column++){
            for(int row = 0; row < 8; row++){
                if(squares[row][column] != null && squares[row][column].getPieceType() == ChessPiece.PieceType.KING && squares[row][column].getTeamColor() == color){
                    //check to see if bug later.
                    return new ChessPosition(row + 1, column  + 1);
                }
            }
        }
        return null;
    }
    public void resetBoard() {
        for(int column = 1; column < 9; column++){
            addPiece(new ChessPosition(2, column), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, column), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            switch(column){
                case 1:
                case 8:
                    addPiece(new ChessPosition(1, column), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                    addPiece(new ChessPosition(8, column), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                    break;
                case 2:
                case 7:
                    addPiece(new ChessPosition(1, column), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                    addPiece(new ChessPosition(8, column), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
                    break;
                case 3:
                case 6:
                    addPiece(new ChessPosition(1, column), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                    addPiece(new ChessPosition(8, column), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
                    break;
                case 4:
                    addPiece(new ChessPosition(1, column), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
                    addPiece(new ChessPosition(8, column), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
                    break;
                case 5:
                    addPiece(new ChessPosition(1, column), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
                    addPiece(new ChessPosition(8, column), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
                    break;
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("ChessBoard{\n");
        for (ChessPiece[] row : squares) {
            result.append("  ");
            for (ChessPiece piece : row) {
                if (piece != null) {
                    result.append(piece.getPieceType()).append("\t");
                } else {
                    result.append("EMPTY\t");
                }
            }
            result.append("\n");
        }
        result.append("}");
        return result.toString();
    }
}

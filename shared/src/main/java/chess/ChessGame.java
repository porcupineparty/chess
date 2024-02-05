package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard myChessBoard;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(startPosition == null){
            return null;
        }
        ChessPiece chessPiece = myChessBoard.getPiece(startPosition);
        Collection<ChessMove> originalMoves = chessPiece.pieceMoves(myChessBoard, startPosition);
        ChessPiece newPiece = myChessBoard.getPiece(startPosition);
        Collection<ChessMove> newValidMoves = new ArrayList<>();
        for(ChessMove move : originalMoves){
            ChessBoard newChessBoard = deepCopyChessBoard(myChessBoard);
            newChessBoard.addPiece(move.getEndPosition(), newPiece);
            newChessBoard.deletePiece(move.getStartPosition());
            if(!isBoardInCheck(newChessBoard, newPiece.getTeamColor())){
                newValidMoves.add(move);
            }
        }
        return newValidMoves;
    }

    private ChessBoard deepCopyChessBoard(ChessBoard original){
        ChessBoard newBoard = new ChessBoard();
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece originalPiece = original.getPiece(position);
                if(originalPiece != null){
                    newBoard.addPiece(position, originalPiece);
                }
            }
        }
        return newBoard;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new InvalidMoveException("Not implemented");
    }

    /**
     * Determines if the given eam is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isBoardInCheck(myChessBoard, teamColor);
    }

    private boolean isBoardInCheck(ChessBoard chessBoard, TeamColor teamColor) {

        ChessPosition kingPosition = chessBoard.getKing(teamColor);
        if(isCheckRowCol(chessBoard, kingPosition, teamColor)) return true;
        if(isCheckDiagonal(chessBoard, kingPosition, teamColor)) return true;
        if(isCheckKnight(chessBoard, kingPosition, teamColor)) return true;
        if(isKingCheck(chessBoard, kingPosition, teamColor)) return true;
        return isCheckPawn(chessBoard, kingPosition, teamColor);
    }

    private boolean isKingCheck(ChessBoard chessBoard, ChessPosition kingPosition, TeamColor teamColor) {
        return(kingSecondCheck(chessBoard, kingPosition, teamColor, 1, -1) ||
                kingSecondCheck(chessBoard, kingPosition, teamColor, 1, 0) ||
                kingSecondCheck(chessBoard, kingPosition, teamColor, 1, 1) ||
                kingSecondCheck(chessBoard, kingPosition, teamColor, -1, -1) ||
                kingSecondCheck(chessBoard, kingPosition, teamColor, -1, 0) ||
                kingSecondCheck(chessBoard, kingPosition, teamColor, -1, 1) ||
                kingSecondCheck(chessBoard, kingPosition, teamColor, 0, 1) ||
                kingSecondCheck(chessBoard, kingPosition, teamColor, 0, -1));
    }

    private boolean isCheckPawn(ChessBoard chessBoard, ChessPosition kingPosition, TeamColor teamColor) {
        if(teamColor == TeamColor.WHITE){
            return pawnCheck(chessBoard, kingPosition, teamColor, 1, 1) ||
                    pawnCheck(chessBoard, kingPosition, teamColor, 1, -1);

        }
        else{
            return pawnCheck(chessBoard, kingPosition, teamColor, -1, 1) ||
                    pawnCheck(chessBoard, kingPosition, teamColor, -1, -1);

        }
    }
    private boolean isCheckKnight(ChessBoard chessBoard, ChessPosition kingPosition, TeamColor teamColor) {
        return (knightSecondCheck(chessBoard, kingPosition, teamColor, -2, 1) ||
                knightSecondCheck(chessBoard, kingPosition, teamColor, -2, -1) ||
                knightSecondCheck(chessBoard, kingPosition, teamColor, 2, 1) ||
                knightSecondCheck(chessBoard, kingPosition, teamColor, 2, -1) ||
                knightSecondCheck(chessBoard, kingPosition, teamColor, 1, 2) ||
                knightSecondCheck(chessBoard, kingPosition, teamColor, 1, -2) ||
                knightSecondCheck(chessBoard, kingPosition, teamColor, -1, 2) ||
                knightSecondCheck(chessBoard, kingPosition, teamColor, -1, -2));
    }
    private boolean isCheckDiagonal(ChessBoard chessBoard, ChessPosition kingPosition, TeamColor teamColor) {
        return (isCheckDiagonalRec(chessBoard, kingPosition, teamColor, 1, 1) ||
                isCheckDiagonalRec(chessBoard, kingPosition, teamColor, 1, -1) ||
                isCheckDiagonalRec(chessBoard, kingPosition, teamColor, -1, 1) ||
                isCheckDiagonalRec(chessBoard, kingPosition, teamColor, -1, -1));
    }
    private boolean isCheckRowCol(ChessBoard chessBoard, ChessPosition kingPosition, TeamColor teamColor){
        return (isCheckRowColRec(chessBoard, kingPosition, teamColor, 0, 1) ||
                isCheckRowColRec(chessBoard, kingPosition, teamColor, 0, -1) ||
                isCheckRowColRec(chessBoard, kingPosition, teamColor, 1, 0) ||
                isCheckRowColRec(chessBoard, kingPosition, teamColor, -1, 0));
    }
    private boolean kingSecondCheck(ChessBoard chessBoard, ChessPosition nextPosition, TeamColor teamColor, int row, int col) {
        ChessPosition newPosition = new ChessPosition(nextPosition.getRow() + row, nextPosition.getColumn() + col);
        if(isPositionValid(newPosition)){
            if(chessBoard.getPiece(newPosition) == null){
                return false;
            }
            else if(chessBoard.getPiece(newPosition).getTeamColor() != teamColor){
                return chessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.KING;
            }
            else if(chessBoard.getPiece(newPosition).getTeamColor() == teamColor){
                return false;
            }
        }
        return false;
    }
    private boolean pawnCheck(ChessBoard chessBoard, ChessPosition nextPosition, TeamColor teamColor, int row, int col) {
        ChessPosition newPosition = new ChessPosition(nextPosition.getRow() + row, nextPosition.getColumn() + col);
        if(isPositionValid(newPosition)){
            if(chessBoard.getPiece(newPosition) == null){
                return false;
            }
            else if(chessBoard.getPiece(newPosition).getTeamColor() != teamColor){
                return chessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.PAWN;

            }
            else if(chessBoard.getPiece(newPosition).getTeamColor() == teamColor){
                return false;
            }
        }
        return false;
    }
    private boolean knightSecondCheck(ChessBoard chessBoard, ChessPosition nextPosition, TeamColor teamColor, int row, int col) {
        ChessPosition newPosition = new ChessPosition(nextPosition.getRow() + row, nextPosition.getColumn() + col);
        if(isPositionValid(newPosition)){
            if(chessBoard.getPiece(newPosition) == null){
                return false;
            }
            else if(chessBoard.getPiece(newPosition).getTeamColor() != teamColor){
                return chessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.KNIGHT;

            }
            else if(chessBoard.getPiece(newPosition).getTeamColor() == teamColor){
                return false;
            }
        }
        return false;
    }
    private boolean isCheckDiagonalRec(ChessBoard chessBoard, ChessPosition nextPosition, TeamColor teamColor, int row, int col) {
        ChessPosition newPosition = new ChessPosition(nextPosition.getRow() + row, nextPosition.getColumn() + col);
        if(isPositionValid(newPosition)){
            if(chessBoard.getPiece(newPosition) == null){
                return isCheckDiagonalRec(chessBoard, newPosition, teamColor, row, col);
            }
            else if(chessBoard.getPiece(newPosition).getTeamColor() != teamColor){
                return chessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.QUEEN || myChessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.BISHOP;

            }
            else if(chessBoard.getPiece(newPosition).getTeamColor() == teamColor){
                return false;
            }
        }
        return false;
    }
    private boolean isCheckRowColRec(ChessBoard chessBoard, ChessPosition nextPosition, TeamColor teamColor, int row, int col) {
        ChessPosition newPosition = new ChessPosition(nextPosition.getRow() + row, nextPosition.getColumn() + col);
        if(isPositionValid(newPosition)){
            if(chessBoard.getPiece(newPosition) == null){
                return isCheckRowColRec(chessBoard, newPosition, teamColor,row, col);
            }
            else if(chessBoard.getPiece(newPosition).getTeamColor() != teamColor){
                return chessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.QUEEN || myChessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.ROOK;

            }
            else if(chessBoard.getPiece(newPosition).getTeamColor() == teamColor){
                return false;
            }
        }
        return false;
    }
    private Boolean isPositionValid(ChessPosition position){
        return position.getRow() >= 1 && position.getColumn() >= 1 && position.getRow() < 9 && position.getColumn() < 9;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
    * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        myChessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myChessBoard;
    }
}

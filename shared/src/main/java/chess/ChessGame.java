package chess;

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

        return null;
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
        ChessPosition kingPosition = myChessBoard.getKing(teamColor);
        if(isCheckRowCol(kingPosition, teamColor)) return true;
        if(isCheckDiagnol(kingPosition, teamColor)) return true;
        if(isCheckKnight(kingPosition, teamColor)) return true;
        return isCheckPawn(kingPosition, teamColor);
    }

    private boolean isCheckPawn(ChessPosition kingPosition, TeamColor teamColor) {
        if(teamColor == TeamColor.WHITE){
            return pawnCheck(kingPosition, teamColor, 1, 1) ||
                    pawnCheck(kingPosition, teamColor, 1, -1);

        }
        else{
            return pawnCheck(kingPosition, teamColor, -1, 1) ||
                    pawnCheck(kingPosition, teamColor, -1, -1);

        }
    }
    private boolean isCheckKnight(ChessPosition kingPosition, TeamColor teamColor) {
        return (knightSecondCheck(kingPosition, teamColor, -2, 1) ||
                knightSecondCheck(kingPosition, teamColor, -2, -1) ||
                knightSecondCheck(kingPosition, teamColor, 2, 1) ||
                knightSecondCheck(kingPosition, teamColor, 2, -1) ||
                knightSecondCheck(kingPosition, teamColor, 1, 2) ||
                knightSecondCheck(kingPosition, teamColor, 1, -2) ||
                knightSecondCheck(kingPosition, teamColor, -1, 2) ||
                knightSecondCheck(kingPosition, teamColor, -1, -2));
    }
    private boolean isCheckDiagnol(ChessPosition kingPosition, TeamColor teamColor) {
        return (isCheckDiagnolRec(kingPosition, teamColor, 1, 1) ||
                isCheckDiagnolRec(kingPosition, teamColor, 1, -1) ||
                isCheckDiagnolRec(kingPosition, teamColor, -1, 1) ||
                isCheckDiagnolRec(kingPosition, teamColor, -1, -1));
    }
    private boolean isCheckRowCol(ChessPosition kingPosition, TeamColor teamColor){
        return (isCheckRowColRec(kingPosition, teamColor, 0, 1) ||
                isCheckRowColRec(kingPosition, teamColor, 0, -1) ||
                isCheckRowColRec(kingPosition, teamColor, 1, 0) ||
                isCheckRowColRec(kingPosition, teamColor, -1, 0));
    }
    private boolean pawnCheck(ChessPosition nextPosition, TeamColor teamColor, int row, int col) {
        ChessPosition newPosition = new ChessPosition(nextPosition.getRow() + row, nextPosition.getColumn() + col);
        if(isPositionValid(newPosition)){
            if(myChessBoard.getPiece(newPosition) == null){
                return false;
            }
            else if(myChessBoard.getPiece(newPosition).getTeamColor() != teamColor){
                return myChessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.PAWN;

            }
            else if(myChessBoard.getPiece(newPosition).getTeamColor() == teamColor){
                return false;
            }
        }
        return false;
    }
    private boolean knightSecondCheck(ChessPosition nextPosition, TeamColor teamColor, int row, int col) {
        ChessPosition newPosition = new ChessPosition(nextPosition.getRow() + row, nextPosition.getColumn() + col);
        if(isPositionValid(newPosition)){
            if(myChessBoard.getPiece(newPosition) == null){
                return false;
            }
            else if(myChessBoard.getPiece(newPosition).getTeamColor() != teamColor){
                return myChessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.KNIGHT;

            }
            else if(myChessBoard.getPiece(newPosition).getTeamColor() == teamColor){
                return false;
            }
        }
        return false;
    }
    private boolean isCheckDiagnolRec(ChessPosition nextPosition, TeamColor teamColor, int row, int col) {
        ChessPosition newPosition = new ChessPosition(nextPosition.getRow() + row, nextPosition.getColumn() + col);
        if(isPositionValid(newPosition)){
            if(myChessBoard.getPiece(newPosition) == null){
                return isCheckDiagnolRec(newPosition, teamColor, row, col);
            }
            else if(myChessBoard.getPiece(newPosition).getTeamColor() != teamColor){
                return myChessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.QUEEN || myChessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.BISHOP;

            }
            else if(myChessBoard.getPiece(newPosition).getTeamColor() == teamColor){
                return false;
            }
        }
        return false;
    }
    private boolean isCheckRowColRec(ChessPosition nextPosition, TeamColor teamColor, int row, int col) {
        ChessPosition newPosition = new ChessPosition(nextPosition.getRow() + row, nextPosition.getColumn() + col);
        if(isPositionValid(newPosition)){
            if(myChessBoard.getPiece(newPosition) == null){
                return isCheckRowColRec(newPosition, teamColor,row, col);
            }
            else if(myChessBoard.getPiece(newPosition).getTeamColor() != teamColor){
                return myChessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.QUEEN || myChessBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.ROOK;

            }
            else if(myChessBoard.getPiece(newPosition).getTeamColor() == teamColor){
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

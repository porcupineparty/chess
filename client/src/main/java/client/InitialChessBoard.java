package client;

import chess.ChessBoard;
import ui.EscapeSequences;
import chess.ChessPosition;
import chess.ChessGame;
import chess.ChessPiece;
public class InitialChessBoard {

    public static void printInitialChessBoard() {
        // Create a new ChessBoard instance
        ChessBoard initialBoard = new ChessBoard();

        // Reset the board to its initial state
        initialBoard.resetBoard();

        // Print out the initial chessboard state using escape sequences
        System.out.println(EscapeSequences.ERASE_SCREEN);
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = initialBoard.getPiece(new ChessPosition(row, col));
                boolean isLightGrey = (row + col) % 2 == 0; // Check if the square is on an even row and an even column

                if (piece != null) {
                    String pieceSymbol = getPieceSymbol(piece);
                    System.out.print(isLightGrey ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.RESET_BG_COLOR);
                    System.out.print(pieceSymbol);
                } else {
                    // Print an empty space for squares with no pieces
                    System.out.print(isLightGrey ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.RESET_BG_COLOR);
                    System.out.print(EscapeSequences.EMPTY);
                }
            }
            // Reset the background color to black after printing each row
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(); // Move to the next line after printing each row
        }
    }

    public static void printInitialChessBoardReverse() {
        // Create a new ChessBoard instance
        ChessBoard initialBoard = new ChessBoard();

        // Reset the board to its initial state
        initialBoard.resetBoard();

        // Print out the initial chessboard state using escape sequences in reverse direction
        System.out.println(EscapeSequences.ERASE_SCREEN);
        for (int row = 8; row >= 1; row--) {
            for (int col = 8; col >= 1; col--) {
                ChessPiece piece = initialBoard.getPiece(new ChessPosition(row, col));
                boolean isLightGrey = (row + col) % 2 == 0; // Check if the square is on an even row and an even column

                if (piece != null) {
                    String pieceSymbol = getPieceSymbol(piece);
                    System.out.print(isLightGrey ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.RESET_BG_COLOR);
                    System.out.print(pieceSymbol);
                } else {
                    // Print an empty space for squares with no pieces
                    System.out.print(isLightGrey ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.RESET_BG_COLOR);
                    System.out.print(EscapeSequences.EMPTY);
                }
            }
            // Reset the background color to black after printing each row
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(); // Move to the next line after printing each row
        }
    }


    private static String getPieceSymbol(ChessPiece piece) {
        // Determine the symbol for the given chess piece
        return switch (piece.getPieceType()) {
            case KING ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }
}

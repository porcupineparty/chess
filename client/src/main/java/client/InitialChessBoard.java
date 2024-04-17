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

        initialBoard.resetBoard();

        System.out.println(EscapeSequences.ERASE_SCREEN);


        System.out.print(" ");
        for (char c = 'A'; c <= 'H'; c++) {
            System.out.print(" " + c + "  ");
        }
        System.out.println();

        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " ");
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

            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(" " + row);
        }

        // Print column labels at the bottom
        System.out.print(" ");
        for (char c = 'A'; c <= 'H'; c++) {
            System.out.print(" " + c + "  ");
        }
        System.out.println();
    }

    public static void printInitialChessBoardReverse() {

        ChessBoard initialBoard = new ChessBoard();

        initialBoard.resetBoard();


        System.out.println(EscapeSequences.ERASE_SCREEN);


        System.out.print(" ");
        for (char c = 'H'; c >= 'A'; c--) {
            System.out.print(" " + c + "  ");
        }
        System.out.println();

        for (int row = 1; row <= 8; row++) {
            System.out.print(row + " ");
            for (int col = 8; col >= 1; col--) {
                ChessPiece piece = initialBoard.getPiece(new ChessPosition(row, col));
                boolean isLightGrey = (row + col) % 2 == 0;

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

            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(" " + row);
        }

        // Print column labels at the bottom
        System.out.print(" ");
        for (char c = 'H'; c >= 'A'; c--) {
            System.out.print(" " + c + "  ");
        }
        System.out.println();
    }

    private static String getPieceSymbol(ChessPiece piece) {

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

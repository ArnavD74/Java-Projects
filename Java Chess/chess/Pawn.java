package chess;

import java.util.ArrayList;
public class Pawn extends ReturnPiece {
    public static String enPassant;

    public Pawn(PieceFile pieceFile, int pieceRank, PieceType pieceType) {
        super();
        this.pieceFile = pieceFile;
        this.pieceRank = pieceRank;
        this.pieceType = pieceType;
    }

    public static boolean isLegalMove(ReturnPiece curr, ReturnPiece next, String des, String move) {

        int currRank = curr.pieceRank;
        int nextRank = Integer.parseInt(des.substring(1));
        char currFile = String.valueOf(curr.pieceFile).charAt(0);
        char nextFile = des.charAt(0);

        // if the user goes out of bounds
        if ((int) nextFile > 104 || (int) nextFile < 97) {
            return false;
        }
        if (nextRank > 8 || nextRank < 1) {
            return false;
        }

        // if the next position is null and rank has to be current +1
        if (next == null) {
            if (curr.pieceType == PieceType.WP) {
                if ((currRank == 2) && (Chess.findPiece(curr.pieceFile, 3) == null)) {
                    if (currRank + 2 == nextRank) {
                        enPassant = Character.toString(currFile) + (Integer.toString(nextRank - 1));
                        if (currFile == nextFile) {
                            return true;
                        }
                    }
                    if (currRank + 1 == nextRank) {
                        if (currFile == nextFile) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    if (currRank + 1 == nextRank) {
                        if (currFile == nextFile) {
                            return true;
                        } else if (des.equals(enPassant)) {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
            }

            if (curr.pieceType == PieceType.BP) {
                if ((currRank == 7) && (Chess.findPiece(curr.pieceFile, 6) == null)) {
                    if (currRank - 2 == nextRank) {
                        enPassant = Character.toString(currFile) + (Integer.toString(nextRank + 1));
                        if (currFile == nextFile) {
                            return true;
                        }
                    }

                    if (currRank - 1 == nextRank) {
                        if (currFile == nextFile) {
                            return true;
                        } else {
                            return false;
                        }
                    }

                } else {
                    if (currRank - 1 == nextRank) {
                        if (currFile == nextFile) {
                            return true;
                        } else if (des.equals(enPassant)) {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
            }

        } else {
            // if next is not null and if it is diagonally from the pawn
            char nextColor = String.valueOf(next.pieceType).charAt(0);
            if (curr.pieceType == PieceType.WP) {
                if (currRank + 1 == nextRank
                        && ((int) currFile + 1 == (int) nextFile || (int) currFile - 1 == (int) nextFile)) {
                    if (nextColor == 'B') {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            if (curr.pieceType == PieceType.BP) {
                if (currRank - 1 == nextRank
                        && ((int) currFile + 1 == (int) nextFile || (int) currFile - 1 == (int) nextFile)) {
                    if (nextColor == 'W') {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static ReturnPlay move(ReturnPiece curr, ReturnPiece next, String des, String promo) {
        ArrayList<ReturnPiece> tempBoardState = new ArrayList<ReturnPiece>();
        for (ReturnPiece rp : Chess.currentBoardState) {
            ReturnPiece tempPiece = new ReturnPiece();
            tempPiece.pieceFile = rp.pieceFile;
            tempPiece.pieceRank = rp.pieceRank;
            tempPiece.pieceType = rp.pieceType;
            tempBoardState.add(tempPiece);
        }
        ReturnPiece tempCurr = Chess.findPiece(curr.pieceFile, curr.pieceRank, tempBoardState);

        ReturnPlay result = new ReturnPlay();

        if (isLegalMove(tempCurr, next, des, promo)) {
            if (next == null && ((des.charAt(0) == String.valueOf(tempCurr.pieceFile).charAt(0) + 1)
                    || (des.charAt(0) == String.valueOf(tempCurr.pieceFile).charAt(0) - 1))) {
                if (tempCurr.pieceType == PieceType.WP) {
                    tempBoardState
                            .remove(Chess.findPiece(ReturnPiece.PieceFile.valueOf(Character.toString(des.charAt(0))),
                                    Integer.parseInt(Character.toString(des.charAt(1))) - 1, tempBoardState));
                    enPassant = "";
                }
                if (tempCurr.pieceType == PieceType.BP) {
                    tempBoardState
                            .remove(Chess.findPiece(ReturnPiece.PieceFile.valueOf(Character.toString(des.charAt(0))),
                                    Integer.parseInt(Character.toString(des.charAt(1))) + 1, tempBoardState));
                    enPassant = "";
                }
            }
            // setting the next position to the current pawn
            ReturnPiece.PieceFile[] possiblePieceFiles = ReturnPiece.PieceFile.values();
            char[] possibleCharFiles = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
            for (int i = 0; i < possiblePieceFiles.length; i++) { // setting the file
                if (possibleCharFiles[i] == des.charAt(0)) {
                    tempCurr.pieceFile = possiblePieceFiles[i];
                }
            }

            tempCurr.pieceRank = Integer.parseInt(des.substring(1)); // setting the rank

            // remove the captured piece
            if (next != null) {
                tempBoardState.remove(next);
            }

            // promotions
            if (tempCurr.pieceRank == 8 && tempCurr.pieceType == PieceType.WP) {
                if (promo.equals("N")) {
                    tempCurr.pieceType = PieceType.WN;
                } else if (promo.equals("B")) {
                    tempCurr.pieceType = PieceType.WB;
                } else if (promo.equals("R ")) {
                    tempCurr.pieceType = PieceType.WR;
                } else if (promo.equals("Q")) {
                    tempCurr.pieceType = PieceType.WQ;
                } else if (promo.equals("")) {
                    tempCurr.pieceType = PieceType.WQ;
                } else {
                    tempCurr.pieceType = PieceType.WQ;
                }
            }

            if (tempCurr.pieceRank == 1 && tempCurr.pieceType == PieceType.BP) {
                if (promo.equals("N")) {
                    tempCurr.pieceType = PieceType.BN;
                } else if (promo.equals("B")) {
                    tempCurr.pieceType = PieceType.BB;
                } else if (promo.equals("R ")) {
                    tempCurr.pieceType = PieceType.BR;
                } else if (promo.equals("Q")) {
                    tempCurr.pieceType = PieceType.BQ;
                } else if (promo.equals("")) {
                    tempCurr.pieceType = PieceType.WQ;
                } else {
                    tempCurr.pieceType = PieceType.BQ;
                }
            }

            // check if the pawn is adjacent to a king
            if (tempCurr.pieceType == PieceType.WP) {
                ReturnPiece checkL = null;
                ReturnPiece checkR = null;
                if (tempCurr.pieceFile.ordinal() > 0 && tempCurr.pieceRank < 8) {
                    checkL = Chess.findPiece(ReturnPiece.PieceFile.values()[tempCurr.pieceFile.ordinal() - 1],
                            tempCurr.pieceRank + 1);
                }
                if (tempCurr.pieceFile.ordinal() < 7 && tempCurr.pieceRank < 8) {
                    checkR = Chess.findPiece(ReturnPiece.PieceFile.values()[tempCurr.pieceFile.ordinal() + 1],
                            tempCurr.pieceRank + 1);

                }
                if (checkL != null) {
                    if (checkL.pieceType == PieceType.BK) {
                        result.message = ReturnPlay.Message.CHECK;
                    }
                }
                if (checkR != null) {
                    if (checkR.pieceType == PieceType.BK) {
                        result.message = ReturnPlay.Message.CHECK;
                    }
                }
            }

            if (tempCurr.pieceType == PieceType.BP) {
                ReturnPiece checkL = null;
                ReturnPiece checkR = null;
                if (tempCurr.pieceFile.ordinal() > 0) {
                    checkL = Chess.findPiece(ReturnPiece.PieceFile.values()[tempCurr.pieceFile.ordinal() - 1],
                            tempCurr.pieceRank - 1);
                }
                if (tempCurr.pieceFile.ordinal() < 7) {
                    checkR = Chess.findPiece(ReturnPiece.PieceFile.values()[tempCurr.pieceFile.ordinal() + 1],
                            tempCurr.pieceRank - 1);
                }
                if (checkL != null) {
                    if (checkL.pieceType == PieceType.WK) {
                        result.message = ReturnPlay.Message.CHECK;
                        result.piecesOnBoard = tempBoardState;
                        return result;
                    }
                }
                if (checkR != null) {
                    if (checkR.pieceType == PieceType.WK) {
                        result.message = ReturnPlay.Message.CHECK;
                        result.piecesOnBoard = tempBoardState;
                        return result;
                    }
                }
            }

            // if tempboardstate has no checks, then set currentboardstate to tempboardstate

            if (Chess.currPlayer == Chess.Player.white) {
                if (Chess.isCheck(tempBoardState, 'W')) {
                    result.message = ReturnPlay.Message.ILLEGAL_MOVE;
                    result.piecesOnBoard = Chess.currentBoardState;
                    return result;
                } else {
                    if (Chess.isCheck(tempBoardState, 'B')) {
                        Chess.currentBoardState = tempBoardState;
                        result.message = ReturnPlay.Message.CHECK;
                        result.piecesOnBoard = Chess.currentBoardState;
                        return result;
                    } else {
                        Chess.currentBoardState = tempBoardState;
                        result.message = null;
                        result.piecesOnBoard = Chess.currentBoardState;
                        return result;
                    }
                }
            } else if (Chess.currPlayer == Chess.Player.black) {
                if (Chess.isCheck(tempBoardState, 'B')) {
                    result.message = ReturnPlay.Message.ILLEGAL_MOVE;
                    result.piecesOnBoard = Chess.currentBoardState;
                    return result;
                } else {
                    if (Chess.isCheck(tempBoardState, 'W')) {
                        Chess.currentBoardState = tempBoardState;
                        result.message = ReturnPlay.Message.CHECK;
                        result.piecesOnBoard = Chess.currentBoardState;
                        return result;
                    } else {
                        Chess.currentBoardState = tempBoardState;
                        result.message = null;
                        result.piecesOnBoard = Chess.currentBoardState;
                        return result;
                    }
                }
            }
        }

        result.message = ReturnPlay.Message.ILLEGAL_MOVE;
        result.piecesOnBoard = Chess.currentBoardState;
        return result;
    }
}
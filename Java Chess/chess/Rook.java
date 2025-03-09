package chess;

import java.util.ArrayList;

public class Rook extends ReturnPiece {

  public static boolean isCastling = false;

  public Rook(PieceFile pieceFile, int pieceRank, PieceType pieceType) {
    super();
    this.pieceFile = pieceFile;
    this.pieceRank = pieceRank;
    this.pieceType = pieceType;
  }

  public static boolean isLegalMove(ReturnPiece curr, ReturnPiece next, String des) {
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

    if (currRank == nextRank || currFile == nextFile) {

      // if there is a piece in the way
      if (currRank == nextRank) {
        int fileDiff = (int) nextFile - (int) currFile;
        int fileDir = fileDiff / Math.abs(fileDiff);
        char file = (char) ((int) currFile + fileDir);
        while (file != nextFile) {
          if (Chess.findPiece(PieceFile.valueOf(String.valueOf(file)), currRank) != null) {
            return false;
          }
          file = (char) ((int) file + fileDir);
        }
      } else {
        int rankDiff = nextRank - currRank;
        int rankDir = rankDiff / Math.abs(rankDiff);
        int rank = currRank + rankDir;
        while (rank != nextRank) {
          if (Chess.findPiece(curr.pieceFile, rank) != null) {
            return false;
          }
          rank += rankDir;
        }
      }
      if (next != null) {
        if (String.valueOf(next.pieceType).charAt(0) == String.valueOf(curr.pieceType).charAt(0)) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  public static ReturnPlay move(ReturnPiece curr, ReturnPiece next, String des) {
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

    if (isLegalMove(curr, next, des)) {

      if (curr.pieceType == PieceType.WR && curr.pieceFile == PieceFile.valueOf("a")) {
        Chess.whiteRookLmoved = true;
      }
      if (curr.pieceType == PieceType.WR && curr.pieceFile == PieceFile.valueOf("h")) {
        Chess.whiteRookRmoved = true;
      }
      if (curr.pieceType == PieceType.BR && curr.pieceFile == PieceFile.valueOf("a")) {
        Chess.blackRookLmoved = true;
      }
      if (curr.pieceType == PieceType.BR && curr.pieceFile == PieceFile.valueOf("h")) {
        Chess.blackRookRmoved = true;
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

      // capture the piece if next is not null
      if (next != null) {
        tempBoardState.remove(next);
      }
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
    } else {
      result.message = ReturnPlay.Message.ILLEGAL_MOVE;
      result.piecesOnBoard = tempBoardState;
      return result;
    }
    result.message = ReturnPlay.Message.ILLEGAL_MOVE;
    result.piecesOnBoard = tempBoardState;
    return result;
  }
}

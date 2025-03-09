package chess;

import java.util.ArrayList;

public class Knight extends ReturnPiece {
  public Knight(PieceFile pieceFile, int pieceRank, PieceType pieceType) {
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

    if ((currRank + 2 == nextRank || currRank - 2 == nextRank)
        && (currFile + 1 == nextFile || currFile - 1 == nextFile)) {
      if (next != null) {
        if (String.valueOf(next.pieceType).charAt(0) == String.valueOf(curr.pieceType).charAt(0)) {
          return false;
        }
      }
      return true;
    } else if ((currRank + 1 == nextRank || currRank - 1 == nextRank)
        && (currFile + 2 == nextFile || currFile - 2 == nextFile)) {
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

    //copy the board into tempBoardState so that moves that result in check will not be saved
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

      // check if the move puts the player in check
      if (Chess.currPlayer == Chess.Player.white) {
        if (Chess.isCheck(tempBoardState, 'W')) {
          result.message = ReturnPlay.Message.ILLEGAL_MOVE;
          result.piecesOnBoard = Chess.currentBoardState;
          return result;
        } else {
          // check if the move puts the opponent in check
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
      result.piecesOnBoard = Chess.currentBoardState;
      return result;
    }
    result.message = null;
    result.piecesOnBoard = Chess.currentBoardState;
    return result;
  }
}

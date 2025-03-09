package chess;

import java.util.ArrayList;

public class King extends ReturnPiece {
  public King(PieceFile pieceFile, int pieceRank, PieceType pieceType) {
    super();
    this.pieceFile = pieceFile;
    this.pieceRank = pieceRank;
    this.pieceType = pieceType;
  }

  public static boolean isCastling(ReturnPiece curr, ReturnPiece next, String des) {

    int currRank = curr.pieceRank;
    char currFile = String.valueOf(curr.pieceFile).charAt(0);

    // king can not move to its own current position
    if (curr.pieceFile == PieceFile.valueOf(des.substring(0, 1))
        && curr.pieceRank == Integer.parseInt(des.substring(1))) {
      return false;
    }

    // e1 g1
    if (Chess.whiteKingMoved == false && Chess.whiteRookRmoved == false) {
      if (curr.pieceType == PieceType.WK && currRank == 1 && currFile == 'e') {
        if (des.equals("g1")) {
          if (Chess.findPiece(PieceFile.valueOf("f"), 1) == null
              && Chess.findPiece(PieceFile.valueOf("g"), 1) == null) {
            // ensure that none of the tiles that the king passes through are under attack
            // create a temporary boardstate
            ArrayList<ReturnPiece> tempBoardState = new ArrayList<ReturnPiece>();
            for (ReturnPiece rp : Chess.currentBoardState) {
              ReturnPiece tempPiece = new ReturnPiece();
              tempPiece.pieceFile = rp.pieceFile;
              tempPiece.pieceRank = rp.pieceRank;
              tempPiece.pieceType = rp.pieceType;
              tempBoardState.add(tempPiece);
            }
            // move the king to f1
            ReturnPiece tempCurr = Chess.findPiece(curr.pieceFile, curr.pieceRank, tempBoardState);
            tempCurr.pieceFile = PieceFile.valueOf("f");
            tempCurr.pieceRank = 1;
            // check if putting the king in d1 would put him in check
            if (Chess.isCheck(tempBoardState, 'W')) {
              return false;
            }
            // move the king to f1
            tempCurr.pieceFile = PieceFile.valueOf("g");
            tempCurr.pieceRank = 1;
            // check if putting king in c1 would put him in check
            if (Chess.isCheck(tempBoardState, 'W')) {
              return false;
            }
            return true;
          }
        }
      }
    }

    // e1 c1
    if (Chess.whiteKingMoved == false && Chess.whiteRookLmoved == false) {
      if (curr.pieceType == PieceType.WK && currRank == 1 && currFile == 'e') {
        if (des.equals("c1")) {
          if (Chess.findPiece(PieceFile.valueOf("b"), 1) == null
              && Chess.findPiece(PieceFile.valueOf("c"), 1) == null
              && Chess.findPiece(PieceFile.valueOf("d"), 1) == null) {
            // ensure that none of the tiles that the king passes through are under attack
            // create a temporary boardstate
            ArrayList<ReturnPiece> tempBoardState = new ArrayList<ReturnPiece>();
            for (ReturnPiece rp : Chess.currentBoardState) {
              ReturnPiece tempPiece = new ReturnPiece();
              tempPiece.pieceFile = rp.pieceFile;
              tempPiece.pieceRank = rp.pieceRank;
              tempPiece.pieceType = rp.pieceType;
              tempBoardState.add(tempPiece);
            }
            // move the king to d1
            ReturnPiece tempCurr = Chess.findPiece(curr.pieceFile, curr.pieceRank, tempBoardState);
            tempCurr.pieceFile = PieceFile.valueOf("d");
            tempCurr.pieceRank = 1;
            // check if putting the king in d1 would put him in check
            if (Chess.isCheck(tempBoardState, 'W')) {
              return false;
            }
            // move the king to c1
            tempCurr.pieceFile = PieceFile.valueOf("c");
            tempCurr.pieceRank = 1;
            // check if putting king in c1 would put him in check
            if (Chess.isCheck(tempBoardState, 'W')) {
              return false;
            }
            return true;
          }
        }
      }
    }

    // e8 g8
    if (Chess.blackKingMoved == false && Chess.blackRookRmoved == false) {
      if (curr.pieceType == PieceType.BK && currRank == 8 && currFile == 'e') {
        if (des.equals("g8")) {
          if (Chess.findPiece(PieceFile.valueOf("f"), 8) == null
              && Chess.findPiece(PieceFile.valueOf("g"), 8) == null) {
            // ensure that none of the tiles that the king passes through are under attack
            // create a temporary boardstate
            ArrayList<ReturnPiece> tempBoardState = new ArrayList<ReturnPiece>();
            for (ReturnPiece rp : Chess.currentBoardState) {
              ReturnPiece tempPiece = new ReturnPiece();
              tempPiece.pieceFile = rp.pieceFile;
              tempPiece.pieceRank = rp.pieceRank;
              tempPiece.pieceType = rp.pieceType;
              tempBoardState.add(tempPiece);
            }
            // move the king to f8
            ReturnPiece tempCurr = Chess.findPiece(curr.pieceFile, curr.pieceRank, tempBoardState);
            tempCurr.pieceFile = PieceFile.valueOf("f");
            tempCurr.pieceRank = 8;
            // check if putting the king in d1 would put him in check
            if (Chess.isCheck(tempBoardState, 'B')) {
              return false;
            }
            // move the king to g8
            tempCurr.pieceFile = PieceFile.valueOf("g");
            tempCurr.pieceRank = 8;
            // check if putting king in c1 would put him in check
            if (Chess.isCheck(tempBoardState, 'B')) {
              return false;
            }
            return true;
          }
        }
      }
    }

    // e8 c8
    if (Chess.blackKingMoved == false && Chess.blackRookLmoved == false) {
      if (curr.pieceType == PieceType.BK && currRank == 8 && currFile == 'e') {
        if (des.equals("c8")) {
          if (Chess.findPiece(PieceFile.valueOf("b"), 8) == null
              && Chess.findPiece(PieceFile.valueOf("c"), 8) == null
              && Chess.findPiece(PieceFile.valueOf("d"), 8) == null) {
            // ensure that none of the tiles that the king passes through are under attack
            // create a temporary boardstate
            ArrayList<ReturnPiece> tempBoardState = new ArrayList<ReturnPiece>();
            for (ReturnPiece rp : Chess.currentBoardState) {
              ReturnPiece tempPiece = new ReturnPiece();
              tempPiece.pieceFile = rp.pieceFile;
              tempPiece.pieceRank = rp.pieceRank;
              tempPiece.pieceType = rp.pieceType;
              tempBoardState.add(tempPiece);
            }
            // move the king to d8
            ReturnPiece tempCurr = Chess.findPiece(curr.pieceFile, curr.pieceRank, tempBoardState);
            tempCurr.pieceFile = PieceFile.valueOf("d");
            tempCurr.pieceRank = 8;
            // check if putting the king in d1 would put him in check
            if (Chess.isCheck(tempBoardState, 'B')) {
              return false;
            }
            // move the king to c8
            tempCurr.pieceFile = PieceFile.valueOf("c");
            tempCurr.pieceRank = 8;
            // check if putting king in c1 would put him in check
            if (Chess.isCheck(tempBoardState, 'B')) {
              return false;
            }
            return true;
          }
        }
      }
    }

    return false;
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

    // king movement implementation
    if (Math.abs(currRank - nextRank) <= 1 && Math.abs((int) currFile - (int) nextFile) <= 1) {
      // //System.out.println("King can move from " + curr.pieceFile + curr.pieceRank
      // +
      // " to " + des);
      if (next != null) {
        // //System.out.println("there is a piece at the next location");
        if (String.valueOf(curr.pieceType).charAt(0) == String.valueOf(next.pieceType).charAt(0)) {
          // //System.out.println("the piece at the next location is of the same color");
          return false;
        }
      }
      return true;
    }
    return false;
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
    char currFile = String.valueOf(curr.pieceFile).charAt(0);

    // System.out.println("black king moved: " + Chess.blackKingMoved);
    // System.out.println("black rook left moved: " + Chess.blackRookLmoved);
    // System.out.println("black rook right moved: " + Chess.blackRookRmoved);
    // System.out.println("white king moved: " + Chess.whiteKingMoved);
    // System.out.println("white rook left moved: " + Chess.whiteRookLmoved);
    // System.out.println("white rook right moved: " + Chess.whiteRookRmoved);
    // System.out.println("Is castling: " + isCastling(curr, next, des));
    // System.out.println("Is legal move: " + isLegalMove(curr, next, des));

    if (isCastling(curr, next, des)) {

      // e1 g1
      if (curr.pieceType == PieceType.WK && curr.pieceRank == 1 && currFile == 'e') {
        if (des.equals("g1")) {
          ReturnPiece rook = Chess.findPiece(PieceFile.valueOf("h"), 1, tempBoardState);
          if (rook != null) {
            rook.pieceFile = PieceFile.valueOf("f");
            rook.pieceRank = 1;
          }
          tempCurr.pieceFile = PieceFile.valueOf("g");
          tempCurr.pieceRank = 1;
          result.message = null;
          result.piecesOnBoard = tempBoardState;
          Chess.whiteKingMoved = true;
        }
      }

      // e1 c1
      if (curr.pieceType == PieceType.WK && curr.pieceRank == 1 && currFile == 'e') {
        if (des.equals("c1")) {
          ReturnPiece rook = Chess.findPiece(PieceFile.valueOf("a"), 1, tempBoardState);
          if (rook != null) {
            rook.pieceFile = PieceFile.valueOf("d");
            rook.pieceRank = 1;
          }
          tempCurr.pieceFile = PieceFile.valueOf("c");
          tempCurr.pieceRank = 1;
          result.message = null;
          result.piecesOnBoard = tempBoardState;
          Chess.whiteKingMoved = true;
        }
      }

      // e8 g8
      if (curr.pieceType == PieceType.BK && curr.pieceRank == 8 && currFile == 'e') {
        if (des.equals("g8")) {
          ReturnPiece rook = Chess.findPiece(PieceFile.valueOf("h"), 8, tempBoardState);
          if (rook != null) {
            rook.pieceFile = PieceFile.valueOf("f");
            rook.pieceRank = 8;
          }
          tempCurr.pieceFile = PieceFile.valueOf("g");
          tempCurr.pieceRank = 8;
          result.message = null;
          result.piecesOnBoard = tempBoardState;
          Chess.blackKingMoved = true;
        }
      }

      // e8 c8
      if (curr.pieceType == PieceType.BK && curr.pieceRank == 8 && currFile == 'e') {
        if (des.equals("c8")) {
          ReturnPiece rook = Chess.findPiece(PieceFile.valueOf("a"), 8, tempBoardState);
          if (rook != null) {
            rook.pieceFile = PieceFile.valueOf("d");
            rook.pieceRank = 8;
          }
          tempCurr.pieceFile = PieceFile.valueOf("c");
          tempCurr.pieceRank = 8;
          result.message = null;
          result.piecesOnBoard = tempBoardState;
          Chess.blackKingMoved = true;
        }
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

      result.message = null;
      result.piecesOnBoard = tempBoardState;
      return result;

    } else if (isLegalMove(curr, next, des)) {
      if (curr.pieceType == PieceType.WK) {
        Chess.whiteKingMoved = true;
      }
      if (curr.pieceType == PieceType.BK) {
        Chess.blackKingMoved = true;
      }
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
    result.message = null;
    result.piecesOnBoard = tempBoardState;
    return result;
  }
}
// Arnav Dashaputra and Mia Wu
package chess;

import java.util.ArrayList;

class ReturnPiece {
	static enum PieceType {
		WP, WR, WN, WB, WQ, WK,
		BP, BR, BN, BB, BK, BQ
	};

	static enum PieceFile {
		a, b, c, d, e, f, g, h
	};

	PieceType pieceType;
	PieceFile pieceFile;
	int pieceRank; // 1..8

	public String toString() {
		return "" + pieceFile + pieceRank + ":" + pieceType;
	}

	public boolean equals(Object other) {
		if (other == null || !(other instanceof ReturnPiece)) {
			return false;
		}
		ReturnPiece otherPiece = (ReturnPiece) other;
		return pieceType == otherPiece.pieceType &&
				pieceFile == otherPiece.pieceFile &&
				pieceRank == otherPiece.pieceRank;
	}
}

class ReturnPlay {
	enum Message {
		ILLEGAL_MOVE, DRAW,
		RESIGN_BLACK_WINS, RESIGN_WHITE_WINS,
		CHECK, CHECKMATE_BLACK_WINS, CHECKMATE_WHITE_WINS,
		STALEMATE
	};

	ArrayList<ReturnPiece> piecesOnBoard;
	Message message;
}

public class Chess {

	enum Player {
		white, black
	}

	/**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for
	 *         details of
	 *         the contents of the returned ReturnPlay instance.
	 */

	public static Player currPlayer;
	public static boolean whiteRookLmoved = false;
	public static boolean whiteRookRmoved = false;
	public static boolean whiteKingMoved = false;
	public static boolean blackRookLmoved = false;
	public static boolean blackRookRmoved = false;
	public static boolean blackKingMoved = false;
	public static ArrayList<ReturnPiece> currentBoardState = new ArrayList<>();

	public static ReturnPlay play(String move) {
		ReturnPlay result = new ReturnPlay();
		result.message = null;
		result.piecesOnBoard = currentBoardState;
		move = move.trim();

		if (move.equals("reset")) { // reset
			start();
		} else if (move.equals("resign")) { // resign
			if (currPlayer == Player.white) {
				result.message = ReturnPlay.Message.RESIGN_BLACK_WINS;
				return result;
			} else {
				result.message = ReturnPlay.Message.RESIGN_WHITE_WINS;
				return result;
			}
		}

		String[] moves = move.split(" "); // a2 a3
		String currPos = moves[0]; // "a2"
		String nextPos = moves[1]; // "a3"
		String promo = "";

		// if moving a pawn to an empty square
		ReturnPiece.PieceFile currFile = ReturnPiece.PieceFile.valueOf(currPos.substring(0, 1)); // "a"
		int currRank = Integer.parseInt(currPos.substring(1)); // "2"
		ReturnPiece.PieceFile nextFile = ReturnPiece.PieceFile.valueOf(nextPos.substring(0, 1)); // "a"
		int nextRank = Integer.parseInt(nextPos.substring(1)); // "3"

		ReturnPiece currentPiece = findPiece(currFile, currRank); // gets piece at current position
		ReturnPiece nextPiece = findPiece(nextFile, nextRank); // gets piece of destination

		// illegal move if the user is trying to move a empty spot
		if (currentPiece == null) {
			result.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return result;
		}

		// check if the color of the piece that user is trying to move matches the
		// current player
		char color = String.valueOf(currentPiece.pieceType).charAt(0);
		if (color == 'W' && currPlayer == Player.black) {
			result.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return result;
		}
		if (color == 'B' && currPlayer == Player.white) {
			result.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return result;
		}

		if (currentPiece.pieceType == ReturnPiece.PieceType.WP || currentPiece.pieceType == ReturnPiece.PieceType.BP) {

			if (moves.length == 3 && !moves[2].equals("draw?")) {
				promo = moves[2];
			} else if (moves.length == 2) {
				promo = "";
			}
			result = Pawn.move(currentPiece, nextPiece, moves[1], promo);
			if (result.message == ReturnPlay.Message.ILLEGAL_MOVE) {
				return result;
			} else if (result.message == ReturnPlay.Message.CHECK) {
				char oppPlayer = currPlayer == Player.white ? 'B' : 'W';
				if (isCheckmate(currentBoardState, oppPlayer)) {
					result.message = currPlayer == Player.white ? ReturnPlay.Message.CHECKMATE_WHITE_WINS
							: ReturnPlay.Message.CHECKMATE_BLACK_WINS;
					return result;
				}
			}
		} else if (currentPiece.pieceType == ReturnPiece.PieceType.WN
				|| currentPiece.pieceType == ReturnPiece.PieceType.BN) {
			result = Knight.move(currentPiece, nextPiece, moves[1]);
			if (result.message == ReturnPlay.Message.ILLEGAL_MOVE) {
				return result;
			} else if (result.message == ReturnPlay.Message.CHECK) {
				char oppPlayer = currPlayer == Player.white ? 'B' : 'W';
				if (isCheckmate(currentBoardState, oppPlayer)) {
					result.message = currPlayer == Player.white ? ReturnPlay.Message.CHECKMATE_WHITE_WINS
							: ReturnPlay.Message.CHECKMATE_BLACK_WINS;
					return result;
				}
			}
		} else if (currentPiece.pieceType == ReturnPiece.PieceType.WK
				|| currentPiece.pieceType == ReturnPiece.PieceType.BK) {
			result = King.move(currentPiece, nextPiece, moves[1]);
			if (result.message == ReturnPlay.Message.ILLEGAL_MOVE) {
				return result;
			} else if (result.message == ReturnPlay.Message.CHECK) {
				char oppPlayer = currPlayer == Player.white ? 'B' : 'W';
				if (isCheckmate(currentBoardState, oppPlayer)) {
					result.message = currPlayer == Player.white ? ReturnPlay.Message.CHECKMATE_WHITE_WINS
							: ReturnPlay.Message.CHECKMATE_BLACK_WINS;
					return result;
				}
			}
		} else if (currentPiece.pieceType == ReturnPiece.PieceType.WR
				|| currentPiece.pieceType == ReturnPiece.PieceType.BR) {
			result = Rook.move(currentPiece, nextPiece, moves[1]);
			if (result.message == ReturnPlay.Message.ILLEGAL_MOVE) {
				return result;
			} else if (result.message == ReturnPlay.Message.CHECK) {
				char oppPlayer = currPlayer == Player.white ? 'B' : 'W';
				if (isCheckmate(currentBoardState, oppPlayer)) {
					result.message = currPlayer == Player.white ? ReturnPlay.Message.CHECKMATE_WHITE_WINS
							: ReturnPlay.Message.CHECKMATE_BLACK_WINS;
					return result;
				}
			}
		} else if (currentPiece.pieceType == ReturnPiece.PieceType.WB
				|| currentPiece.pieceType == ReturnPiece.PieceType.BB) {
			result = Bishop.move(currentPiece, nextPiece, moves[1]);
			if (result.message == ReturnPlay.Message.ILLEGAL_MOVE) {
				return result;
			} else if (result.message == ReturnPlay.Message.CHECK) {
				char oppPlayer = currPlayer == Player.white ? 'B' : 'W';
				if (isCheckmate(currentBoardState, oppPlayer)) {
					result.message = currPlayer == Player.white ? ReturnPlay.Message.CHECKMATE_WHITE_WINS
							: ReturnPlay.Message.CHECKMATE_BLACK_WINS;
					return result;
				}
			}
		} else if (currentPiece.pieceType == ReturnPiece.PieceType.WQ
				|| currentPiece.pieceType == ReturnPiece.PieceType.BQ) {
			result = Queen.move(currentPiece, nextPiece, moves[1]);
			if (result.message == ReturnPlay.Message.ILLEGAL_MOVE) {
				return result;
			} else if (result.message == ReturnPlay.Message.CHECK) {
				char oppPlayer = currPlayer == Player.white ? 'B' : 'W';
				if (isCheckmate(currentBoardState, oppPlayer)) {
					result.message = currPlayer == Player.white ? ReturnPlay.Message.CHECKMATE_WHITE_WINS
							: ReturnPlay.Message.CHECKMATE_BLACK_WINS;
					return result;
				}
			}
		}

		// draw
		if (moves.length == 3 && moves[2].equals("draw?")) {
			result.message = ReturnPlay.Message.DRAW;
			return result;
		}

		// update the current player
		if (currPlayer == Player.white) {
			currPlayer = Player.black;
		} else {
			currPlayer = Player.white;
		}
		return result;
	}

	static ReturnPiece findPiece(ReturnPiece.PieceFile file, int rank) {
		for (ReturnPiece piece : currentBoardState) {
			if (piece.pieceFile == file && piece.pieceRank == rank) {
				return piece;
			}
		}
		return null;
	}

	static ReturnPiece findPiece(ReturnPiece.PieceFile file, int rank, ArrayList<ReturnPiece> boardState) {
		for (ReturnPiece piece : boardState) {
			if (piece.pieceFile == file && piece.pieceRank == rank) {
				return piece;
			}
		}
		return null;
	}

	public static boolean isCheck(ArrayList<ReturnPiece> boardState, char kingColor) {
		ReturnPiece.PieceFile kingFile = null;
		int kingRank = 0;
		for (ReturnPiece piece : boardState) {
			if (kingColor == 'W') {
				if (piece.pieceType == ReturnPiece.PieceType.WK) {
					kingFile = piece.pieceFile;
					kingRank = piece.pieceRank;
				}
			} else if (kingColor == 'B') {
				if (piece.pieceType == ReturnPiece.PieceType.BK) {
					kingFile = piece.pieceFile;
					kingRank = piece.pieceRank;
				}
			}
		}

		// System.out.println("The king color being checked for check is: " + kingColor
		// + " at " + kingFile + kingRank);

		// check front two corners for pawn
		if (kingColor == 'W') {

			ReturnPiece rightFront = null;
			ReturnPiece leftFront = null;
			// file+1 and rank+1
			if (kingFile.ordinal() + 1 < 8 && kingRank + 1 <= 8) {
				rightFront = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + 1], kingRank + 1,
						boardState);
			}
			// file-1 and rank+1
			if (kingFile.ordinal() - 1 >= 0 && kingRank + 1 <= 8) {
				leftFront = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - 1], kingRank + 1,
						boardState);
			}

			if (rightFront != null) {
				if (rightFront.pieceType == ReturnPiece.PieceType.BP) {
					// System.out.println(" right front pawn checks it");
					return true;
				}
			}
			if (leftFront != null) {
				if (leftFront.pieceType == ReturnPiece.PieceType.BP) {
					// System.out.println(" left front pawn checks it");
					return true;
				}
			}
		} else if (kingColor == 'B') {

			ReturnPiece rightBottom = null;
			ReturnPiece leftBottom = null;
			// file+1 and rank-1
			if (kingFile.ordinal() + 1 < 8 && kingRank - 1 > 0) {
				rightBottom = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + 1], kingRank - 1,
						boardState);
			}
			// file-1 and rank-1
			if (kingFile.ordinal() - 1 >= 0 && kingRank - 1 <= 8) {
				leftBottom = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - 1], kingRank - 1,
						boardState);
			}

			if (rightBottom != null) {
				if (rightBottom.pieceType == ReturnPiece.PieceType.WP) {
					// System.out.println(" right bottom pawn checks it");
					return true;
				}
			}
			if (leftBottom != null) {
				if (leftBottom.pieceType == ReturnPiece.PieceType.WP) {
					// System.out.println(" left bottom pawn checks it");
					return true;
				}
			}
		}

		// check the 8 possible spaces for knight
		ReturnPiece kTopLeftTop = null;
		ReturnPiece kTopRightTop = null;
		ReturnPiece kTopLeftSide = null;
		ReturnPiece kTopRightSide = null;
		ReturnPiece kBotLeftBot = null;
		ReturnPiece kBotRightBot = null;
		ReturnPiece kBotLeftSide = null;
		ReturnPiece kBotRightSide = null;

		if (kingFile.ordinal() - 1 >= 0 && kingRank + 2 <= 8)
			kTopLeftTop = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - 1], kingRank + 2,
					boardState);

		if (kingFile.ordinal() + 1 < 8 && kingRank + 2 < 8)
			kTopRightTop = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + 1], kingRank + 2,
					boardState);

		if (kingFile.ordinal() - 2 >= 0 && kingRank + 1 < 8)
			kTopLeftSide = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - 2], kingRank + 1,
					boardState);
		if (kingFile.ordinal() + 2 < 8 && kingRank + 1 < 8)
			kTopRightSide = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + 2], kingRank + 1,
					boardState);

		if (kingFile.ordinal() - 1 >= 0 && kingRank - 2 >= 0)
			kBotLeftBot = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - 1], kingRank - 2,
					boardState);

		if (kingFile.ordinal() + 1 < 8 && kingRank - 2 >= 0)
			kBotRightBot = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + 1], kingRank - 2,
					boardState);

		if (kingFile.ordinal() - 2 >= 0 && kingRank - 1 >= 0)
			kBotLeftSide = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - 2], kingRank - 1,
					boardState);

		if (kingFile.ordinal() + 2 < 8 && kingRank - 1 >= 0)
			kBotRightSide = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + 2], kingRank - 1,
					boardState);

		if (kTopLeftTop != null) {
			if (kingColor == 'W' && (kTopLeftTop.pieceType == ReturnPiece.PieceType.BN)) {
				// System.out.println(" top left top knight checks it");
				return true;
			} else if (kingColor == 'B' && (kTopLeftTop.pieceType == ReturnPiece.PieceType.WN)) {
				// System.out.println(" top left top knight checks it");
				return true;
			}
		}
		if (kTopRightTop != null) {
			if (kingColor == 'W' && (kTopRightTop.pieceType == ReturnPiece.PieceType.BN)) {
				// System.out.println(" top right top knight checks it");
				return true;
			}
			if (kingColor == 'B' && (kTopRightTop.pieceType == ReturnPiece.PieceType.WN)) {
				// System.out.println(" top right top knight checks it");
				return true;
			}
		}
		if (kTopLeftSide != null) {
			if (kingColor == 'W' && (kTopLeftSide.pieceType == ReturnPiece.PieceType.BN)) {
				// System.out.println(" top left side knight checks it");
				return true;
			} else if (kingColor == 'B' && (kTopLeftSide.pieceType == ReturnPiece.PieceType.WN)) {
				// System.out.println(" top left side knight checks it");
				return true;
			}
		}
		if (kTopRightSide != null) {
			if (kingColor == 'W' && (kTopRightSide.pieceType == ReturnPiece.PieceType.BN)) {
				return true;
			} else if (kingColor == 'B' && (kTopRightSide.pieceType == ReturnPiece.PieceType.WN)) {
				// System.out.println(" top right side knight checks it");
				return true;
			}
		}
		if (kBotLeftBot != null) {
			if (kingColor == 'W' && (kBotLeftBot.pieceType == ReturnPiece.PieceType.BN)) {
				return true;
			} else if (kingColor == 'B' && (kBotLeftBot.pieceType == ReturnPiece.PieceType.WN)) {
				// System.out.println(" bottom left bottom knight checks it");
				return true;
			}
		}
		if (kBotRightBot != null) {
			if (kingColor == 'W' && (kBotRightBot.pieceType == ReturnPiece.PieceType.BN)) {
				// System.out.println(" bottom right bottom knight checks it");
				return true;
			} else if (kingColor == 'B' && (kBotRightBot.pieceType == ReturnPiece.PieceType.WN)) {
				// System.out.println(" bottom right bottom knight checks it");
				return true;
			}
		}
		if (kBotLeftSide != null) {
			if (kingColor == 'W' && (kBotLeftSide.pieceType == ReturnPiece.PieceType.BN)) {
				// System.out.println(" bottom left side knight checks it");ReturnPiece.
				return true;
			} else if (kingColor == 'B' && (kBotLeftSide.pieceType == ReturnPiece.PieceType.WN)) {
				// System.out.println(" bottom left side knight checks it");
				return true;
			}
		}
		if (kBotRightSide != null) {
			if (kingColor == 'W' && (kBotRightSide.pieceType == ReturnPiece.PieceType.BN)) {
				// System.out.println(" bottom right side knight checks it");
				return true;
			} else if (kingColor == 'B' && (kBotRightSide.pieceType == ReturnPiece.PieceType.WN)) {
				// System.out.println(" bottom right side knight checks it");
				return true;
			}
		}

		// check diagonals for bishop and queen
		boolean topRightChecked = false;
		boolean topLeftChecked = false;
		boolean botRightChecked = false;
		boolean botLeftChecked = false;

		for (int i = 1; i < 8; i++) {
			ReturnPiece topRight = null;
			ReturnPiece topLeft = null;
			ReturnPiece botRight = null;
			ReturnPiece botLeft = null;

			if (kingFile.ordinal() + i < 8 && kingRank + i <= 8) {
				topRight = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + i], kingRank + i,
						boardState);
			}
			if (kingFile.ordinal() - i >= 0 && kingRank + i <= 8) {
				topLeft = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - i], kingRank + i,
						boardState);
			}
			if (kingFile.ordinal() + i < 8 && kingRank - i > 0) {
				botRight = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + i], kingRank - i,
						boardState);
			}
			if (kingFile.ordinal() - i >= 0 && kingRank - i > 0) {
				botLeft = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - i], kingRank - i,
						boardState);
			}

			if (topRight != null && topRightChecked == false) {
				topRightChecked = true;
				if (kingColor == 'W' && (topRight.pieceType == ReturnPiece.PieceType.BB
						|| topRight.pieceType == ReturnPiece.PieceType.BQ)) {
					// System.out.println(" top right bishop/queen checks it");
					return true;
				} else if (kingColor == 'B' && (topRight.pieceType == ReturnPiece.PieceType.WB
						|| topRight.pieceType == ReturnPiece.PieceType.WQ)) {
					// System.out.println(" top right bishop/queen checks it");

					return true;
				}
			}
			if (topLeft != null && topLeftChecked == false) {
				topLeftChecked = true;
				if (kingColor == 'W' && (topLeft.pieceType == ReturnPiece.PieceType.BB
						|| topLeft.pieceType == ReturnPiece.PieceType.BQ)) {
					// System.out.println(" top left piece type is " + topLeft.pieceType + " and
					// kingColor is: " + kingColor);
					// System.out.println(" top left bishop/queen checks it");
					return true;
				} else if (kingColor == 'B' && (topLeft.pieceType == ReturnPiece.PieceType.WB
						|| topLeft.pieceType == ReturnPiece.PieceType.WQ)) {
					// boolean checkingKingColor = kingColor == 'B';
					// boolean checkingPieceType = topLeft.pieceType == PieceType.WB ||
					// topLeft.pieceType == PieceType.WQ;
					// System.out.println("checking if king color is black: " + checkingKingColor);
					// System.out.println("checking if piece type is white bishop or queen: " +
					// checkingPieceType);
					// System.out.println(" top left piece type is " + topLeft.pieceType + " and
					// kingColor is: " + kingColor);
					// System.out.println(" top left bishop/queen checks it");
					return true;
				}
			}
			if (botRight != null && botRightChecked == false) {
				botRightChecked = true;
				if (kingColor == 'W' && (botRight.pieceType == ReturnPiece.PieceType.BB
						|| botRight.pieceType == ReturnPiece.PieceType.BQ)) {
					// System.out.println(" bottom right bishop/queen checks it");
					return true;
				} else if (kingColor == 'B'
						&& (botRight.pieceType == ReturnPiece.PieceType.WB
								|| botRight.pieceType == ReturnPiece.PieceType.WQ)) {
					// System.out.println(" bottom right bishop/queen checks it");
					return true;
				}
			}
			if (botLeft != null && botLeftChecked == false) {
				botLeftChecked = true;
				if (kingColor == 'W' && (botLeft.pieceType == ReturnPiece.PieceType.BB
						|| botLeft.pieceType == ReturnPiece.PieceType.BQ)) {
					// System.out.println( " bottom left bishop/queen checks it");
					return true;
				} else if (kingColor == 'B'
						&& (botLeft.pieceType == ReturnPiece.PieceType.WB
								|| botLeft.pieceType == ReturnPiece.PieceType.WQ)) {
					// System.out.println( " bottom left bishop/queen checks it");
					return true;
				}
			}
		}

		// check the front/side directions for rook and queen
		boolean topChecked = false;
		boolean botChecked = false;
		boolean leftChecked = false;
		boolean rightChecked = false;
		for (int i = 1; i < 8; i++) {

			ReturnPiece right = null;
			ReturnPiece left = null;
			ReturnPiece top = null;
			ReturnPiece bot = null;

			if (kingFile.ordinal() + i < 8) {
				right = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + i], kingRank, boardState);
			}
			if (kingFile.ordinal() - i >= 0) {
				left = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - i], kingRank, boardState);
			}
			if (kingRank + i < 9) {
				top = Chess.findPiece(kingFile, kingRank + i, boardState);
			}
			if (kingRank - i > 0) {
				bot = Chess.findPiece(kingFile, kingRank - i, boardState);
			}

			if (right != null && rightChecked == false) {
				rightChecked = true;
				if (kingColor == 'W' && (right.pieceType == ReturnPiece.PieceType.BR
						|| right.pieceType == ReturnPiece.PieceType.BQ)) {
					// System.out.println(" right rook/queen checks it");
					return true;
				} else if (kingColor == 'B' && (right.pieceType == ReturnPiece.PieceType.WR
						|| right.pieceType == ReturnPiece.PieceType.WQ)) {
					// System.out.println(" right rook/queen checks it");
					return true;
				}
			}
			if (left != null && leftChecked == false) {
				leftChecked = true;
				if (kingColor == 'W'
						&& (left.pieceType == ReturnPiece.PieceType.BR || left.pieceType == ReturnPiece.PieceType.BQ)) {
					// System.out.println(" left rook/queen checks it");
					return true;
				} else if (kingColor == 'B'
						&& (left.pieceType == ReturnPiece.PieceType.WR || left.pieceType == ReturnPiece.PieceType.WQ)) {
					// System.out.println(" left rook/queen checks it");
					return true;
				}
			}
			if (top != null && topChecked == false) {
				topChecked = true;
				if (kingColor == 'W'
						&& (top.pieceType == ReturnPiece.PieceType.BR || top.pieceType == ReturnPiece.PieceType.BQ)) {
					// System.out.println(" top rook/queen checks it");ReturnPiece.
					return true;
				} else if (kingColor == 'B'
						&& (top.pieceType == ReturnPiece.PieceType.WR || top.pieceType == ReturnPiece.PieceType.WQ)) {
					// System.out.println(" top rook/queen checks it");
					return true;
				}
			}
			if (bot != null && botChecked == false) {
				// System.out.println("bot is: " + bot.pieceType + " and kingColor is: " +
				// kingColor);
				botChecked = true;
				if (kingColor == 'W'
						&& (bot.pieceType == ReturnPiece.PieceType.BR || bot.pieceType == ReturnPiece.PieceType.BQ)) {
					// System.out.println(" bottom rook/queen checks it");
					return true;
				} else if (kingColor == 'B'
						&& (bot.pieceType == ReturnPiece.PieceType.WR || bot.pieceType == ReturnPiece.PieceType.WQ)) {
					// System.out.println(" bottom rook/queen checks it");
					return true;
				}
			}
		}

		// if king is in proximity of king

		ReturnPiece topLeftK = null;
		ReturnPiece topMidK = null;
		ReturnPiece topRightK = null;
		ReturnPiece sideLeftK = null;
		ReturnPiece sideRightK = null;
		ReturnPiece botLeftK = null;
		ReturnPiece botMidK = null;
		ReturnPiece botRightK = null;

		if (kingFile.ordinal() + 1 < 8 && kingRank + 1 <= 8)
			topLeftK = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + 1], kingRank + 1,
					boardState);
		if (kingRank + 1 <= 8)
			topMidK = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal()], kingRank + 1, boardState);
		if (kingFile.ordinal() - 1 >= 0 && kingRank + 1 <= 8)
			topRightK = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - 1], kingRank + 1,
					boardState);
		if (kingFile.ordinal() + 1 < 8)
			sideLeftK = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + 1], kingRank, boardState);
		if (kingFile.ordinal() - 1 >= 0)
			sideRightK = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - 1], kingRank, boardState);
		if (kingFile.ordinal() - 1 >= 0 && kingRank - 1 > 0)
			botLeftK = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() - 1], kingRank - 1,
					boardState);
		if (kingRank - 1 > 0)
			botMidK = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal()], kingRank - 1, boardState);
		if (kingFile.ordinal() + 1 < 8 && kingRank - 1 > 0)
			botRightK = Chess.findPiece(ReturnPiece.PieceFile.values()[kingFile.ordinal() + 1], kingRank - 1,
					boardState);

		if (topLeftK != null) {
			if (kingColor == 'W' && (topLeftK.pieceType == ReturnPiece.PieceType.BK)) {
				// System.out.println(" top left king checks it");
				return true;
			} else if (kingColor == 'B' && (topLeftK.pieceType == ReturnPiece.PieceType.WK)) {
				// System.out.println(" top left king checks it");
				return true;
			}
		}
		if (topMidK != null) {
			if (kingColor == 'W' && (topMidK.pieceType == ReturnPiece.PieceType.BK)) {
				// System.out.println(" top middle king checks it");
				return true;
			} else if (kingColor == 'B' && (topMidK.pieceType == ReturnPiece.PieceType.WK)) {
				// System.out.println(" top middle king checks it");
				return true;
			}
		}
		if (topRightK != null) {
			if (kingColor == 'W' && (topRightK.pieceType == ReturnPiece.PieceType.BK)) {
				// System.out.println(" top right king checks it");
				return true;
			} else if (kingColor == 'B' && (topRightK.pieceType == ReturnPiece.PieceType.WK)) {
				// System.out.println(" top right king checks it");
				return true;
			}
		}
		if (sideLeftK != null) {
			if (kingColor == 'W' && (sideLeftK.pieceType == ReturnPiece.PieceType.BK)) {
				// System.out.println(" side left king checks it");
				return true;
			} else if (kingColor == 'B' && (sideLeftK.pieceType == ReturnPiece.PieceType.WK)) {
				// System.out.println(" side left king checks it");
				return true;
			}
		}
		if (sideRightK != null) {
			if (kingColor == 'W' && (sideRightK.pieceType == ReturnPiece.PieceType.BK)) {
				// System.out.println( " side right king checks it");
				return true;
			} else if (kingColor == 'B' && (sideRightK.pieceType == ReturnPiece.PieceType.WK)) {
				// System.out.println( " side right king checks it");
				return true;
			}
		}
		if (botLeftK != null) {
			if (kingColor == 'W' && (botLeftK.pieceType == ReturnPiece.PieceType.BK)) {
				// System.out.println(" bottom left king checks it");
				return true;
			} else if (kingColor == 'B' && (botLeftK.pieceType == ReturnPiece.PieceType.WK)) {
				// System.out.println(" bottom left king checks it");
				return true;
			}
		}
		if (botMidK != null) {
			if (kingColor == 'W' && (botMidK.pieceType == ReturnPiece.PieceType.BK)) {
				// System.out.println(" bottom middle king checks it");
				return true;
			} else if (kingColor == 'B' && (botMidK.pieceType == ReturnPiece.PieceType.WK)) {
				// System.out.println(" bottom middle king chReturnPiece.ecks it");
				return true;
			}
		}
		if (botRightK != null) {
			if (kingColor == 'W' && (botRightK.pieceType == ReturnPiece.PieceType.BK)) {
				// System.out.println(" bottom right king checks it");
				return true;
			} else if (kingColor == 'B' && (botRightK.pieceType == ReturnPiece.PieceType.WK)) {
				// System.out.println(" bottom right king checks it");
				return true;
			}
		}

		return false;
	}

	public static boolean isCheckmate(ArrayList<ReturnPiece> boardState, char kingColor) {
		// check if the king is in check
		if (!isCheck(boardState, kingColor)) {
			return false;
		}

		// check if the king can move out of check

		// copy the board state 8 times for all 8 positions the king can go to
		ArrayList<ReturnPiece> tempBoardState = new ArrayList<ReturnPiece>();
		for (ReturnPiece rp : Chess.currentBoardState) {
			ReturnPiece tempPiece = new ReturnPiece();
			tempPiece.pieceFile = rp.pieceFile;
			tempPiece.pieceRank = rp.pieceRank;
			tempPiece.pieceType = rp.pieceType;
			tempBoardState.add(tempPiece);
		}
		ArrayList<ReturnPiece> origBoardState = tempBoardState;

		// ReturnPiece currentKingLocation = null; //what was the point of this
		ReturnPiece.PieceFile currKFile = null; // currKfile is the file of the king before it is moved
		int currKRank = 0; // currKRank is the rank of the king before it is moved
		String nextMoveLocation = null; // nextMoveLocation is the location of the king after it is moved

		for (ReturnPiece piece : tempBoardState) {
			if (kingColor == 'W' && piece.pieceType == ReturnPiece.PieceType.WK) {
				// currentKingLocation = piece;
				currKFile = piece.pieceFile;
				currKRank = piece.pieceRank;
			} else if (kingColor == 'B' && piece.pieceType == ReturnPiece.PieceType.BK) {
				// currentKingLocation = piece;
				currKFile = piece.pieceFile;
				currKRank = piece.pieceRank;
			}
		}

		// check if the king can move to any of the 8 positions
		ReturnPiece tempKing = findPiece(currKFile, currKRank, tempBoardState); // move this every time
		ReturnPiece fixedKing = new ReturnPiece(); // this doesn't move
		fixedKing.pieceFile = currKFile;
		fixedKing.pieceRank = currKRank;
		if (kingColor == 'W') {
			fixedKing.pieceType = ReturnPiece.PieceType.WK;
		} else if (kingColor == 'B') {
			fixedKing.pieceType = ReturnPiece.PieceType.BK;
		}

		// check if the king can move to the top left
		if (currKFile.ordinal() - 1 >= 0 && currKRank + 1 <= 8) {
			// create a temporary piece which is the piece that the king is trying to move
			// on top of
			ReturnPiece tempPiece = findPiece(ReturnPiece.PieceFile.values()[currKFile.ordinal() - 1], currKRank + 1,
					tempBoardState);
			if (tempPiece != null) {
				// there is a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() - 1];
				tempKing.pieceRank = currKRank + 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (King.isLegalMove(fixedKing, tempPiece, nextMoveLocation) && !isCheck(tempBoardState, kingColor)) {
					return false;
				}
			} else {
				// there is NOT a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() - 1]; // move the king to that
																								// square
				tempKing.pieceRank = currKRank + 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (!isCheck(tempBoardState, kingColor)) {
					return false;
				}
			}
			tempBoardState = origBoardState; // reset the temp board state
		}

		// check if the king can move to the top
		if (currKRank + 1 <= 8) {
			ReturnPiece tempPiece = findPiece(ReturnPiece.PieceFile.values()[currKFile.ordinal()], currKRank + 1,
					tempBoardState);
			if (tempPiece != null) {
				// there is a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal()];
				tempKing.pieceRank = currKRank + 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (King.isLegalMove(fixedKing, tempPiece, nextMoveLocation) && !isCheck(tempBoardState, kingColor)) {
					return false;
				}
			} else {
				// there is NOT a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal()];
				tempKing.pieceRank = currKRank + 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (!isCheck(tempBoardState, kingColor)) {
					return false;
				}
			}
			tempBoardState = origBoardState;
		}

		// check if the king can move to the top right
		if (currKFile.ordinal() + 1 < 8 && currKRank + 1 <= 8) {
			ReturnPiece tempPiece = findPiece(ReturnPiece.PieceFile.values()[currKFile.ordinal() + 1], currKRank + 1,
					tempBoardState);
			if (tempPiece != null) {
				// there is a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() + 1];
				tempKing.pieceRank = currKRank + 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (King.isLegalMove(fixedKing, tempPiece, nextMoveLocation) && !isCheck(tempBoardState, kingColor)) {
					return false;
				}
			} else {
				// there is NOT a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() + 1];
				tempKing.pieceRank = currKRank + 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (!isCheck(tempBoardState, kingColor)) {
					return false;
				}
			}
			tempBoardState = origBoardState;
		}

		// check if the king can move to the left
		if (currKFile.ordinal() - 1 >= 0) {
			ReturnPiece tempPiece = findPiece(ReturnPiece.PieceFile.values()[currKFile.ordinal() - 1], currKRank,
					tempBoardState);
			if (tempPiece != null) {
				// there is a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() - 1];
				tempKing.pieceRank = currKRank;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (King.isLegalMove(fixedKing, tempPiece, nextMoveLocation) && !isCheck(tempBoardState, kingColor)) {
					return false;
				}
			} else {
				// there is NOT a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() - 1];
				tempKing.pieceRank = currKRank;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (!isCheck(tempBoardState, kingColor)) {
					return false;
				}
			}
			tempBoardState = origBoardState;
		}

		// check if the king can move to the right
		if (currKFile.ordinal() + 1 < 8) {
			ReturnPiece tempPiece = findPiece(ReturnPiece.PieceFile.values()[currKFile.ordinal() + 1], currKRank,
					tempBoardState);
			if (tempPiece != null) {
				// there is a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() + 1];
				tempKing.pieceRank = currKRank;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (King.isLegalMove(fixedKing, tempPiece, nextMoveLocation) && !isCheck(tempBoardState, kingColor)) {
					return false;
				}
			} else {
				// there is NOT a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() + 1];
				tempKing.pieceRank = currKRank;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (!isCheck(tempBoardState, kingColor)) {
					return false;
				}
			}
			tempBoardState = origBoardState;
		}
		// check if the king can move to the bottom left
		if (currKFile.ordinal() - 1 >= 0 && currKRank - 1 > 0) {
			ReturnPiece tempPiece = findPiece(ReturnPiece.PieceFile.values()[currKFile.ordinal() - 1], currKRank - 1,
					tempBoardState);
			if (tempPiece != null) {
				// there is a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() - 1];
				tempKing.pieceRank = currKRank - 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (King.isLegalMove(fixedKing, tempPiece, nextMoveLocation) && !isCheck(tempBoardState, kingColor)) {
					return false;
				}
			} else {
				// there is NOT a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() - 1];
				tempKing.pieceRank = currKRank - 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (!isCheck(tempBoardState, kingColor)) {
					return false;
				}
			}
			tempBoardState = origBoardState;
		}

		// check if the king can move to the bottom
		if (currKRank - 1 > 0) {
			ReturnPiece tempPiece = findPiece(ReturnPiece.PieceFile.values()[currKFile.ordinal()], currKRank - 1,
					tempBoardState);

			if (tempPiece != null) {
				// there is a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal()];
				tempKing.pieceRank = currKRank - 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (King.isLegalMove(fixedKing, tempPiece, nextMoveLocation) && !isCheck(tempBoardState, kingColor)) {
					return false;
				}
			} else {
				// there is NOT a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal()];
				tempKing.pieceRank = currKRank - 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (!isCheck(tempBoardState, kingColor)) {
					return false;
				}
			}
			tempBoardState = origBoardState;
		}

		// check if the king can move to the bottom right
		if (currKFile.ordinal() + 1 < 8 && currKRank - 1 > 0) {
			ReturnPiece tempPiece = findPiece(ReturnPiece.PieceFile.values()[currKFile.ordinal() + 1], currKRank - 1,
					tempBoardState);

			if (tempPiece != null) {
				// there is a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() + 1];
				tempKing.pieceRank = currKRank - 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;

				// System.out.println();
				// System.out.println("fixedKing location: " + fixedKing.pieceFile +
				// fixedKing.pieceRank);
				// System.out.println("tempPiece location: " + tempPiece.pieceFile +
				// tempPiece.pieceRank);
				// System.out.println("nextMoveLocation: " + nextMoveLocation);
				// System.out.println();
				// System.out.println("King.isLegalMove: " + King.isLegalMove(fixedKing,
				// tempPiece, nextMoveLocation)); //should be true
				// System.out.println("!isCheck: " + !isCheck(tempBoardState, kingColor));
				// //should be true
				// System.out.println();

				if (King.isLegalMove(fixedKing, tempPiece, nextMoveLocation) && !isCheck(tempBoardState, kingColor)) {
					return false;
				}
			} else {
				// there is NOT a piece where the king is trying to move to
				tempKing.pieceFile = ReturnPiece.PieceFile.values()[currKFile.ordinal() + 1];
				tempKing.pieceRank = currKRank - 1;
				nextMoveLocation = tempKing.pieceFile.toString() + tempKing.pieceRank;
				if (!isCheck(tempBoardState, kingColor)) {
					return false;
				}
			}
			tempBoardState = origBoardState;
		}

		return true;
	}

	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		/* FILL IN THIS METHOD */
		currentBoardState = new ArrayList<ReturnPiece>();
		currPlayer = Player.white;

		// Initialize all white pawns
		for (ReturnPiece.PieceFile file : ReturnPiece.PieceFile.values()) {
			Pawn whitePawn = new Pawn(file, 2, ReturnPiece.PieceType.WP);
			currentBoardState.add(whitePawn);
		}

		// Initialize all black pawns
		for (ReturnPiece.PieceFile file : ReturnPiece.PieceFile.values()) {
			Pawn blackPawn = new Pawn(file, 7, ReturnPiece.PieceType.BP);
			currentBoardState.add(blackPawn);
		}

		// ReturnPiece test = new ReturnPiece();
		// test.pieceType = ReturnPiece.PieceType.WP;
		// test.pieceFile = ReturnPiece.PieceFile.e;
		// test.pieceRank = 1;
		// currentBoardState.add(test);

		ReturnPiece whiteKnight1 = new ReturnPiece();
		whiteKnight1.pieceType = ReturnPiece.PieceType.WN;
		whiteKnight1.pieceFile = ReturnPiece.PieceFile.b;
		whiteKnight1.pieceRank = 1;
		currentBoardState.add(whiteKnight1);

		ReturnPiece whiteKnight2 = new ReturnPiece();
		whiteKnight2.pieceType = ReturnPiece.PieceType.WN;
		whiteKnight2.pieceFile = ReturnPiece.PieceFile.g;
		whiteKnight2.pieceRank = 1;
		currentBoardState.add(whiteKnight2);

		ReturnPiece whiteBishop1 = new ReturnPiece();
		whiteBishop1.pieceType = ReturnPiece.PieceType.WB;
		whiteBishop1.pieceFile = ReturnPiece.PieceFile.c;
		whiteBishop1.pieceRank = 1;
		currentBoardState.add(whiteBishop1);

		ReturnPiece whiteBishop2 = new ReturnPiece();
		whiteBishop2.pieceType = ReturnPiece.PieceType.WB;
		whiteBishop2.pieceFile = ReturnPiece.PieceFile.f;
		whiteBishop2.pieceRank = 1;
		currentBoardState.add(whiteBishop2);

		ReturnPiece whiteRook1 = new ReturnPiece();
		whiteRook1.pieceType = ReturnPiece.PieceType.WR;
		whiteRook1.pieceFile = ReturnPiece.PieceFile.a;
		whiteRook1.pieceRank = 1;
		currentBoardState.add(whiteRook1);

		ReturnPiece whiteRook2 = new ReturnPiece();
		whiteRook2.pieceType = ReturnPiece.PieceType.WR;
		whiteRook2.pieceFile = ReturnPiece.PieceFile.h;
		whiteRook2.pieceRank = 1;
		currentBoardState.add(whiteRook2);

		ReturnPiece whiteQueen = new ReturnPiece();
		whiteQueen.pieceType = ReturnPiece.PieceType.WQ;
		whiteQueen.pieceFile = ReturnPiece.PieceFile.d;
		whiteQueen.pieceRank = 1;
		currentBoardState.add(whiteQueen);

		ReturnPiece whiteKing = new ReturnPiece();
		whiteKing.pieceType = ReturnPiece.PieceType.WK;
		whiteKing.pieceFile = ReturnPiece.PieceFile.e; // e
		whiteKing.pieceRank = 1; // 1
		currentBoardState.add(whiteKing);

		// black pieces
		ReturnPiece blackKnight1 = new ReturnPiece();
		blackKnight1.pieceType = ReturnPiece.PieceType.BN;
		blackKnight1.pieceFile = ReturnPiece.PieceFile.b;
		blackKnight1.pieceRank = 8;
		currentBoardState.add(blackKnight1);

		ReturnPiece blackKnight2 = new ReturnPiece();
		blackKnight2.pieceType = ReturnPiece.PieceType.BN;
		blackKnight2.pieceFile = ReturnPiece.PieceFile.g;
		blackKnight2.pieceRank = 8;
		currentBoardState.add(blackKnight2);

		ReturnPiece blackBishop1 = new ReturnPiece();
		blackBishop1.pieceType = ReturnPiece.PieceType.BB;
		blackBishop1.pieceFile = ReturnPiece.PieceFile.c;
		blackBishop1.pieceRank = 8;
		currentBoardState.add(blackBishop1);

		ReturnPiece blackBishop2 = new ReturnPiece();
		blackBishop2.pieceType = ReturnPiece.PieceType.BB;
		blackBishop2.pieceFile = ReturnPiece.PieceFile.f;
		blackBishop2.pieceRank = 8;
		currentBoardState.add(blackBishop2);

		ReturnPiece blackRook1 = new ReturnPiece();
		blackRook1.pieceType = ReturnPiece.PieceType.BR;
		blackRook1.pieceFile = ReturnPiece.PieceFile.a;
		blackRook1.pieceRank = 8;
		currentBoardState.add(blackRook1);

		ReturnPiece blackRook2 = new ReturnPiece();
		blackRook2.pieceType = ReturnPiece.PieceType.BR;
		blackRook2.pieceFile = ReturnPiece.PieceFile.h;
		blackRook2.pieceRank = 8;
		currentBoardState.add(blackRook2);

		ReturnPiece blackQueen = new ReturnPiece();
		blackQueen.pieceType = ReturnPiece.PieceType.BQ;
		blackQueen.pieceFile = ReturnPiece.PieceFile.d;
		blackQueen.pieceRank = 8;
		currentBoardState.add(blackQueen);

		ReturnPiece blackKing = new ReturnPiece();
		blackKing.pieceType = ReturnPiece.PieceType.BK;
		blackKing.pieceFile = ReturnPiece.PieceFile.e; // e
		blackKing.pieceRank = 8; // 8
		currentBoardState.add(blackKing);

		PlayChess.printBoard(currentBoardState); // prints the current board
	}
}
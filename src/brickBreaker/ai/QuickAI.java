package brickBreaker.ai;

import brickBreaker.board.BoardDuplicator;
import brickBreaker.board.PlayableBoard;
import brickBreaker.board.ReadOnlyBoard;
import utils.IntVector2D;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Brute force it!
 * Try out each possible move ordered by points
 * (Press the largest group and undo if that didn't work until it works)
 */
public class QuickAI extends AI {
    @Override
    public List<IntVector2D> getWinningMoveSet(ReadOnlyBoard board) {
        Stack<IntVector2D> winningMoveSet = new Stack<>();
        if (getWinningMoveSet(BoardDuplicator.duplicate(board), winningMoveSet)) {
            return winningMoveSet;
        }
        throw new IllegalArgumentException("provided board is unsolvable");
    }

    private boolean getWinningMoveSet(PlayableBoard boardToUse, Stack<IntVector2D> moveList) {
        if (boardToUse.isEmpty()) {
            return true;
        }
        Map<IntVector2D, Integer> possibleMoves = getAllPossibleMovesMap(boardToUse);
        if (possibleMoves.isEmpty() || AI.unWinnable(boardToUse)) {
            return false;
        }
        return possibleMoves.keySet().stream()
                .sorted((o1, o2) -> possibleMoves.get(o2) - possibleMoves.get(o1))
                .map(move -> {
                    PlayableBoard dup = BoardDuplicator.duplicate(boardToUse);
                    dup.popCell(move);
                    if (getWinningMoveSet(dup, moveList)) {
                        moveList.insertElementAt(move, 0);
                        return true;
                    }
                    return false;
                })
                .filter(ret -> ret)
                .findFirst().orElse(false);
    }
}

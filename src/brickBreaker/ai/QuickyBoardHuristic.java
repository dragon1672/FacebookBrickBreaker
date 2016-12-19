package brickBreaker.ai;

import brickBreaker.board.BoardDuplicator;
import brickBreaker.board.PlayableBoard;
import brickBreaker.board.ReadOnlyBoard;
import utils.IntVector2D;
import utils.Tuple;
import utils.Tuple.Tuple3;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Sorts proposed moved based off improved board heuristic
 */
public class QuickyBoardHuristic extends AI {
    private static int boardHeuristicMovePoints(ReadOnlyBoard board) {
        return getAllPossibleMovesMap(board).values().stream().reduce((integer, integer2) -> integer + integer2).orElse(-1);
    }

    /**
     * Apples move and display heuristic difference.
     * @param board input board
     * @param move move to take
     * @return Tuple of new board post move, the move, and the heuristic delta
     */
    private static Tuple3<PlayableBoard,IntVector2D,Integer> boardHeuristicChange(ReadOnlyBoard board, IntVector2D move) {
        int preMoveScore = boardHeuristicMovePoints(board);
        PlayableBoard duplicate = BoardDuplicator.duplicate(board);
        duplicate.popCell(move);
        int postMoveScore = boardHeuristicMovePoints(board);
        int heuristicDelta = postMoveScore - preMoveScore;
        return Tuple.of(duplicate,move,heuristicDelta);
    }

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
                // dup board, preform move, save heuristic
                .map(move -> boardHeuristicChange(boardToUse,move))
                // sort by board heuristic
                .sorted(Comparator.comparingInt(Tuple3::getThird))
                // recurse
                .map(tuple-> {
                    PlayableBoard board = tuple.getFirst();
                    IntVector2D move = tuple.getSecond();
                    if (getWinningMoveSet(board, moveList)) {
                        moveList.insertElementAt(move, 0);
                        return true;
                    }
                    return false;
                })
                .filter(ret -> ret)
                .findFirst().orElse(false);
    }
}

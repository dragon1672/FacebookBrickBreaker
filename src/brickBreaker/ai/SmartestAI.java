package brickBreaker.ai;

import brickBreaker.board.ReadWriteBoard;
import com.sun.istack.internal.Nullable;
import utils.IntVector2D;

import java.util.*;

/**
 * Explores all possible outcomes and returns the best solutions
 * Right now way to slow to be useable
 */
@SuppressWarnings("unused")
class SmartestAI extends AI {
    @Override
    public List<IntVector2D> getWinningMoveSet(ReadWriteBoard board) {
        List<IntVector2D> winningMoveSet = new ArrayList<>();
        getMaxMoveScore(board, winningMoveSet);
        Collections.reverse(winningMoveSet);
        return winningMoveSet;
    }

    private int getMaxMoveScore(ReadWriteBoard boardToUse, List<IntVector2D> outputMoveList) {
        return getMaxMoveScore(boardToUse, null, outputMoveList);
    }

    private int getMaxMoveScore(ReadWriteBoard board, @Nullable IntVector2D pos, List<IntVector2D> outputMoveList) {
        int moveScore = 0;
        if (pos != null) {
            moveScore = board.popCell(pos);

            if (board.isEmpty() || AI.unWinnable(board)) {
                return moveScore;
            }
        }
        Set<IntVector2D> possibleMoves = getAllPossibleMoves(board);
        if (possibleMoves.isEmpty()) {
            return -1;
        }
        Map<Integer, List<IntVector2D>> scoreToMoves = new HashMap<>();
        possibleMoves.parallelStream().forEach(move -> {
            List<IntVector2D> moves = new ArrayList<>();
            int moveMaxScore = getMaxMoveScore(board.duplicate(), move, moves);
            if (moveMaxScore > 0) {
                synchronized (scoreToMoves) {
                    scoreToMoves.put(moveMaxScore, moves);
                }
            }
        });


        Optional<Integer> maxScore = scoreToMoves.keySet().stream().max(Integer::compareTo);
        int finalMoveScore = moveScore;
        maxScore.map(returnScore -> returnScore + finalMoveScore);
        maxScore.ifPresent(score -> outputMoveList.addAll(scoreToMoves.get(score)));
        return maxScore.orElse(-1);
    }
}

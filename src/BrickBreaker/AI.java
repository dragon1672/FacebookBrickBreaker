package BrickBreaker;

import com.sun.istack.internal.Nullable;
import utils.IntVector2D;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * My Dumb AIs for the breaker game
 */
abstract class AI {

    Set<IntVector2D> getAllPossibleMoves(Board board) {
        return getAllPossibleMovesMap(board).keySet();
    }

    Map<IntVector2D, Integer> getAllPossibleMovesMap(Board board) {
        Set<IntVector2D> consideredPositions = new HashSet<>();
        Map<IntVector2D, Integer> possibleMoves = new HashMap<>();
        board.getCurrentBoardPositions()
                .forEach(pos -> {
                    if (!consideredPositions.contains(pos)) {
                        Set<IntVector2D> matchResult = board.getSameColoredGroup(pos);
                        if (matchResult.size() > 1) {
                            possibleMoves.put(pos, matchResult.size());
                            consideredPositions.addAll(matchResult);
                        }
                    }
                });
        return possibleMoves;
    }

    /**
     * Returns true if the current board is unable to win
     * This will not modify the board
     * @param board const board to check
     * @return
     */
    private static boolean unWinnable(Board board) {
        boolean singleInstanceOfColor = board.getCurrentBoardPositions()
                .map(board::getColor)
                .collect(Collectors.groupingBy(Function.identity(),Collectors.counting())).values()
                .stream()
                .anyMatch(value -> value == 1);
        //noinspection RedundantIfStatement
        if(singleInstanceOfColor) {
            return true;
        }
        //TODO add more checks
        return false;
    }

    abstract List<IntVector2D> getWinningMoveSet(Board board);

    /**
     * Brute force it!
     * Try out each possible move ordered by points
     * (Press the largest group and undo if that didn't work until it works)
     */
    static class QuickAI extends AI {
        @Override
        List<IntVector2D> getWinningMoveSet(Board board) {
            Stack<IntVector2D> winningMoveSet = new Stack<>();
            if (getWinningMoveSet(board, winningMoveSet)) {
                return winningMoveSet;
            }
            throw new IllegalArgumentException("provided board is unsolvable");
        }

        boolean getWinningMoveSet(Board boardToUse, Stack<IntVector2D> moveList) {
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
                Board dup = boardToUse.duplicate();
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

    /**
     * Explores all possible outcomes and returns the best solutions
     * Right now way to slow to be useable
     */
    @SuppressWarnings("unused")
    static class SmartestAI extends AI {
        @Override
        List<IntVector2D> getWinningMoveSet(Board board) {
            List<IntVector2D> winningMoveSet = new ArrayList<>();
            getMaxMoveScore(board, winningMoveSet);
            Collections.reverse(winningMoveSet);
            return winningMoveSet;
        }

        int getMaxMoveScore(Board boardToUse, List<IntVector2D> outputMoveList) {
            return getMaxMoveScore(boardToUse, null, outputMoveList);
        }

        int getMaxMoveScore(Board board, @Nullable IntVector2D pos, List<IntVector2D> outputMoveList) {
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
}

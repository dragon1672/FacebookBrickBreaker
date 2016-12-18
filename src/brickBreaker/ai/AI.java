package brickBreaker.ai;

import brickBreaker.board.Board;
import brickBreaker.board.ReadOnlyBoard;
import utils.IntVector2D;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * My Dumb AIs for the breaker game
 */
public abstract class AI {
    // TODO: Add more checks
    // These checks
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    private static final List<Predicate<ReadOnlyBoard>> failIfTrueChecks = Arrays.asList(
            // Check if not enough colors left to win
            board -> board.getCurrentBoardPositions()
                    .map(board::getColor)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).values()
                    .stream()
                    .anyMatch(value -> value == 1)
    );
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    private static final List<Predicate<ReadOnlyBoard>> failIfFalseChecks = Arrays.asList();

    /**
     * Returns true if the current board is unable to win
     * This will not modify the board
     *
     * @param board const board to check
     * @return true if current board cannot win, false if unknown
     */
    static boolean unWinnable(Board board) {
        if (failIfTrueChecks.stream().anyMatch(checker -> checker.test(board))) {
            return true;
        }
        if (failIfFalseChecks.stream().anyMatch(checker -> !checker.test(board))) {
            return true;
        }
        // Unknown
        return false;
    }

    /**
     * Gets the shortest list of all unique moves.  For each given group only 1 of their positions will exist in this set
     *
     * @param board board to evaluate
     * @return shortest list of all unique moves
     */
    static Set<IntVector2D> getAllPossibleMoves(Board board) {
        return getAllPossibleMovesMap(board).keySet();
    }

    /**
     * Gets the shortest list of all unique moves and their scores.  For each given group only 1 of their positions will exist in this set
     *
     * @param board board to check
     * @return shortest list of all unique moves and their scores
     */
    static Map<IntVector2D, Integer> getAllPossibleMovesMap(Board board) {
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

    public abstract List<IntVector2D> getWinningMoveSet(Board board);
}

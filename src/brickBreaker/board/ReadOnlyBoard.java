package brickBreaker.board;

import utils.IntVector2D;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Actions that do not edit board state
 */
public interface ReadOnlyBoard {

    /**
     * @return true if the board has no values
     */
    boolean isEmpty();

    /**
     * Get the board color at given position
     *
     * @param position position to get color for
     * @return color at given position
     * @throws IllegalArgumentException if position isn't valid
     */
    Color getColor(IntVector2D position);

    /**
     * Stream of currently valid board positions
     * (valid positions, not valid moves)
     *
     * @return stream of positions
     */
    Stream<IntVector2D> getCurrentBoardPositions();

    /**
     * Provides list of transitively connected neighbors of the same color
     *
     * @param startingPosition initial position
     * @return list of transitively connected neighbors with the same color
     */
    Set<IntVector2D> getSameColoredGroup(IntVector2D startingPosition);

    /**
     * Get a string representation of the board
     *
     * @return the board
     */
    String getBoardString();

    /**
     * Get a handle to the board's columns
     * @return readonly board columns
     */
    Iterable<Iterable<Color>> getColumns();
}

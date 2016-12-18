package brickBreaker.board;

import utils.IntVector2D;

/**
 * Playable BoardImpl
 */
public interface PlayableBoard extends ReadOnlyBoard {
    /**
     * modifies board and pops adjacent cells. NO-Op if invalid move
     *
     * @param posToPop positions to pop
     * @return score
     */
    int popCell(IntVector2D posToPop);

    // Maybe this shouldn't be a member function but a separate utility function. That would allow the AI to have much tighter types
    /**
     * Create a copy of the current board without changing internal state
     *
     * @return copy of current board
     */
    PlayableBoard duplicate();
}

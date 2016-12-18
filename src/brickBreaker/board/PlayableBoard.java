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
}

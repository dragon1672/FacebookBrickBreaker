package brickBreaker.board;

/**
 * Duplicates provided board
 */
public class BoardDuplicator {
    public static PlayableBoard duplicate(ReadOnlyBoard input) {
        return new CachedBoard(new BoardImpl(input));
    }
}

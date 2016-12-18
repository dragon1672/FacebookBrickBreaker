package brickBreaker.board;

import utils.IntVector2D;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Saves Expensive Calls
 */
public class CachedBoard implements ReadWriteBoard {

    private final ReadWriteBoard backbone;
    private final Cache cache = new Cache();

    public CachedBoard(ReadWriteBoard backbone) {
        this.backbone = backbone;
    }

    @Override
    public int popCell(IntVector2D posToPop) {
        int ret = backbone.popCell(posToPop);
        cache.invalidate();
        return ret;
    }

    @Override
    public ReadWriteBoard duplicate() {
        return backbone.duplicate();
    }

    @Override
    public boolean isEmpty() {
        return backbone.isEmpty();
    }

    @Override
    public Color getColor(IntVector2D position) {
        return backbone.getColor(position);
    }

    @Override
    public Stream<IntVector2D> getCurrentBoardPositions() {
        return backbone.getCurrentBoardPositions();
    }

    @Override
    public Set<IntVector2D> getSameColoredGroup(IntVector2D startingPosition) {
        return cache.getSameColoredNeighbors.computeIfAbsent(startingPosition, backbone::getSameColoredGroup);
    }

    @Override
    public String getBoardString() {
        return backbone.getBoardString();
    }

    /**
     * Performance help maybe
     */
    private class Cache {
        // This is called multiple times by the current AIs so lets optimize the load
        // This is also the method taking up the most time during execution
        Map<IntVector2D, Set<IntVector2D>> getSameColoredNeighbors = new HashMap<>();

        void invalidate() {
            getSameColoredNeighbors.clear();
        }
    }
}

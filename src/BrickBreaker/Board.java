package BrickBreaker;

import utils.IntVector2D;
import utils.MyStringUtils;
import utils.Q;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Breaker Game Board
 */
class Board {
    /**
     * Performance help maybe
     */
    class Cache {
        // This is called multiple times by the current AIs so lets optimize the load
        // This is also the method taking up the most time during execution
        Map<IntVector2D,Set<IntVector2D>> getSameColoredNeighbors = new HashMap<>();
        void invalidate() {
            getSameColoredNeighbors.clear();
        }
    }

    private Cache cache = new Cache();

    private void invalidateCache() {
        cache.invalidate();
    }

    /**
     * Valid direction to match in (left, right, up, down
     */
    private static Collection<IntVector2D> matchDirections = Arrays.asList(
            IntVector2D.create(-1, 0),
            IntVector2D.create(1, 0),
            IntVector2D.create(0, -1),
            IntVector2D.create(0, 1)
    );

    // column then row
    private List<List<Color>> backbone = new ArrayList<>();

    private boolean validPosition(IntVector2D position) {
        return 0 <= position.X() && position.X() < backbone.size()
                && 0 <= position.Y() && position.Y() < backbone.get(position.X()).size();
    }

    private void validatePosition(IntVector2D position) {
        //* Disable if needed for performance, this is mainly a sanity check I didn't mess anything up
        if (!validPosition(position)) {
            throw new IllegalArgumentException(String.format("%s is not a valid board position board: \n%s", position,this));
        }
        //*/
    }

    Color getColor(IntVector2D position) {
        validatePosition(position);
        return backbone.get(position.X()).get(position.Y());
    }

    private void setColor(IntVector2D pos, Color color) {
        validatePosition(pos);
        backbone.get(pos.X()).set(pos.Y(), color);
    }

    /**
     * Stream of currently valid board positions
     * (valid positions, not valid moves)
     * @return stream of positions
     */
    Stream<IntVector2D> getCurrentBoardPositions() {
        // This used to return a Collection,
        // but converting to a stream increased performance, even though it is harder to read
        return IntStream.range(0,backbone.size())
                .mapToObj(i -> IntStream.range(0,backbone.get(i).size())
                        .mapToObj(j->IntVector2D.create(i,j)))
                .flatMap(stream->stream);
    }

    // TODO: move this logic out of board
    void generate(int width, int height) {
        backbone.clear();
        for (int i = 0; i < width; i++) {
            backbone.add(Stream.generate(Color::random).limit(height).collect(Collectors.toList()));
        }
    }

    //TODO: move this logic out of board
    void generate(String board) {
        // we will assume a properly formatted string
        int width = board.indexOf('\n');
        int height = board.length() / width;
        generate(width, height);
        for (int i = 0; i < board.length(); i++) {
            if (board.charAt(i) != '\n') {
                //the +1s are to accommodate for the the line return character
                int x = i % (width + 1);
                int y = height - 1 - (i / (height + 1));
                IntVector2D pos = IntVector2D.create(x, y);

                char colorSymbol = board.charAt(i);
                Color color = Color.fromSymbol(colorSymbol);

                this.setColor(pos, color);
            }
        }
        removeCells(getCurrentBoardPositions().filter(pos -> Color.WHITES_INVALID.equals(getColor(pos))));
    }

    private Stream<IntVector2D> cellNeighbors(IntVector2D pos) {
        // Converting this to a stream greatly increase performances because of how often this is called
        return matchDirections.stream()
                .map(pos::add)
                .filter(this::validPosition)
        ;
    }
    private Stream<IntVector2D> cellNeighborsWithSameColor(IntVector2D pos) {
        Color toMatch = getColor(pos);
        return cellNeighbors(pos)
                .filter(toCheck-> getColor(toCheck).equals(toMatch));
    }
    private Set<IntVector2D> getSameColoredGroup_(IntVector2D startingPosition) {
        Set<IntVector2D> matchedNeighbors = new HashSet<>();
        Q<IntVector2D> toVisit = new Q<>();
        toVisit.add(startingPosition);
        while (toVisit.size() > 0) {
            IntVector2D currentPos = toVisit.pop();
            matchedNeighbors.add(currentPos);
            cellNeighborsWithSameColor(currentPos)
                    .filter(neighbor -> !matchedNeighbors.contains(neighbor))
                    .forEach(toVisit::add);
        }
        return matchedNeighbors;
    }

    /**
     * Provides list of transitively connected neighbors of the same color
     * @param startingPosition initial position
     * @return list of transitively connected neighbors with the same color
     */
    Set<IntVector2D> getSameColoredGroup(IntVector2D startingPosition) {
        return cache.getSameColoredNeighbors.computeIfAbsent(startingPosition,this::getSameColoredGroup_);
    }

    /**
     * modifies board and pops adjacent cells. NO-Op if invalid move
     * @param posToPop positions to pop
     * @return score
     */
    int popCell(IntVector2D posToPop) {
        Set<IntVector2D> cellsToPop = getSameColoredGroup(posToPop);
        if (cellsToPop.size() > 1) {
            removeCells(cellsToPop);
        }
        return cellsToPop.size() * cellsToPop.size() - cellsToPop.size();
    }

    private void removeCells(Collection<IntVector2D> toRemove) {
        removeCells(toRemove.stream());
    }
    private void removeCells(Stream<IntVector2D> toRemove) {
        toRemove.sorted((o1, o2) -> o2.Y() - o1.Y()) // to not corrupt the positions mid removal
                .forEach(posToRemove -> backbone.get(posToRemove.X()).remove(posToRemove.Y()));
        for (int i = backbone.size() - 1; i >= 0; i--) {
            if (backbone.get(i).size() == 0) {
                backbone.remove(i);
            }
        }
        invalidateCache();
    }

    String getBoardString() {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < backbone.size(); x++) {
            for (int y = 0; y < backbone.get(x).size(); y++) {
                IntVector2D pos = IntVector2D.create(x, y);
                char symbol = Color.toSymbol(getColor(pos));
                sb.append(symbol);
            }
            sb.append('\n');
        }
        // Don't judge me :p just wanted something that worked off the bat
        return MyStringUtils.rotateCounterClockwise(sb.toString());
    }

    @Override
    public String toString() {
        return getBoardString();
    }

    Board duplicate() {
        // I'm super surprised how little this method impacted performance, turns out to be very cheap
        Board ret = new Board();
        ret.backbone = new ArrayList<>();
        for (int i = 0; i < backbone.size(); i++) {
            ret.backbone.add(new ArrayList<>());
            for (int j = 0; j < backbone.get(i).size(); j++) {
                ret.backbone.get(i).add(backbone.get(i).get(j));
            }
        }
        return ret;
    }

    boolean isEmpty() {
        return backbone.isEmpty();
    }
}

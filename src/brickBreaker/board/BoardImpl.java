package brickBreaker.board;

import utils.IntVector2D;
import utils.MyStringUtils;
import utils.Q;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Breaker Game BoardImpl
 */
public class BoardImpl implements PlayableBoard {
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

    BoardImpl(ReadOnlyBoard input) {
        for(Iterable<Color> inputColumn : input.getColumns()) {
            List<Color> newColumn = new ArrayList<>();
            backbone.add(newColumn);
            for(Color color : inputColumn) {
                newColumn.add(color);
            }
        }
    }
    BoardImpl(List<List<Color>> input) {
        for(Iterable<Color> inputColumn : input) {
            List<Color> newColumn = new ArrayList<>();
            backbone.add(newColumn);
            for(Color color : inputColumn) {
                newColumn.add(color);
            }
        }
    }

    private boolean validPosition(IntVector2D position) {
        return 0 <= position.X() && position.X() < backbone.size()
                && 0 <= position.Y() && position.Y() < backbone.get(position.X()).size();
    }

    private void validatePosition(IntVector2D position) {
        //* Disable if needed for performance, Neither board nor AI should call an invalid position
        if (!validPosition(position)) {
            throw new IllegalArgumentException(String.format("%s is not a valid board position board: \n%s", position, this));
        }
        //*/
    }

    @Override
    public Color getColor(IntVector2D position) {
        validatePosition(position);
        return backbone.get(position.X()).get(position.Y());
    }

    @Override
    public Stream<IntVector2D> getCurrentBoardPositions() {
        // This used to return a Collection,
        // but converting to a stream increased performance, even though it is harder to read
        return IntStream.range(0, backbone.size())
                .mapToObj(i -> IntStream.range(0, backbone.get(i).size())
                        .mapToObj(j -> IntVector2D.create(i, j)))
                .flatMap(stream -> stream);
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
                .filter(toCheck -> getColor(toCheck).equals(toMatch));
    }

   @Override
    public Set<IntVector2D> getSameColoredGroup(IntVector2D startingPosition) {
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

    @Override
    public int popCell(IntVector2D posToPop) {
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
        backbone.removeIf(List::isEmpty);
    }

    @Override
    public String getBoardString() {
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
    public Iterable<Iterable<Color>> getColumns() {
        return backbone.stream().collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return getBoardString();
    }

    @Override
    public boolean isEmpty() {
        return backbone.isEmpty();
    }
}

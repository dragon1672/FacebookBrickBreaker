package brickBreaker.board;

import utils.IntVector2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Creates a board from various inputs
 */
public class BoardGenerator {
    private List<List<Color>> backbone = new ArrayList<>();

    /**
     * Will set that position's color, if required will auto fill with empty squares
     * @param position pos to set color
     * @param toSet color to set
     */
    private BoardGenerator setColor(IntVector2D position, Color toSet) {
        int x = position.X();
        int y = position.Y();
        while(backbone.size() <= x) {
            List<Color> toAdd = new ArrayList<>();
            toAdd.add(Color.WHITES_INVALID);
            backbone.add(toAdd);
        }
        while(backbone.get(x).size() <= y) {
            backbone.get(x).add(Color.WHITES_INVALID);
        }
        backbone.get(x).set(y,toSet);
        return this;
    }

    /**
     * Removes any excess and fills in gaps with Color.WHITES_INVALID
     * @param width width
     * @param height height
     */
    private BoardGenerator setDimensions(int width, int height) {
        while(backbone.size()> width) {
            backbone.remove(backbone.size()-1);
        }
        while(backbone.size() < width) {
            backbone.add(Stream.generate(()->Color.WHITES_INVALID).limit(height).collect(Collectors.toList()));
        }
        backbone.forEach(column -> {
            // trim
            while(column.size() > height) {
                column.remove(height-1);
            }
            // add
            int toAdd = height - column.size();
            column.addAll(Stream.generate(()->Color.WHITES_INVALID).limit(toAdd).collect(Collectors.toList()));
        });
        return this;
    }

    /**
     * Like popping an individual position, will collapse any empty columns
     * is a no-op if position is invalid
     * @param position position to remove
     */
    private BoardGenerator removePosition(IntVector2D position) {
        int x = position.X();
        int y = position.Y();
        if(backbone.size() > x && backbone.get(x).size() > y) {
            backbone.get(x).remove(y);
        }
        backbone.removeIf(List::isEmpty);
        return this;
    }

    /**
     * Removes given inputs from board
     * is a no-op if position is invalid
     * @param positions positions to remove
     */
    private BoardGenerator removePositions(Stream<IntVector2D> positions) {
        positions
                // to not corrupt the positions mid removal
                .sorted((o1, o2) -> o2.Y() - o1.Y())
                .forEach(this::removePosition);
        return this;
    }

    public PlayableBoard getAsPlayableBoard() {
        return new BoardImpl(backbone);
    }

    public BoardGenerator generateFromString(String board) {
        // we will assume a properly formatted string
        int width = board.indexOf('\n') + 1;
        int height = Math.round((float)board.length() / width);
        setDimensions(width-1, height); // -1 to account for \n character
        List<IntVector2D> whiteSquares = new ArrayList<>();
        for (int i = 0; i < board.length(); i++) {
            if (board.charAt(i) != '\n') {
                int x = i % width;
                int y = height - 1 - (i / width); // -1 to make 0 based
                if(y < 0) continue;
                IntVector2D pos = IntVector2D.create(x, y);

                char colorSymbol = board.charAt(i);
                Color color = Color.fromSymbol(colorSymbol);

                this.setColor(pos, color);
                if(color == Color.WHITES_INVALID) {
                    whiteSquares.add(pos);
                }
            }
        }
        removePositions(whiteSquares.stream());
        return this;
    }

    public BoardGenerator generateRandomBoard(int width, int height) {
        Supplier<List<Color>> randomColumnGenerator = ()->Stream.generate(Color::random)
                .limit(height)
                .collect(Collectors.toList());
        List<List<Color>> generatedList = Stream.generate(randomColumnGenerator)
                .limit(width)
                .collect(Collectors.toList());
        backbone.clear();
        backbone.addAll(generatedList);
        return this;
    }

    public BoardGenerator generateFromImage(String path, int width, int height) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        StringBuilder boardStr = new StringBuilder((width + 1) * height);
        int cellWidth = img.getWidth() / width;
        int cellHeight = img.getHeight() / height;
        int cellXOffset = cellWidth / 2;
        int cellYOffset = cellHeight / 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int correctedX = x * cellWidth + cellXOffset;
                int correctedY = y * cellHeight + cellYOffset;
                int rgb = img.getRGB(correctedX, correctedY);
                Color color = Color.fromRGB(rgb);
                boardStr.append(Color.toSymbol(color));
            }
            if (y != height - 1) {
                boardStr.append('\n');
            }
        }
        return generateFromString(boardStr.toString());
    }
}

package BrickBreaker;

import utils.IntVector2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Simple game with board of colors, you can pop groups of 2+ of the same color
 * cells fall down and empty columns collapse
 */
public class Driver {

    private static Board getBoardFromString(String str) {
        Board board = new Board();
        board.generate(str);
        return board;
    }
    private static Board getStaticBoard() {
        return getBoardFromString("" +
                "GYYRGRGGGY\n" +
                "RRGRGYGGYR\n" +
                "GRRRGRYYYG\n" +
                "RGRGGGGRGY\n" +
                "GGRYYRRYGG\n" +
                "GGGYYGGGGY\n" +
                "RRYRYRRGGG\n" +
                "RRGGYYRYRG\n" +
                "RYYYRGRRGR\n" +
                "RGYGRYGGGY");
    }
    private static Board getRandomBoard() {
        Board board = new Board();
        board.generate(10, 10);
        return board;
    }

    /**
     * From facebook's brick breaker take a snippet/ of just the colored squares amd provide a path.
     * Try to keep the border close, color samples are taken from an approximated center of cell
     * This allows for a margin of error around half the width of a cell (because of their funky color fade stuff)
     * @param path path to image file
     * @param width number of cells across
     * @param height number of cells top-bottom
     * @return generated board
     * @throws IOException if there is an error loading the image
     */
    private static Board getBoardFromImage(String path, int width, int height) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        StringBuilder boardStr = new StringBuilder((width+1) * height);
        int cellWidth = img.getWidth() / width;
        int cellHeight = img.getHeight() / height;
        int cellXOffset = cellWidth / 2;
        int cellYOffset = cellHeight / 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int correctedX = x * cellWidth + cellXOffset;
                int correctedY = y * cellHeight + cellYOffset;
                int rgb = img.getRGB(correctedX,correctedY);
                Color color = Color.fromRGB(rgb);
                boardStr.append(Color.toSymbol(color));
            }
            if(y != height-1) {
                boardStr.append('\n');
            }
        }
        return getBoardFromString(boardStr.toString());
    }

    /**
     * Creates a nice display output with the given board and AI's moves
     * @param board game board
     * @param ai ai implementation
     */
    private static void playGame(Board board, AI ai) {
        System.out.println(board.getBoardString());
        List<IntVector2D> winningMoves = ai.getWinningMoveSet(board);
        int score = 0;
        for (IntVector2D move : winningMoves) {
            int newPoints = board.popCell(move);
            score += newPoints;
            System.out.printf("move: %s + %s pts Score: %s pts, resulting board: \n%s\n\n", move.add(IntVector2D.create(1,1)), newPoints, score, board.getBoardString());
            //System.out.printf("move: %s + %s pts\n\n", move, score);
        }
    }

    public static void main(String... args) throws IOException {
        AI ai = new AI.QuickAI();
        Board board;

        //board = getRandomBoard();
        //board = getStaticBoard();


        //* Load from Image

        board = getBoardFromImage("C:\\Users\\drago\\Desktop\\Capture.png",10,10);

        /*/ // Load from string

        //So it seems to break if I don't use a 10x10 board
        board = getBoardFromString("" +
                "          \n" +
                "          \n" +
                "    Y     \n" +
                "    Y     \n" +
                "    PI    \n" +
                "    PB    \n" +
                "I B IGI   \n" +
                "IIB BGB Y \n" +
                "PBIYIYG P \n" +
                "PPYYYYYGI " +
                "");
        //*/
        playGame(board,ai);
    }
}

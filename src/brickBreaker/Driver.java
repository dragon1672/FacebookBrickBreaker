package brickBreaker;

import brickBreaker.ai.AI;
import brickBreaker.ai.QuickAI;
import brickBreaker.board.BoardGenerator;
import brickBreaker.board.CachedBoard;
import brickBreaker.board.PlayableBoard;
import utils.IntVector2D;

import java.io.IOException;
import java.util.List;

/**
 * Simple game with board of colors, you can pop groups of 2+ of the same color
 * cells fall down and empty columns collapse
 */
@SuppressWarnings({"SameParameterValue", "unused"})
public class Driver {

    /**
     * Creates a nice display output with the given board and AI's moves
     *
     * @param board game board
     * @param ai    ai implementation
     */
    private static void playAIGame(PlayableBoard board, AI ai) {
        System.out.println(board.getBoardString());
        List<IntVector2D> winningMoves = ai.getWinningMoveSet(board);
        int score = 0;
        for (IntVector2D move : winningMoves) {
            int newPoints = board.popCell(move);
            score += newPoints;
            System.out.printf("move: %s + %s pts Score: %s pts, resulting board: \n%s\n\n", move.add(IntVector2D.create(1, 1)), newPoints, score, board.getBoardString());
            //System.out.printf("move: %s + %s pts\n\n", move, score);
        }
    }

    private static String getStaticBoardString() {
        //*
        return "" +
                "    Y    \n" +
                "    Y    \n" +
                "    PI   \n" +
                "    PB   \n" +
                "I B IGI  \n" +
                "IIB BGB Y\n" +
                "PBIYIYG P\n" +
                "PPYYYYYGI\n" +
                "";
        /*/
        return "" +
                "GYYRGRGGGY\n" +
                "RRGRGYGGYR\n" +
                "GRRRGRYYYG\n" +
                "RGRGGGGRGY\n" +
                "GGRYYRRYGG\n" +
                "GGGYYGGGGY\n" +
                "RRYRYRRGGG\n" +
                "RRGGYYRYRG\n" +
                "RYYYRGRRGR\n" +
                "RGYGRYGGGY\n" +
                "";
        //*/
    }

    public static void main(String... args) throws IOException {
        AI ai = new QuickAI();

        BoardGenerator boardGenerator = new BoardGenerator();

        //boardGenerator.generateRandomBoard(10,10);
        //boardGenerator.generateFromString(getStaticBoardString());
        boardGenerator.generateFromImage("Capture.png",10,10);

        PlayableBoard playableBoard = new CachedBoard(boardGenerator.getAsPlayableBoard());

        playAIGame(playableBoard, ai);
    }
}

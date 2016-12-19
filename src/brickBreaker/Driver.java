package brickBreaker;

import brickBreaker.ai.AI;
import brickBreaker.ai.FirstPathNonRecursive;
import brickBreaker.ai.RecursiveFirstPath;
import brickBreaker.board.*;
import utils.IntVector2D;
import utils.Tuple;
import utils.Tuple.Tuple2;

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
        /*
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

    private static int getAIScore(ReadOnlyBoard board, AI ai) {
        List<IntVector2D> winningMoves = ai.getWinningMoveSet(board);
        PlayableBoard boardToGetScore = BoardDuplicator.duplicate(board);
        return winningMoves.stream().map(boardToGetScore::popCell).reduce((o1,o2) -> o1 + o2).orElse(-1);
    }

    private static int battleAI(PlayableBoard board, AI ai1, AI ai2) {
        int ai1Score = getAIScore(board,ai1);
        int ai2Score = getAIScore(board,ai2);
        int winner = 0;
        if(ai1Score > ai2Score) winner = -1;
        if(ai1Score < ai2Score) winner = 1;
        String winnerStr = winner == 0 ? "tie" : winner > 0 ? "2" : "1";
        System.out.printf("Winner: %s, score Ai1: %s, Ai2: %s Diff: %s\n",winnerStr,ai1Score,ai2Score, Math.abs(ai1Score-ai2Score));
        return winner;
    }

    private static void battleAI(AI ai1, AI ai2, int rounds) {
        int overallWinner = 0;
        for (int i = 0; i < rounds; i++) {
            PlayableBoard board = new BoardGenerator().generateRandomBoard(10,10).getAsPlayableBoard();
            overallWinner += battleAI(board,ai1,ai2);
        }
        String overallWinnerString =  overallWinner == 0 ? "tie" : overallWinner > 0 ? "2" : "1";
        System.out.printf("Overall Winner: %s, with %s more wins\n\n",overallWinnerString,Math.abs(overallWinner));
    }

    private static void battleAI_main() throws IOException {
        Tuple2<AI,AI> AIs;
        //AIs = Tuple.of(new RecursiveFirstPath(),new RecursiveBoardHeuristic());
        AIs = Tuple.of(new RecursiveFirstPath(),new FirstPathNonRecursive());
        AIs = Tuple.of(new FirstPathNonRecursive(),new RecursiveFirstPath());

        /* Toggle between pre-defined board and battle with random

        PlayableBoard board;
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateFromString(getStaticBoardString());
        boardGenerator.generateFromImage("Capture.png",10,10);
        boardGenerator.generateRandomBoard(10,10);
        battleAI(boardGenerator.getAsPlayableBoard(), AIs.getFirst(),AIs.getSecond());

        /*/
        battleAI(AIs.getFirst(),AIs.getSecond(),10);
        //*/
    }

    public static void mainTester() throws IOException {
        AI ai = new RecursiveFirstPath();

        BoardGenerator boardGenerator = new BoardGenerator();

        //boardGenerator.generateRandomBoard(10,10);
        //boardGenerator.generateFromString(getStaticBoardString());
        boardGenerator.generateFromImage("Capture.png",10,10);

        PlayableBoard playableBoard = new CachedBoard(boardGenerator.getAsPlayableBoard());

        playAIGame(playableBoard, ai);
    }

    public static void main(String... args) throws IOException {
        //mainTester();
        battleAI_main();
    }
}

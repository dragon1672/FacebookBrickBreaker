package brickBreaker.ai;

import brickBreaker.board.BoardDuplicator;
import brickBreaker.board.PlayableBoard;
import brickBreaker.board.ReadOnlyBoard;
import utils.IntVector2D;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Returns the first found path not using recursion
 */
public class FirstPathNonRecursive extends AI {
    private static int boardHeuristicMovePoints(ReadOnlyBoard board) {
        return getAllPossibleMovesMap(board).values().stream().reduce((integer, integer2) -> integer + integer2).orElse(-1);
    }
    private static class MovePossibility {
        final MovePossibility parent;
        final PlayableBoard boardInstance;
        final IntVector2D moveToMake;
        final int score;
        final int boardHeuristic;

        MovePossibility(ReadOnlyBoard boardInstance, IntVector2D moveToMake) {
            this.parent = null;
            this.boardInstance = BoardDuplicator.duplicate(boardInstance);
            this.moveToMake = moveToMake;
            this.score = this.boardInstance.popCell(moveToMake);
            this.boardHeuristic = boardHeuristicMovePoints(this.boardInstance);
        }

        MovePossibility(MovePossibility parent, IntVector2D moveToMake) {
            this.parent = parent;
            this.boardInstance = BoardDuplicator.duplicate(parent.boardInstance);
            this.moveToMake = moveToMake;
            this.score = boardInstance.popCell(moveToMake);
            this.boardHeuristic = boardHeuristicMovePoints(this.boardInstance);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MovePossibility that = (MovePossibility) o;

            if (score != that.score) return false;
            if (boardInstance != null ? !boardInstance.equals(that.boardInstance) : that.boardInstance != null)
                return false;
            return moveToMake != null ? moveToMake.equals(that.moveToMake) : that.moveToMake == null;
        }

        @Override
        public int hashCode() {
            int result = boardInstance != null ? boardInstance.hashCode() : 0;
            result = 31 * result + (moveToMake != null ? moveToMake.hashCode() : 0);
            result = 31 * result + score;
            return result;
        }
    }

    private Stream<MovePossibility> getAllMoves(ReadOnlyBoard boardInput) {
        return getAllPossibleMoves(boardInput)
                .stream()
                .map(move -> new MovePossibility(boardInput,move));
    }
    private Stream<MovePossibility> getAllMoves(MovePossibility parent) {
        return getAllPossibleMoves(parent.boardInstance)
                .stream()
                .map(move -> new MovePossibility(parent,move));
    }

    private List<IntVector2D> makeMoveList(MovePossibility movePossibility) {
        if(movePossibility == null) {
            return new ArrayList<>();
        }
        List<IntVector2D> ret = makeMoveList(movePossibility.parent);
        ret.add(movePossibility.moveToMake);
        return ret;
    }

    @Override
    public List<IntVector2D> getWinningMoveSet(ReadOnlyBoard originalBoard) {
        // can I thread this somehow?
        SortedSet<MovePossibility> possibleMoves = new TreeSet<>(Comparator.comparingInt(o -> o.boardHeuristic));
        possibleMoves.addAll(getAllMoves(originalBoard).collect(Collectors.toList()));
        while(!possibleMoves.isEmpty()) {
            // calling .first() or .last() wasn't giving me the proper sorted item :(
            MovePossibility possibleMove = possibleMoves.stream().findFirst().orElse(null);
            possibleMoves.remove(possibleMove);

            if(possibleMove.boardInstance.isEmpty()) {
                return makeMoveList(possibleMove);
            }
            possibleMoves.addAll(getAllMoves(possibleMove).collect(Collectors.toList()));
        }
        throw new IllegalArgumentException("Board is unsolvable");
    }
}

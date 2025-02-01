package de.junaeisenhauer.puzzlesolver.algorithm;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A node for the puzzle.
 */
public class PuzzleNode implements Node<int[]> {

    @Getter
    private PuzzleNode parent;
    private PuzzleSize size;
    /**
     * Value of 0 represents the space in the puzzle. The algorithm considers the 0 (the space) as the moving tile.
     */
    private int[] state;
    @Getter
    private MoveDirection moveDirection;
    private List<Node<int[]>> children;
    private int heuristicEstimation = -1;
    private int costs = -1;

    public PuzzleNode(PuzzleNode parent, PuzzleSize size, int[] state, MoveDirection moveDirection) {
        this.parent = parent;
        this.size = size;
        this.state = state;
        this.moveDirection = moveDirection;
    }

    @Override
    public int[] getState() {
        return state.clone();
    }

    @Override
    public boolean isGoal() {
        for (int i = 0; i < state.length - 1; i++) {
            if (state[i] != i + 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Node<int[]>> getChildren() {
        if (children != null) {
            return children;
        }
        children = generateChildren();
        return children;
    }

    @Override
    public int getHeuristicEstimation() {
        if (heuristicEstimation != -1) {
            return heuristicEstimation;
        }

        // manhattan metric
        heuristicEstimation = 0;
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0) {
                continue;
            }

            int actualX = i % size.getWidth();
            int actualY = (i - actualX) / size.getWidth();

            int goalPos = state[i] - 1;
            int goalX = goalPos % size.getWidth();
            int goalY = (goalPos - goalX) / size.getWidth();

            heuristicEstimation = Math.abs(goalX - actualX) + Math.abs(goalY - actualY);
        }

        return heuristicEstimation;
    }

    @Override
    public int getCosts() {
        if (costs != -1) {
            return costs;
        }
        costs = 0;
        Node<?> parent = this.parent;
        while (parent != null) {
            costs++;
            parent = parent.getParent();
        }
        return costs;
    }

    private List<Node<int[]>> generateChildren() {
        List<Node<int[]>> children = new ArrayList<>();

        int spacePosition = getSpacePosition();

        Node<int[]> up = moveUp(spacePosition);
        addChildIfValid(children, up);

        Node<int[]> down = moveDown(spacePosition);
        addChildIfValid(children, down);

        Node<int[]> right = moveRight(spacePosition);
        addChildIfValid(children, right);

        Node<int[]> left = moveLeft(spacePosition);
        addChildIfValid(children, left);

        return children;
    }

    private void addChildIfValid(List<Node<int[]>> children, Node<int[]> child) {
        if (child != null) {
            children.add(child);
        }
    }

    private int getSpacePosition() {
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0) {
                return i;
            }
        }
        throw new IllegalStateException("Space (0) not found");
    }

    private Node<int[]> moveUp(int spacePosition) {
        if (moveDirection == MoveDirection.DOWN) {
            // cycle check of length 2
            return null;
        }
        if (spacePosition < size.getWidth()) {
            // can not move up
            return null;
        }
        int[] upState = state.clone();
        swap(upState, spacePosition, spacePosition - size.getWidth());
        return new PuzzleNode(this, size, upState, MoveDirection.UP);
    }

    private Node<int[]> moveDown(int spacePosition) {
        if (moveDirection == MoveDirection.UP) {
            // cycle check of length 2
            return null;
        }
        if (spacePosition >= size.getWidth() * (size.getHeight() - 1)) {
            // can not move down
            return null;
        }
        int[] downState = state.clone();
        swap(downState, spacePosition, spacePosition + size.getWidth());
        return new PuzzleNode(this, size, downState, MoveDirection.DOWN);
    }

    private Node<int[]> moveRight(int spacePosition) {
        if (moveDirection == MoveDirection.LEFT) {
            // cycle check of length 2
            return null;
        }
        if (spacePosition % size.getWidth() == size.getWidth() - 1) {
            // can not move right
            return null;
        }
        int[] leftState = state.clone();
        swap(leftState, spacePosition, spacePosition + 1);
        return new PuzzleNode(this, size, leftState, MoveDirection.RIGHT);
    }

    private Node<int[]> moveLeft(int spacePosition) {
        if (moveDirection == MoveDirection.RIGHT) {
            // cycle check of length 2
            return null;
        }
        if (spacePosition % size.getWidth() == 0) {
            // can not move left
            return null;
        }
        int[] rightState = state.clone();
        swap(rightState, spacePosition, spacePosition - 1);
        return new PuzzleNode(this, size, rightState, MoveDirection.LEFT);
    }

    private void swap(int[] array, int pos1, int pos2) {
        // Bitwise xor swap
        array[pos1] = array[pos1] ^ array[pos2];
        array[pos2] = array[pos1] ^ array[pos2];
        array[pos1] = array[pos1] ^ array[pos2];
    }

    public enum MoveDirection {
        UP,
        DOWN,
        RIGHT,
        LEFT
    }

    @AllArgsConstructor
    @Getter
    public static class PuzzleSize {
        private int width;
        private int height;
    }

}

package de.junaeisenhauer.puzzlesolver.algorithm.strategy;

import de.junaeisenhauer.puzzlesolver.algorithm.Node;

import java.util.List;
import java.util.Random;

/**
 * Shuffle strategy is not really a solve strategy because it only does random actions.
 * It is used to randomize a node path.
 */
public class ShuffleStrategy implements SolveStrategy {

    private final Random random;
    private int shuffleNumber;

    public ShuffleStrategy(int shuffleNumber) {
        this.shuffleNumber = shuffleNumber;
        random = new Random();
    }

    @Override
    public <T> Node<T> solve(Node<T> start) {
        Node<T> result = start;
        for (int i = 0; i < shuffleNumber; i++) {
            result = shuffle(result);
        }

        return result;
    }

    private <T> Node<T> shuffle(Node<T> node) {
        List<Node<T>> children = node.getChildren();
        int randomPosition = random.nextInt(children.size());
        return children.get(randomPosition);
    }

}

package de.junaeisenhauer.puzzlesolver.algorithm.strategy;

import de.junaeisenhauer.puzzlesolver.algorithm.Node;

import java.util.List;

public class IterativeDeepening implements SolveStrategy {

    @Override
    public <T> Node<T> solve(Node<T> start) {
        int limit = 0;
        Node<T> result;
        do {
            result = depthSearchB(start, 0, limit);
            limit++;
        } while (result == null);
        return result;
    }

    private <T> Node<T> depthSearchB(Node<T> node, int depth, int limit) {
        if (node.isGoal()) {
            return node;
        }
        List<Node<T>> children = node.getChildren();
        while (!children.isEmpty() && depth < limit) {
            Node<T> result = depthSearchB(children.get(0), depth + 1, limit);
            if (result != null) {
                return result;
            }
            children.remove(0);
        }

        return null;
    }

}

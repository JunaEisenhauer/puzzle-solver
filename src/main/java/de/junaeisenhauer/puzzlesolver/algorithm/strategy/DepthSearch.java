package de.junaeisenhauer.puzzlesolver.algorithm.strategy;

import de.junaeisenhauer.puzzlesolver.algorithm.Node;

import java.util.List;

public class DepthSearch implements SolveStrategy {
    @Override
    public <T> Node<T> solve(Node<T> node) {
        if (node.isGoal()) {
            return node;
        }

        List<Node<T>> children = node.getChildren();
        while (!children.isEmpty()) {
            Node<T> result = solve(children.get(0));
            if (result != null) {
                return result;
            }
            children.remove(0);
        }

        return null;
    }

}

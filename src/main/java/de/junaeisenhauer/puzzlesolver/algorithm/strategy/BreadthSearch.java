package de.junaeisenhauer.puzzlesolver.algorithm.strategy;

import de.junaeisenhauer.puzzlesolver.algorithm.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BreadthSearch implements SolveStrategy {

    @Override
    public <T> Node<T> solve(Node<T> start) {
        return solve(Collections.singletonList(start));
    }

    private <T> Node<T> solve(List<Node<T>> nodes) {
        List<Node<T>> children = new ArrayList<>();
        for (Node<T> node : nodes) {
            if (node.isGoal()) {
                return node;
            }
            children.addAll(node.getChildren());
        }

        if (children.isEmpty()) {
            return null;
        }
        return solve(children);
    }

}

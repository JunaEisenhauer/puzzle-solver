package de.junaeisenhauer.puzzlesolver.algorithm.strategy;

import de.junaeisenhauer.puzzlesolver.algorithm.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class AStarSearch implements SolveStrategy {

    @Override
    public <T> Node<T> solve(Node<T> start) {
        // Priority Queue is based on a priority, binary heap
        PriorityQueue<Node<T>> open = new PriorityQueue<>(createHeuristicComparator());
        List<Node<T>> closed = new ArrayList<>();

        open.add(start);

        while (!open.isEmpty()) {
            Node<T> node = open.poll();
            if (node.isGoal()) {
                return node;
            }
            closed.add(node);

            List<Node<T>> children = node.getChildren();
            for (Node<T> child : children) {
                if (closed.contains(child)) {
                    continue;
                }
                if (open.contains(child)) {
                    continue;
                }

                open.add(child);
            }
        }

        return null;
    }

    private Comparator<Node<?>> createHeuristicComparator() {
        return (node1, node2) -> {
            int heuristicEvaluation1 = node1.getHeuristicEstimation() + node1.getCosts();
            int heuristicEvaluation2 = node2.getHeuristicEstimation() + node2.getCosts();
            return heuristicEvaluation1 - heuristicEvaluation2;
        };
    }

}

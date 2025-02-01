package de.junaeisenhauer.puzzlesolver.algorithm.strategy;

import de.junaeisenhauer.puzzlesolver.algorithm.Node;

/**
 * The interface to provide a algorithm to solve the problem.
 * -> Strategy design pattern
 */
public interface SolveStrategy {

    /**
     * Solves a problem with a start node.
     *
     * @param start the start node of the problem
     * @return the goal node with it's parent to reproduce the path of the solution
     */
    <T> Node<T> solve(Node<T> start);

}

package de.junaeisenhauer.puzzlesolver.algorithm;

import java.util.List;

/**
 * Represents a single state with a generic type for the state.
 */
public interface Node<T> {

    /**
     * Gets the state of this node.
     *
     * @return the state of this node with a generic type
     */
    T getState();

    /**
     * Checks if this node is the goal.
     *
     * @return if this node is the goal
     */
    boolean isGoal();

    /**
     * Gets the parent node which represents the direct predecessor of this node.
     *
     * @return the parent node
     */
    Node<T> getParent();

    /**
     * Gets all children which represents all direct successors of this node.
     *
     * @return a list of all children nodes
     */
    List<Node<T>> getChildren();

    /**
     * Gets the heuristic estimation costs for heuristic search algorithms.
     *
     * @return the estimated heuristic costs
     */
    int getHeuristicEstimation();

    /**
     * Gets the actual costs from the start to this node.
     *
     * @return the costs from the start to this node
     */
    int getCosts();

}

package net.automatalib.util.traversal;

public enum SearchEventType {
	/**
	 * Processing of an initial state
	 */
	INIT,
	/**
	 * Starting exploration of state or node.
	 */
	START,
	/**
	 * Finished processing of a state or node (DFS: backtracking)
	 */
	FINISH,
	/**
	 * An edge in the search tree.
	 */
	TREE_EDGE,
	/**
	 * A non-tree edge to a descendant of the current state or node in the
	 * search tree. 
	 */
	FORWARD_EDGE,
	/**
	 * A non-tree edge to a predecessor (or reflexive edge) of the current state or node in
	 * the search tree.
	 */
	BACK_EDGE,
	/**
	 * A non-tree edge to an unrelated state or node (neither descendant nor ancestor)
	 * of the current state or node in the search tree.
	 */
	CROSS_EDGE
}
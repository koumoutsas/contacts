package com.kareebo.contacts.server.handler;

import javax.annotation.Nonnull;
import java.util.HashSet;

/**
 * Accessor interface for contacts graph
 */
public interface GraphAccessor
{
	/**
	 * Add a set of edges from a node to a set of nodes. If one of the endpoints doesn't exist, it is created as a node. If an edge already exists, it is ignored
	 *
	 * @param from The common node for the added edges
	 * @param to   The set of other endpoints
	 */
	void addEdges(final Long from,final @Nonnull HashSet<Long> to);

	/**
	 * Remove a set of edges from a node. If a node becomes isolated, it is removed
	 *
	 * @param from The common node for all edges
	 * @param to   The set of other endpoints
	 * @throws IllegalStateException When an edge to be removed doesn't already exist
	 */
	void removeEdges(final @Nonnull Long from,final @Nonnull HashSet<Long> to) throws IllegalStateException;

	/**
	 * Finalize the graph and store it
	 */
	void close();
}
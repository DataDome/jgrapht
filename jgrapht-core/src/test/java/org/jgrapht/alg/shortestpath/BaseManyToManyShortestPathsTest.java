/*
 * (C) Copyright 2019-2021, by Semen Chudakov and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.alg.shortestpath;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Base test for many-to-many shortest paths algorithms. Currently extended by
 * {@link CHManyToManyShortestPathsTest}, {@link DijkstraManyToManyShortestPathsTest} and
 * {@link DefaultManyToManyShortestPathsTest}.
 *
 * @author Semen Chudakov
 */
public abstract class BaseManyToManyShortestPathsTest
{
    /**
     * Seed for random numbers generator used in tests.
     */
    protected static final long SEED = 17L;

    /**
     * Provides implementation of
     * {@link org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths}
     * to be tested.
     *
     * @param graph a graph
     * @return algorithm implementation
     */
    protected abstract ManyToManyShortestPathsAlgorithm<Integer, DefaultWeightedEdge> getAlgorithm(
        Graph<Integer, DefaultWeightedEdge> graph);

    /**
     * Tests provided algorithm on an empty graph to ensure no exception is thrown.
     */
    protected void testEmptyGraph()
    {
        Graph<Integer, DefaultWeightedEdge> graph =
            new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        ManyToManyShortestPathsAlgorithm<Integer, DefaultWeightedEdge> algorithm =
            getAlgorithm(graph);
        algorithm.getManyToManyPaths(Collections.emptySet(), Collections.emptySet());
    }

    /**
     * Checks that provided implementation throws exception when source vertices set is null.
     */
    protected void testSourcesIsNull()
    {
        Graph<Integer, DefaultWeightedEdge> graph =
            new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        ManyToManyShortestPathsAlgorithm<Integer, DefaultWeightedEdge> algorithm =
            getAlgorithm(graph);
        algorithm.getManyToManyPaths(null, Collections.emptySet());
    }

    /**
     * Checks that provided implementation throws exception when target vertices set is null.
     */
    protected void testTargetsIsNull()
    {
        Graph<Integer, DefaultWeightedEdge> graph =
            new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        ManyToManyShortestPathsAlgorithm<Integer, DefaultWeightedEdge> algorithm =
            getAlgorithm(graph);
        algorithm.getManyToManyPaths(Collections.emptySet(), null);
    }

    /**
     * Checks that provided implementation returns {@link Double#POSITIVE_INFINITY} when there is no
     * path between a source and a target as well as that the returned path is $null$.
     */
    protected void testNoPath()
    {
        Graph<Integer, DefaultWeightedEdge> graph =
            new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);

        ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Integer,
            DefaultWeightedEdge> shortestPaths =
                getAlgorithm(graph).getManyToManyPaths(new HashSet<>(Arrays.asList(1)), new HashSet<>(Arrays.asList(2)));

        assertEquals(Double.POSITIVE_INFINITY, shortestPaths.getWeight(1, 2), 1e-9);
        assertNull(shortestPaths.getPath(1, 2));
    }

    /**
     * Test provided algorithm on the graph generated by {@code getSimpleGraph} using disjoint sets
     * of source and target vertices.
     */
    protected void testDifferentSourcesAndTargetsSimpleGraph()
    {
        ManyToManyShortestPathsAlgorithm<Integer, DefaultWeightedEdge> algorithm =
            getAlgorithm(getSimpleGraph());

        ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Integer,
            DefaultWeightedEdge> shortestPaths =
                algorithm.getManyToManyPaths(new HashSet<>(Arrays.asList(4, 1, 2)), new HashSet<>(Arrays.asList(8, 9, 6)));

        assertEquals(2.0, shortestPaths.getWeight(4, 8), 1e-9);
        assertEquals(Arrays.asList(4, 5, 8), shortestPaths.getPath(4, 8).getVertexList());

        assertEquals(3.0, shortestPaths.getWeight(4, 9), 1e-9);
        assertEquals(Arrays.asList(4, 5, 6, 9), shortestPaths.getPath(4, 9).getVertexList());

        assertEquals(2.0, shortestPaths.getWeight(4, 6), 1e-9);
        assertEquals(Arrays.asList(4, 5, 6), shortestPaths.getPath(4, 6).getVertexList());

        assertEquals(3.0, shortestPaths.getWeight(1, 8), 1e-9);
        assertEquals(Arrays.asList(1, 4, 5, 8), shortestPaths.getPath(1, 8).getVertexList());

        assertEquals(4.0, shortestPaths.getWeight(1, 9), 1e-9);
        assertEquals(Arrays.asList(1, 4, 5, 6, 9), shortestPaths.getPath(1, 9).getVertexList());

        assertEquals(3.0, shortestPaths.getWeight(1, 6), 1e-9);
        assertEquals(Arrays.asList(1, 4, 5, 6), shortestPaths.getPath(1, 6).getVertexList());

        assertEquals(2.0, shortestPaths.getWeight(2, 8), 1e-9);
        assertEquals(Arrays.asList(2, 5, 8), shortestPaths.getPath(2, 8).getVertexList());

        assertEquals(3.0, shortestPaths.getWeight(2, 9), 1e-9);
        assertEquals(Arrays.asList(2, 5, 6, 9), shortestPaths.getPath(2, 9).getVertexList());

        assertEquals(2.0, shortestPaths.getWeight(2, 6), 1e-9);
        assertEquals(Arrays.asList(2, 5, 6), shortestPaths.getPath(2, 6).getVertexList());
    }

    /**
     * Test provided algorithm on the graph generated by {@code getMultigraph} using disjoint sets
     * of source and target vertices.
     */
    protected void testDifferentSourcesAndTargetsMultigraph()
    {
        ManyToManyShortestPathsAlgorithm<Integer, DefaultWeightedEdge> algorithm =
            getAlgorithm(getMultigraph());

        ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Integer,
            DefaultWeightedEdge> shortestPaths =
                algorithm.getManyToManyPaths(new HashSet<>(Arrays.asList(1, 4)), new HashSet<>(Arrays.asList(2, 5)));

        assertEquals(1.0, shortestPaths.getWeight(1, 2), 1e-9);
        assertEquals(Arrays.asList(1, 2), shortestPaths.getPath(1, 2).getVertexList());

        assertEquals(32, shortestPaths.getWeight(1, 5), 1e-9);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), shortestPaths.getPath(1, 5).getVertexList());

        assertEquals(16, shortestPaths.getWeight(4, 2), 1e-9);
        assertEquals(Arrays.asList(4, 3, 2), shortestPaths.getPath(4, 2).getVertexList());

        assertEquals(15, shortestPaths.getWeight(4, 5), 1e-9);
        assertEquals(Arrays.asList(4, 5), shortestPaths.getPath(4, 5).getVertexList());

    }

    /**
     * Test provided algorithm on the graph generated by {@code getSimpleGraph} using the same
     * source and target vertices.
     */
    protected void testSourcesEqualTargetsSimpleGraph()
    {
        ManyToManyShortestPathsAlgorithm<Integer, DefaultWeightedEdge> algorithm =
            getAlgorithm(getSimpleGraph());

        ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Integer,
            DefaultWeightedEdge> shortestPaths =
                algorithm.getManyToManyPaths(new HashSet<>(Arrays.asList(1, 5, 9)), new HashSet<>(Arrays.asList(1, 5, 9)));

        assertEquals(0.0, shortestPaths.getWeight(1, 1), 1e-9);
        assertEquals(Collections.singletonList(1), shortestPaths.getPath(1, 1).getVertexList());

        assertEquals(0.0, shortestPaths.getWeight(5, 5), 1e-9);
        assertEquals(Collections.singletonList(5), shortestPaths.getPath(5, 5).getVertexList());

        assertEquals(0.0, shortestPaths.getWeight(9, 9), 1e-9);
        assertEquals(Collections.singletonList(9), shortestPaths.getPath(9, 9).getVertexList());

        assertEquals(2.0, shortestPaths.getWeight(1, 5), 1e-9);
        assertEquals(Arrays.asList(1, 4, 5), shortestPaths.getPath(1, 5).getVertexList());
        assertEquals(2.0, shortestPaths.getWeight(5, 1), 1e-9);
        assertEquals(Arrays.asList(5, 4, 1), shortestPaths.getPath(5, 1).getVertexList());

        assertEquals(4.0, shortestPaths.getWeight(1, 9), 1e-9);
        assertEquals(Arrays.asList(1, 4, 5, 6, 9), shortestPaths.getPath(1, 9).getVertexList());
        assertEquals(4.0, shortestPaths.getWeight(9, 1), 1e-9);
        assertEquals(Arrays.asList(9, 6, 5, 4, 1), shortestPaths.getPath(9, 1).getVertexList());

        assertEquals(2.0, shortestPaths.getWeight(5, 9), 1e-9);
        assertEquals(Arrays.asList(5, 6, 9), shortestPaths.getPath(5, 9).getVertexList());
        assertEquals(2.0, shortestPaths.getWeight(9, 5), 1e-9);
        assertEquals(Arrays.asList(9, 6, 5), shortestPaths.getPath(9, 5).getVertexList());
    }

    /**
     * Test provided algorithm on the graph generated by {@code getMultigraph} using the same source
     * and target vertices.
     */
    protected void testSourcesEqualTargetsMultigraph()
    {
        ManyToManyShortestPathsAlgorithm<Integer, DefaultWeightedEdge> algorithm =
            getAlgorithm(getMultigraph());

        ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Integer,
            DefaultWeightedEdge> shortestPaths =
                algorithm.getManyToManyPaths(new HashSet<>(Arrays.asList(2, 4, 6)), new HashSet<>(Arrays.asList(2, 4, 6)));

        assertEquals(0.0, shortestPaths.getWeight(2, 2), 1e-9);
        assertEquals(Collections.singletonList(2), shortestPaths.getPath(2, 2).getVertexList());

        assertEquals(0.0, shortestPaths.getWeight(4, 4), 1e-9);
        assertEquals(Collections.singletonList(4), shortestPaths.getPath(4, 4).getVertexList());

        assertEquals(0.0, shortestPaths.getWeight(6, 6), 1e-9);
        assertEquals(Collections.singletonList(6), shortestPaths.getPath(6, 6).getVertexList());

        assertEquals(16.0, shortestPaths.getWeight(2, 4), 1e-9);
        assertEquals(Arrays.asList(2, 3, 4), shortestPaths.getPath(2, 4).getVertexList());
        assertEquals(16.0, shortestPaths.getWeight(4, 2), 1e-9);
        assertEquals(Arrays.asList(4, 3, 2), shortestPaths.getPath(4, 2).getVertexList());

        assertEquals(24.0, shortestPaths.getWeight(2, 6), 1e-9);
        assertEquals(Arrays.asList(2, 1, 6), shortestPaths.getPath(2, 6).getVertexList());
        assertEquals(24.0, shortestPaths.getWeight(6, 2), 1e-9);
        assertEquals(Arrays.asList(6, 1, 2), shortestPaths.getPath(6, 2).getVertexList());

        assertEquals(32.0, shortestPaths.getWeight(4, 6), 1e-9);
        assertEquals(Arrays.asList(4, 5, 6), shortestPaths.getPath(4, 6).getVertexList());
        assertEquals(32.0, shortestPaths.getWeight(6, 4), 1e-9);
        assertEquals(Arrays.asList(6, 5, 4), shortestPaths.getPath(6, 4).getVertexList());
    }

    /**
     * Tests provided algorithm on randomly generated graphs.
     *
     * @param numOfVertices number of vertices in random graphs
     * @param vertexDegree vertex degree in random graphs
     * @param numOfSourcesAndTargets number of source and target vertices
     * @param numOfIterations number of test iterations for each random graph
     */
    protected void testOnRandomGraphs(
        int numOfVertices, int vertexDegree, int[][] numOfSourcesAndTargets, int numOfIterations)
    {
        Random random = new Random(SEED);

        for (int[] randomVertices : numOfSourcesAndTargets) {
            for (int i = 0; i < numOfIterations; i++) {
                Graph<Integer, DefaultWeightedEdge> graph =
                    generateRandomGraph(numOfVertices, vertexDegree * numOfVertices, random);

                Set<Integer> sources = getRandomVertices(graph, randomVertices[0], random);
                Set<Integer> targets = getRandomVertices(graph, randomVertices[1], random);
                testOnGraph(graph, sources, targets);
            }
        }
    }

    /**
     * Tests provided algorithm on {@code graph} using {@code sources} and {@code targets}.
     *
     * @param graph a test graph instance
     * @param sources source vertices
     * @param targets target vertices
     */
    protected void testOnGraph(
        Graph<Integer, DefaultWeightedEdge> graph, Set<Integer> sources, Set<Integer> targets)
    {
        ManyToManyShortestPathsAlgorithm<Integer, DefaultWeightedEdge> algorithm =
            getAlgorithm(graph);

        ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Integer,
            DefaultWeightedEdge> sourcesToTargetsPaths =
                algorithm.getManyToManyPaths(sources, targets);

        ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Integer,
            DefaultWeightedEdge> sourcesToSourcesPaths =
                algorithm.getManyToManyPaths(sources, sources);

        assertCorrectPaths(graph, sourcesToTargetsPaths, sources, targets);
        assertCorrectPaths(graph, sourcesToSourcesPaths, sources, sources);
    }

    /**
     * Generates a graph instance from the $G(n,M)$ random graphs model with {@code numOfVertices}
     * vertices and {@code numOfEdges} edges.
     *
     * @param numOfVertices number of vertices in a graph
     * @param numOfEdges number of edges in a graph
     * @param random random generator
     * @return random graph
     */
    protected Graph<Integer, DefaultWeightedEdge> generateRandomGraph(
        int numOfVertices, int numOfEdges, Random random)
    {
        DirectedWeightedPseudograph<Integer, DefaultWeightedEdge> graph =
            new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);
        graph.setVertexSupplier(SupplierUtil.createIntegerSupplier());

        GraphGenerator<Integer, DefaultWeightedEdge, Integer> generator =
            new GnmRandomGraphGenerator<>(numOfVertices, numOfEdges - numOfVertices + 1, SEED);
        generator.generateGraph(graph);
        makeConnected(graph);
        addEdgeWeights(graph, random);

        return graph;
    }

    /**
     * Makes {@code graph} connected.
     *
     * @param graph a graph
     */
    protected void makeConnected(Graph<Integer, DefaultWeightedEdge> graph)
    {
        Object[] vertices = graph.vertexSet().toArray();
        for (int i = 0; i < vertices.length - 1; ++i) {
            graph.addEdge((Integer) vertices[i], (Integer) vertices[i + 1]);
            graph.addEdge((Integer) vertices[i + 1], (Integer) vertices[i]);
        }
    }

    /**
     * Sets weight for every edge in the {@code graph}.
     *
     * @param graph a graph
     * @param random random generator instance
     */
    protected void addEdgeWeights(Graph<Integer, DefaultWeightedEdge> graph, Random random)
    {
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            graph.setEdgeWeight(edge, random.nextDouble());
        }
    }

    /**
     * Asserts that shortest paths stored in {@code paths} are correct. {@link DijkstraShortestPath}
     * algorithm is used a certificate of correctness.
     *
     * @param graph a graph
     * @param paths many-to-many shortest paths object
     * @param sources source vertices
     * @param targets target vertices
     */
    protected void assertCorrectPaths(
        Graph<Integer, DefaultWeightedEdge> graph,
        ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Integer,
            DefaultWeightedEdge> paths,
        Set<Integer> sources, Set<Integer> targets)
    {
        ShortestPathAlgorithm<Integer, DefaultWeightedEdge> dijkstra =
            new DijkstraShortestPath<>(graph);
        for (Integer source : sources) {
            ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> expectedPaths =
                dijkstra.getPaths(source);
            for (Integer target : targets) {
                GraphPath<Integer, DefaultWeightedEdge> expected = expectedPaths.getPath(target);
                GraphPath<Integer, DefaultWeightedEdge> actual = paths.getPath(source, target);
                assertEquals(expected.getWeight(), actual.getWeight(), 1e-9);
                assertEquals(expected.getVertexList(), actual.getVertexList());
            }
        }
    }

    /**
     * Generates list of randomly selected vertices from the given {@code graph}.
     *
     * @param graph a graph
     * @param numOfRandomVertices number of vertices to return
     * @param random random numbers generator
     * @return list of random vertices
     */
    protected Set<Integer> getRandomVertices(
        Graph<Integer, DefaultWeightedEdge> graph, int numOfRandomVertices, Random random)
    {
        Set<Integer> result = new HashSet<>(numOfRandomVertices);
        Integer[] graphVertices = graph.vertexSet().toArray(new Integer[0]);

        for (int i = 0; i < numOfRandomVertices; ++i) {
            int vertex = random.nextInt(graph.vertexSet().size());
            while (result.contains(vertex)) {
                vertex = graphVertices[random.nextInt(graph.vertexSet().size())];
            }

            result.add(vertex);
        }

        return result;
    }

    /**
     * Generates simple graph for test cases.
     *
     * @return test graph
     */
    protected Graph<Integer, DefaultWeightedEdge> getSimpleGraph()
    {
        Graph<Integer, DefaultWeightedEdge> graph =
            new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        Graphs.addEdgeWithVertices(graph, 1, 2, 3);
        Graphs.addEdgeWithVertices(graph, 1, 4, 1);

        Graphs.addEdgeWithVertices(graph, 2, 3, 3);
        Graphs.addEdgeWithVertices(graph, 2, 5, 1);

        Graphs.addEdgeWithVertices(graph, 3, 6, 1);

        Graphs.addEdgeWithVertices(graph, 4, 5, 1);
        Graphs.addEdgeWithVertices(graph, 4, 7, 1);

        Graphs.addEdgeWithVertices(graph, 5, 6, 1);
        Graphs.addEdgeWithVertices(graph, 5, 8, 1);

        Graphs.addEdgeWithVertices(graph, 6, 9, 1);

        Graphs.addEdgeWithVertices(graph, 7, 8, 3);
        Graphs.addEdgeWithVertices(graph, 8, 9, 3);

        return graph;
    }

    /**
     * Generates multigraph for test cases.
     *
     * @return test graph
     */
    protected Graph<Integer, DefaultWeightedEdge> getMultigraph()
    {
        Graph<Integer, DefaultWeightedEdge> graph =
            new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);

        Graphs.addEdgeWithVertices(graph, 1, 2, 1);
        Graphs.addEdgeWithVertices(graph, 1, 2, 2);
        Graphs.addEdgeWithVertices(graph, 2, 1, 3);
        Graphs.addEdgeWithVertices(graph, 2, 1, 4);

        Graphs.addEdgeWithVertices(graph, 2, 3, 8);
        Graphs.addEdgeWithVertices(graph, 2, 3, 7);
        Graphs.addEdgeWithVertices(graph, 3, 2, 6);
        Graphs.addEdgeWithVertices(graph, 3, 2, 5);

        Graphs.addEdgeWithVertices(graph, 3, 4, 9);
        Graphs.addEdgeWithVertices(graph, 3, 4, 10);
        Graphs.addEdgeWithVertices(graph, 4, 3, 11);
        Graphs.addEdgeWithVertices(graph, 4, 3, 12);

        Graphs.addEdgeWithVertices(graph, 4, 5, 16);
        Graphs.addEdgeWithVertices(graph, 4, 5, 15);
        Graphs.addEdgeWithVertices(graph, 5, 4, 14);
        Graphs.addEdgeWithVertices(graph, 5, 4, 13);

        Graphs.addEdgeWithVertices(graph, 5, 6, 17);
        Graphs.addEdgeWithVertices(graph, 5, 6, 18);
        Graphs.addEdgeWithVertices(graph, 6, 5, 19);
        Graphs.addEdgeWithVertices(graph, 6, 5, 20);

        Graphs.addEdgeWithVertices(graph, 6, 1, 24);
        Graphs.addEdgeWithVertices(graph, 6, 1, 23);
        Graphs.addEdgeWithVertices(graph, 1, 6, 22);
        Graphs.addEdgeWithVertices(graph, 1, 6, 21);

        return graph;
    }

    protected void testNoPathMultiSet()
    {
        Graph<Integer, DefaultWeightedEdge> graph =
            new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);

        ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Integer,
            DefaultWeightedEdge> shortestPaths =
                getAlgorithm(graph).getManyToManyPaths(new HashSet<>(Arrays.asList(1)), new HashSet<>(Arrays.asList(2, 3)));

        assertEquals(Double.POSITIVE_INFINITY, shortestPaths.getWeight(1, 2), 1e-9);
        assertEquals(Double.POSITIVE_INFINITY, shortestPaths.getWeight(1, 3), 1e-9);
        assertNull(shortestPaths.getPath(1, 2));
        assertNull(shortestPaths.getPath(1, 3));
    }

}

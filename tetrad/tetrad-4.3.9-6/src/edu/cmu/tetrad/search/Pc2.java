///////////////////////////////////////////////////////////////////////////////
// For information as to what this class does, see the Javadoc, below.       //
// Copyright (C) 2005 by Peter Spirtes, Richard Scheines, Joseph Ramsey,     //
// and Clark Glymour.                                                        //
//                                                                           //
// This program is free software; you can redistribute it and/or modify      //
// it under the terms of the GNU General Public License as published by      //
// the Free Software Foundation; either version 2 of the License, or         //
// (at your option) any later version.                                       //
//                                                                           //
// This program is distributed in the hope that it will be useful,           //
// but WITHOUT ANY WARRANTY; without even the implied warranty of            //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             //
// GNU General Public License for more details.                              //
//                                                                           //
// You should have received a copy of the GNU General Public License         //
// along with this program; if not, write to the Free Software               //
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA //
///////////////////////////////////////////////////////////////////////////////

package edu.cmu.tetrad.search;

import edu.cmu.tetrad.data.Knowledge;
import edu.cmu.tetrad.graph.*;
import edu.cmu.tetrad.search.indtest.IndependenceTest;
import edu.cmu.tetrad.search.indtest.SearchLogUtils;
import edu.cmu.tetrad.util.ChoiceGenerator;
import edu.cmu.tetrad.util.TetradLogger;
import edu.cmu.tetrad.util.TetradLoggerConfig;
import edu.cmu.tetrad.util.DepthChoiceGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements the PC ("Peter/Clark") algorithm, as specified in Chapter 6 of
 * Spirtes, Glymour, and Scheines, "Causation, Prediction, and Search," 2nd
 * edition, with a modified rule set in step D due to Chris Meek. For the
 * modified rule set, see Chris Meek (1995), "Causal inference and causal
 * explanation with background knowledge."
 *
 * @author Joseph Ramsey.
 */
public class Pc2 implements GraphSearch {

    /**
     * The independence test used for the PC search.
     */
    private IndependenceTest independenceTest;

    /**
     * Forbidden and required edges for the search.
     */
    private Knowledge knowledge = new Knowledge();

    /**
     * Sepset information accumulated in the search.
     */
    private SepsetMap sepset;

    /**
     * The maximum number of nodes conditioned on in the search. The default it 1000.
     */
    private int depth = 1000;

    /**
     * The graph that's constructed during the search.
     */
    private Graph graph;

    /**
     * Elapsed time of the most recent search.
     */
    private long elapsedTime;

    /**
     * True if cycles are to be aggressively prevented. May be expensive
     * for large graphs (but also useful for large graphs).
     */
    private boolean aggressivelyPreventCycles = false;

    /**
     * The logger to use.
     */
    private TetradLogger logger = TetradLogger.getInstance();

    /**
     * In an enumeration of triple types, these are the collider triples.
     */
    private Set<Triple> unshieldedColliders;

    /**
     * In an enumeration of triple types, these are the noncollider triples.
     */
    private Set<Triple> unshieldedNoncolliders;

    /**
     * The number of indepdendence tests in the last search.
     */
    private int numIndependenceTests;

    /**
     * The true graph, for purposes of comparison. Temporary.
     */
    private Graph trueGraph;

    /**
     * The number of false dependence judgements from FAS, judging from the true graph, if set. Temporary.
     */
    private int numFalseDependenceJudgements;

    /**
     * The number of dependence judgements from FAS. Temporary.
     */
    private int numDependenceJudgements;

    /**
     * The list of all unshielded triples.
     */
    private Set<Triple> allTriples;

    /**
     * Set of unshielded colliders from the triple orientation step.
     */
    private Set<Triple> colliderTriples;

    /**
     * Set of unshielded noncolliders from the triple orientation step.
     */
    private Set<Triple> noncolliderTriples;

    /**
     * Set of ambiguous unshielded triples.
     */
    private Set<Triple> ambiguousTriples;

    //=============================CONSTRUCTORS==========================//

    /**
     * Constructs a new PC search using the given independence test as oracle.
     *
     * @param independenceTest The oracle for conditional independence facts.
     *                         This does not make a copy of the independence test, for fear of
     *                         duplicating the data set!
     */
    public Pc2(IndependenceTest independenceTest) {
        if (independenceTest == null) {
            throw new NullPointerException();
        }

        this.independenceTest = independenceTest;

        TetradLoggerConfig config = logger.getTetradLoggerConfigForModel(this.getClass());

        if (config != null) {
            logger.setTetradLoggerConfig(config);
        }
    }

    //==============================PUBLIC METHODS========================//

    /**
     * @return true iff edges will not be added if they would create cycles.
     */
    public boolean isAggressivelyPreventCycles() {
        return this.aggressivelyPreventCycles;
    }

    /**
     * @param aggressivelyPreventCycles Set to true just in case edges will
     *                                  not be addeds if they would create cycles.
     */
    public void setAggressivelyPreventCycles(boolean aggressivelyPreventCycles) {
        this.aggressivelyPreventCycles = aggressivelyPreventCycles;
    }

    /**
     * Returns the independence test being used in the search.
     */
    public IndependenceTest getIndependenceTest() {
        return independenceTest;
    }

    /**
     * Returns the knowledge specification used in the search. Non-null.
     */
    public Knowledge getKnowledge() {
        return knowledge;
    }

    /**
     * Sets the knowledge specification to be used in the search. May not be
     * null.
     */
    public void setKnowledge(Knowledge knowledge) {
        if (knowledge == null) {
            throw new NullPointerException();
        }

        this.knowledge = knowledge;
    }

    /**
     * Returns the sepset map from the most recent search. Non-null after the
     * first call to <code>search()</code>.
     */
    public SepsetMap getSepset() {
        return sepset;
    }

    /**
     * @return the current depth of search--that is, the maximum number of
     *         conditioning nodes for any conditional independence checked.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets the depth of the search--that is, the maximum number of conditioning
     * nodes for any conditional independence checked.
     *
     * @param depth The depth of the search. The default is 1000. A value of
     *              -1 may be used to indicate that the depth should be high (1000). A
     *              value of Integer.MAX_VALUE may not be used, due to a bug on multi-core
     *              machines.
     */
    public void setDepth(int depth) {
        if (depth < -1) {
            throw new IllegalArgumentException("Depth must be -1 or >= 0.");
        }

        if (depth > 1000) {
            throw new IllegalArgumentException("Depth must be <= 1000.");
        }

        this.depth = depth;
    }

    /**
     * Runs PC starting with a complete graph over all nodes of the given
     * conditional independence test, using the given independence test
     * and knowledge and returns
     * the resultant graph. The returned graph will be a pattern if the
     * independence information is consistent with the hypothesis that there
     * are no latent common causes. It may, however, contain cycles or
     * bidirected edges if this assumption is not born out, either due to
     * the actual presence of latent common causes, or due to statistical
     * errors in conditional independence judgments.
     */
    public Graph search() {
        return search(independenceTest.getVariables());
    }

    /**
     * Runs PC starting with a commplete graph over the given list of nodes,
     * using the given independence test and knowledge and returns
     * the resultant graph. The returned graph will be a pattern if the
     * independence information is consistent with the hypothesis that there
     * are no latent common causes. It may, however, contain cycles or
     * bidirected edges if this assumption is not born out, either due to
     * the actual presence of latent common causes, or due to statistical
     * errors in conditional independence judgments.
     * <p/>
     * All of the given nodes must be in the domain of the given conditional
     * independence test.
     */
    public Graph search(List<Node> nodes) {
        this.logger.log("info", "Starting PC algorithm");
        this.logger.log("info", "Independence test = " + getIndependenceTest() + ".");

//        this.logger.log("info", "Variables " + independenceTest.getVariables());

        long startTime = System.currentTimeMillis();

        if (getIndependenceTest() == null) {
            throw new NullPointerException();
        }

        List allNodes = getIndependenceTest().getVariables();
        if (!allNodes.containsAll(nodes)) {
            throw new IllegalArgumentException("All of the given nodes must " +
                    "be in the domain of the independence test provided.");
        }

        graph = new EdgeListGraph(nodes);
        graph.fullyConnect(Endpoint.TAIL);
        List<Edge> edges = graph.getEdges();

//        graph.clear();

        for (Edge edge : edges) {
            ChoiceGenerator generator = new ChoiceGenerator(graph.getNumNodes(), 4);
            int[] choice;

            while ((choice = generator.next()) != null) {
                List<Node> _nodes = GraphUtils.asList(choice, nodes);

                if (_nodes.contains(edge.getNode1())) {
                    continue;
                }

                if (_nodes.contains(edge.getNode2())) {
                    continue;
                }

                if (getIndependenceTest().isIndependent(edge.getNode1(), edge.getNode2(), _nodes)) {
                    System.out.println("Removing " + edge);
                    graph.removeEdge(edge);
                    break;
                }
            }
        }

//        enumerateTriples();
//
//        SearchGraphUtils.pcOrientbk(knowledge, graph, nodes);
//        SearchGraphUtils.orientCollidersUsingSepsets(sepset, knowledge, graph);
//        MeekRules rules = new MeekRules();
//        rules.setAggressivelyPreventCycles(this.aggressivelyPreventCycles);
//        rules.setKnowledge(knowledge);
//        rules.orientImplied(graph);

        orientUnshieldedTriples(knowledge, getIndependenceTest(), depth);
        MeekRules meekRules = new MeekRules();

        meekRules.setAggressivelyPreventCycles(this.aggressivelyPreventCycles);
        meekRules.setKnowledge(knowledge);

        meekRules.orientImplied(graph);
        

        this.logger.log("graph", "\nReturning this graph: " + graph);

        this.elapsedTime = System.currentTimeMillis() - startTime;

        this.logger.info("Elapsed time = " + (elapsedTime) / 1000. + " s");
        this.logger.info("Finishing PC Algorithm.");
        this.logger.flush();

        return graph;
    }

    /**
     * Returns the elapsed time of the search, in milliseconds.
     */
    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Returns the set of unshielded colliders in the graph
     * returned by <code>search()</code>. Non-null after <code>search</code>
     * is called.
     */
    public Set<Triple> getUnshieldedColliders() {
        return unshieldedColliders;
    }

    /**
     * Returns the set of unshielded noncolliders in the graph
     * returned by <code>search()</code>. Non-null after <code>search</code>
     * is called.
     */
    public Set<Triple> getUnshieldedNoncolliders() {
        return unshieldedNoncolliders;
    }

    //===============================PRIVATE METHODS=======================//

    private void enumerateTriples() {
        this.unshieldedColliders = new HashSet<Triple>();
        this.unshieldedNoncolliders = new HashSet<Triple>();

        for (Node y : graph.getNodes()) {
            List<Node> adj = graph.getAdjacentNodes(y);

            if (adj.size() < 2) {
                continue;
            }

            ChoiceGenerator gen = new ChoiceGenerator(adj.size(), 2);
            int[] choice;

            while ((choice = gen.next()) != null) {
                Node x = adj.get(choice[0]);
                Node z = adj.get(choice[1]);

                List<Node> nodes = sepset.get(x, z);

                // Note that checking adj(x, z) does not suffice when knowledge
                // has been specified.
                if (nodes == null) {
                    continue;
                }

                if (nodes.contains(y)) {
                    getUnshieldedNoncolliders().add(new Triple(x, y, z));
                } else {
                    getUnshieldedColliders().add(new Triple(x, y, z));
                }
            }
        }
    }

    public int getNumIndependenceTests() {
        return numIndependenceTests;
    }

    public void setTrueGraph(Graph trueGraph) {
        this.trueGraph = trueGraph;
    }

    public int getNumFalseDependenceJudgements() {
        return numFalseDependenceJudgements;
    }

    public int getNumDependenceJudgements() {
        return numDependenceJudgements;
    }

    private void orientUnshieldedTriples(Knowledge knowledge,
            IndependenceTest test, int depth) {
        TetradLogger.getInstance().info("Starting Collider Orientation:");

//        System.out.println("orientUnshieldedTriples 1");

        this.allTriples = new HashSet<Triple>();
        colliderTriples = new HashSet<Triple>();
        noncolliderTriples = new HashSet<Triple>();
        ambiguousTriples = new HashSet<Triple>();

        for (Node y : graph.getNodes()) {
//            System.out.println("orientUnshieldedTriples 2");

        	List<Node> adjacentNodes = graph.getAdjacentNodes(y);

            if (adjacentNodes.size() < 2) {
                continue;
            }

            ChoiceGenerator cg = new ChoiceGenerator(adjacentNodes.size(), 2);
            int[] combination;

            while ((combination = cg.next()) != null) {
//                System.out.println("orientUnshieldedTriples 3");

                Node x = adjacentNodes.get(combination[0]);
                Node z = adjacentNodes.get(combination[1]);

                if (this.graph.isAdjacentTo(x, z)) {
                    continue;
                }

                getAllTriples().add(new Triple(x, y, z));

//                System.out.println("orientUnshieldedTriples 4");

                SearchGraphUtils.CpcTripleType type = SearchGraphUtils.getCpcTripleType(x, y, z, test, depth, graph);

//                System.out.println("orientUnshieldedTriples 5");

                if (type == SearchGraphUtils.CpcTripleType.COLLIDER) {
//                    System.out.println("orientUnshieldedTriples 6");

                    if (colliderAllowed(x, y, z, knowledge)) {
                        graph.setEndpoint(x, y, Endpoint.ARROW);
                        graph.setEndpoint(z, y, Endpoint.ARROW);

                        TetradLogger.getInstance().log("colliderOriented", SearchLogUtils.colliderOrientedMsg(x, y, z));
                    }

                    colliderTriples.add(new Triple(x, y, z));
                }
                else if (type == SearchGraphUtils.CpcTripleType.AMBIGUOUS) {
//                    System.out.println("orientUnshieldedTriples 7");

                    Triple triple = new Triple(x, y, z);
                    ambiguousTriples.add(triple);
                    graph.setAmbiguous(triple, true);
                }
                else {
//                    System.out.println("orientUnshieldedTriples 8");

                    noncolliderTriples.add(new Triple(x, y, z));
                }
            }
        }

        TetradLogger.getInstance().info("Finishing Collider Orientation.");
    }

    /**
     * Returns the set of all triples found during the most recent
     * run of the algorithm. Non-null after a call to <code>search()</code>.
     */
    public Set<Triple> getAllTriples() {
        return new HashSet<Triple>(allTriples);
    }

    private boolean colliderAllowed(Node x, Node y, Node z, Knowledge knowledge) {
        return isArrowpointAllowed1(x, y, knowledge) &&
                isArrowpointAllowed1(z, y, knowledge);
    }

    private static boolean isArrowpointAllowed1(Node from, Node to,
            Knowledge knowledge) {
        return knowledge == null || !knowledge.edgeRequired(to.toString(), from.toString()) &&
                !knowledge.edgeForbidden(from.toString(), to.toString());
    }


}
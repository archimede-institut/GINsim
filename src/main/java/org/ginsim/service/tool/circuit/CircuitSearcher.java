package org.ginsim.service.tool.circuit;

import org.colomoto.common.task.AbstractTask;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.service.tool.scc.SCCGraphService;

import java.util.ArrayList;
import java.util.List;

/**
 * Find circuits in a graph.
 *
 * @author Aurelien Naldi
 */
public class CircuitSearcher extends AbstractTask<List<CircuitDescrInTree>> {

    private final SCCGraphService connectivity = ServiceManager.getManager().getService(SCCGraphService.class);

    private final RegulatoryGraph graph;
    private final CircuitSearchStoreConfig config;

    public CircuitSearcher(RegulatoryGraph graph) {
        this.graph = graph;
        config = new CircuitSearchStoreConfig(graph.getNodeOrder());
        config.setReady();
    }

    @Override
    public List<CircuitDescrInTree> doGetResult() {
        List<NodeReducedData> sccs = connectivity.getComponents(graph);

        List<CircuitDescrInTree> circuits = getCircuits(sccs);

        if (config.minlen < 2) {
            // also search autoregulations
            if (config.minMust < 2) {
                for (int i = 0; i < graph.getNodeOrderSize(); i++) {
                    RegulatoryNode vertex = (RegulatoryNode) graph
                            .getNodeOrder().get(i);
                    if (config.minMust == 1 && config.t_status[i] == 1 ||
                            config.minMust == 0 && config.t_status[i] == 3) {
                        RegulatoryMultiEdge edge = graph.getEdge(vertex, vertex);
                        if (edge != null) {
                            CircuitDescr circuit = new CircuitDescr();
                            circuit.t_vertex = new RegulatoryNode[] { vertex };
                            circuit.t_me = new RegulatoryMultiEdge[] { edge };
                            circuits.add(new CircuitDescrInTree(circuit, true, CircuitDescr.ALL));
                        }
                    }
                }
            }
        }

        return circuits;
    }

    public List<CircuitDescrInTree> getCircuits(List<NodeReducedData> sccs) {
        List<CircuitDescrInTree> circuits = new ArrayList();
        for (NodeReducedData scc: sccs) {
            searchCircuitInSCC(scc.getContent(), circuits);
        }
        return circuits;
    }

    private void searchCircuitInSCC(List v_cc, List<CircuitDescrInTree> circuits) {

        if (v_cc.size() < 2) {
            return;
        }
        byte[][] t_cc = buildTCC(graph, v_cc); // int[][] giving edges in
        // the current SCC
        byte[] t_visited = new byte[t_cc.length]; // remember visited nodes
        // and their position
        byte[][] t_path = new byte[t_cc.length][3]; // remember the followed
        // path: series of
        // nodes, index of the
        // followed edge and
        // score
        byte[] t_map = new byte[t_cc.length];
        byte cur = 0; // num of the current node
        byte pos = 0; // position of the current node in the current path
        boolean[] t_history = new boolean[t_cc.length]; // avoid refinding
        // cycles not starting
        // at the first gene
        t_visited[0] = 0;
        t_path[0][0] = 0;
        t_path[0][1] = 0;
        int score = 0; // score of the current circuit (to know if it can be
        // accepted)
        t_map[0] = (byte) graph.getNodeOrder().indexOf(v_cc.get(0));
        if (config != null && config.t_status[t_map[0]] == 1) {
            score++;
        }
        for (int j = 1; j < t_visited.length; j++) {
            t_visited[j] = -1;
            t_history[j] = false;
            t_map[j] = (byte) graph.getNodeOrder().indexOf(v_cc.get(j));
            if (config != null && config.t_status[t_map[j]] == 1) {
                score++;
            }
        }
        if (config != null) {
            t_path[0][2] = config.t_status[t_map[0]];
        } else {
            t_path[0][2] = 3;
        }

        // if the SCC doesn't contain enough "must have" node, don't even go on
        if (config != null && score < config.minMust) {
            return;
        }

        // while we can go on
        while (true) {
            // simply follow the path until finding an already visited node
            cur = t_cc[cur][t_path[pos][1]];
            while (t_visited[cur] == -1) {
                // test "must have" and forbiden nodes
                // mark it as visited, add it to the path
                t_visited[cur] = ++pos;
                t_path[pos][0] = cur;
                t_path[pos][1] = 0;
                if (config != null) {
                    t_path[pos][2] = config.t_status[t_map[cur]];
                } else {
                    t_path[pos][2] = 3;
                }
                // go to next node
                cur = t_cc[cur][0];
            }

            // if we are here, we have just found a path!
            // first choose if it is acceptable!
            boolean accepted = true;
            if (config != null) {
                int a = t_visited[cur];
                score = pos - a + 1;
                if (score >= config.minlen && score <= config.maxlen) {
                    score = 0;
                    for (; a <= pos; a++) {
                        if (t_path[a][2] == 1) {
                            score++;
                        } else if (t_path[a][2] == 2) {
                            accepted = false;
                            break;
                        }
                    }

                } else {
                    accepted = false;
                }
            }
            if (accepted && (config == null || score >= config.minMust)
                    && !t_history[cur]) {
                CircuitDescr circuit = new CircuitDescr();
                circuits.add(new CircuitDescrInTree(circuit, true, CircuitDescr.ALL));
                circuit.t_vertex = new RegulatoryNode[pos - t_visited[cur]
                        + 1];
                int p = 0;
                int a = t_visited[cur];
                circuit.t_vertex[p++] = (RegulatoryNode) v_cc
                        .get(t_path[a][0]);
                a++;
                for (; a <= pos; a++) {
                    circuit.t_vertex[p++] = (RegulatoryNode) v_cc
                            .get(t_path[a][0]);
                }

                circuit.t_me = new RegulatoryMultiEdge[circuit.t_vertex.length];
                RegulatoryNode source = circuit.t_vertex[0];
                RegulatoryNode target = null;
                for (int i = 1; i < circuit.t_vertex.length; i++) {
                    target = circuit.t_vertex[i];
                    circuit.t_me[i - 1] = graph.getEdge(source, target);
                    source = target;
                }
                circuit.t_me[circuit.t_me.length - 1] = graph.getEdge(target, circuit.t_vertex[0]);
            }

            // rewind the path and get ready for the next search, stop if
            // nothing more can be done
            boolean goon = false;
            do {
                cur = t_path[pos][0];
                // if the current node has remaining edges
                if (++t_path[pos][1] < t_cc[cur].length) {
                    goon = true;
                    break;
                }
                // else rewind
                t_visited[cur] = -1;
                t_history[cur] = true;
                pos--;
            } while (pos >= 0);
            if (!goon) {
                break;
            }
        }
    }

    private byte[][] buildTCC( Graph graph, List v_cc) {
        byte[][] t_cc = new byte[v_cc.size()][];
        for (byte i = 0; i < t_cc.length; i++) {
            byte[] t = new byte[t_cc.length - 1];
            int last = 0;
            Object source = v_cc.get(i);
            for (byte j = 0; j < t_cc.length; j++) {
                if (i != j && graph.containsEdge(source, v_cc.get(j))) {
                    t[last++] = j;
                }
            }
            if (t.length != last) {
                byte[] t2 = new byte[last];
                for (byte j = 0; j < last; j++) {
                    t2[j] = t[j];
                }
                t = t2;
            }
            t_cc[i] = t;
        }
        return t_cc;
    }

}

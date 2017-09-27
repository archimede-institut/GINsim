package org.ginsim.service.tool.circuit;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

import java.util.List;

/**
 * store configuration of circuit search.
 */
public class CircuitSearchStoreConfig {

	public byte[] t_status;
    public List<RegulatoryNode> v_list;
    public int minlen;
    public int maxlen;
    public int minMust;

    public CircuitSearchStoreConfig(List<RegulatoryNode> v_list) {
        this.v_list = v_list;
        minlen = 1;
        maxlen = v_list.size();
        t_status = new byte[maxlen];
        for (int i = 0; i < t_status.length; i++) {
            t_status[i] = 3;
        }
    }

    public void setReady() {
        minMust = 0;
        for (int i=0 ; i<t_status.length ; i++) {
            if (t_status[i] == 1) {
                minMust++;
            }
        }
    }
}

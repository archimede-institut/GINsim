package org.ginsim.service.tool.circuit;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

import java.util.List;

/**
 * store configuration of circuit search.
 */
public class CircuitSearchStoreConfig {
    /**  */
    public byte[] t_status;
    /**  */
    public byte[][] t_constraint;
    /**  */
    public List v_list;
    /**  */
    public int minlen;
    /**  */
    public int maxlen;
    /**  */
    public int minMust;

    public CircuitSearchStoreConfig(List v_list) {
        v_list = v_list;
        minlen = 1;
        maxlen = v_list.size();
        t_status = new byte[maxlen];
        t_constraint = new byte[maxlen][3];
        for (int i = 0; i < t_status.length; i++) {
            t_status[i] = 3;
            byte max = ((RegulatoryNode) v_list.get(
                    i)).getMaxValue();
            t_constraint[i][0] = 0;
            t_constraint[i][1] = max;
            t_constraint[i][2] = max;
        }
    }

    /**
     * 
     */
    public void setReady() {
        minMust = 0;
        for (int i=0 ; i<t_status.length ; i++) {
            if (t_status[i] == 1) {
                minMust++;
            }
        }
    }
}

package org.ginsim.service.tool.circuit;
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

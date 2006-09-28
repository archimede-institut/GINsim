package fr.univmrs.ibdm.GINsim.circuit;
import java.util.Vector;

/**
 * store configuration of circuit search.
 */
public class GsCircuitSearchStoreConfig {
    /**  */
    public short[] t_status;
    /**  */
    public short[][] t_constraint;
    /**  */
    public Vector v_list;
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

package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Vector;

/**
 * remember, save and restore a simulation parameter.
 */
public class GsSimulationParameters {

    String name;
    int mode;
    boolean pclass_fine;
    int[][] pclass = null;
    int[][] block = null;
    Vector initStates = null;
    String[] genes;
    int[] tailleGenes;
    int maxdepth;
    int maxnodes;

    
    /**
     * clone the object
     */
    public Object clone() {
        GsSimulationParameters other = new GsSimulationParameters();
        other.name = ""+name;
        other.mode = mode;
        if (block != null) {
            other.block = new int[block.length][2];
            for (int i=0 ; i<block.length ; i++) {
                other.block[i][0] = block[i][0];
                other.block[i][1] = block[i][1];
            }
        }
        if (pclass != null) {
            other.pclass = new int[pclass.length][];
            for (int i=0 ; i<pclass.length ; i++) {
                int[] cl = pclass[i];
                int[] c_cl = new int[cl.length];
                for (int j=0 ; j<cl.length ;j++) {
                    c_cl[j] = cl[j];
                }
            }
        }
        return other;
    }
    
    public String toString() {
        return name;
    }
    
    /**
     * a not so simple toString method.
     * It shows the full content of this simu parameters, used to add a usefull comment to the state transition graph.
     * @return a human readable description of the parameter
     */
    public String getDescr() {
        String s;
        switch (mode) {
            case Simulation.SEARCH_ASYNCHRONE_BF:
                s = "asynchronous (BF)\n";
                break;
            case Simulation.SEARCH_ASYNCHRONE_DF:
                s = "asynchronous (DF)\n";
                break;
            case Simulation.SEARCH_BYPRIORITYCLASS:
                s = "by priority class\n";
                for (int i=0 ; i<pclass.length ; i++) {
                    int[] cl = pclass[i];
                    s += "   "+cl[0]+ (cl[1]==0?" sync":" async")+": ";
                    for (int j=2;j<cl.length ; j+=2) {
                        if (j>2) {
                            s += ", ";
                        }
                        s += genes[cl[j]]+(cl[j+1]==0?"":(cl[j+1]==1?"+":"-"));
                    }
                    s += "\n";
                }
                break;
            case Simulation.SEARCH_SYNCHRONE:
                s = "synchronous\n";
                break;
            default:
                s = "";
        }
        if (initStates == null) {
            s += "full graph";
        } else {
            s += "initial states:\n";
            for (int i=0 ; i<initStates.size() ; i++) {
                Vector[] vstate = (Vector[])initStates.get(i);
                for (int j=0 ; j<vstate.length ; j++) {
                    s += "  "+Reg2dynTableModel.getNiceValue(vstate[j], tailleGenes[j]);
                }
                s += "\n";
            }
        }
        
        if (block != null && block[0] != null) {
            String s_block = "";
            for (int i=0 ; i<block[0].length ; i++) {
                if (block[0][i] != -1) {
                    s_block += "   "+genes[i]+": ["+block[0][i]+" ; "+block[1][i]+"]";
                }
            }
            if (!s_block.equals("")) {
                s += "\ntransition blocked:\n"+s_block;
            }
        }
        if (maxdepth != 0) {
            s += "max depth: "+maxdepth+"\n";
        }
        if (maxnodes != 0) {
            s += "max nodes: "+maxnodes+"\n";
        }
        return s;
    }
}

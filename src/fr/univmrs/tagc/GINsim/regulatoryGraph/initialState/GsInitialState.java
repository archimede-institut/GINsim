package fr.univmrs.tagc.GINsim.regulatoryGraph.initialState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.datastore.NamedObject;

public class GsInitialState implements NamedObject {
	String name;
	Map m = new HashMap();
	
	public void setData(String[] t_s, List nodeOrder) {
        for (int i=0 ; i<t_s.length ; i++) {
            GsRegulatoryVertex vertex = null;
            String[] t_val = t_s[i].split(";");
            if (t_val.length > 1) {
                for (int j=0 ; j<nodeOrder.size() ; j++) {
                    if (((GsRegulatoryVertex)nodeOrder.get(j)).getId().equals(t_val[0])) {
                        vertex = (GsRegulatoryVertex)nodeOrder.get(j);
                        break;
                    }
                }
                if (vertex != null) {
                	List v_val = new ArrayList();
                    for (int j=1 ; j<t_val.length ; j++) {
                        try {
                        	int v = Integer.parseInt(t_val[j]);
                            Integer val;
                            if (v>=0 && v < GsEnv.t_integers.length) {
                            	val = GsEnv.t_integers[v];
                            } else {
                            	val = new Integer(v);
                            }
                            if (val.intValue() >= 0 && val.intValue() <= vertex.getMaxValue()) {
                                boolean ok = true;
                                for (int k=0 ; k<v_val.size() ; k++) {
                                    if (v_val.get(k).equals(val)) {
                                        ok = false;
                                        break;
                                    }
                                }
                                if (ok) {
                                    v_val.add(val);
                                }
                            } else {
                                // TODO: report error in file
                            }
                        } catch (NumberFormatException e) {
                            // TODO: report error in file
                        }
                    }
                    if (!v_val.isEmpty() && v_val.size() <= vertex.getMaxValue()) {
                        m.put(vertex, v_val);
                    }
                }
            }
        }
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map getMap() {
		return m;
	}
}

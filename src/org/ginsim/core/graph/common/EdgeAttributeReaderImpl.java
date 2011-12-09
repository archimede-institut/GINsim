package org.ginsim.core.graph.common;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ginsim.common.OptionStore;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.gui.resource.Translator;


/**
 * a generic edgeAttributeReader storing data into a dedicated hashmap
 */
public class EdgeAttributeReaderImpl implements EdgeAttributesReader {

    protected static List<String> v_style = null;
	protected static List<String> v_routing = null;
    protected static List<String> v_patternID = null;
    protected static Map<String, float[]> m_pattern = null;
	
    public static Color default_color = new Color(((Integer)OptionStore.getOption("vs.edgecolor", new Integer(-13395457))).intValue());

    public static void saveOptions() {
        OptionStore.setOption("vs.edgecolor", new Integer(default_color.getRGB()));
    }
    

    
    protected float defaultLineWidth;
    
    static {
        v_style = new ArrayList<String>();
        v_style.add(Translator.getString("STR_styleCurve"));
        v_style.add(Translator.getString("STR_styleStraight"));
        v_routing = new ArrayList<String>();
        v_routing.add(Translator.getString("STR_routingNone"));
        v_routing.add(Translator.getString("STR_routingAuto"));
        v_patternID = new ArrayList<String>();
        m_pattern = new HashMap<String, float[]>();
        String s = Translator.getString("STR_none");
        v_patternID.add(s);
        m_pattern.put(s, null);
        s = Translator.getString("STR_dash");
        v_patternID.add(s);
        m_pattern.put(s, new float[] {10, 4, 3, 5});
    }

    
	public List<String> getRoutingList() {
	    return v_routing;
	}
	/**
	 * @return the list of available draw style
	 */
	public List<String> getStyleList() {
	    return v_style;
	}
	/**
	 * @return the list of available draw style
	 */
	public List<String> getPatternList() {
	    return v_patternID;
	}

    
	
	private final AbstractGraph graph;
    private final Map dataMap;
    
    private float defaultsize = 1;
    private boolean defaultfill = true;
    
    private Edge<?> edge;
    private EdgeVSdata evsd = null;

    private int defaultstyle;
    private int defaultRouting; 
    
    
    /**
     * @param dataMap
     */
    public EdgeAttributeReaderImpl(AbstractGraph backend, Map dataMap) {
    	this.graph = backend;
        this.dataMap = dataMap;
    }
    
    public void setDefaultEdgeColor(Color color) {
    	this.default_color = color;
    }

    public void setDefaultEdgeSize(float s) {
        defaultsize = s;
    }

    public void setDefaultEdgeEndFill(boolean b) {
        defaultfill = b;
    }

    public float getLineWidth() {
        if (evsd == null) {
            return 0;
        }
        return evsd.size;
    }

    public void setLineWidth(float w) {
        if (evsd == null) {
            return;
        }
        evsd.size = w;
    }

    public void setEdge(Object obj) {
    	edge = (Edge)obj;
        evsd = (EdgeVSdata)dataMap.get(obj);
        if (evsd == null && obj instanceof Edge) {
            evsd = (EdgeVSdata)dataMap.get(obj);
        }
        if (evsd == null) {
            evsd = new EdgeVSdata();
            
            evsd.color = default_color;
            evsd.fill = defaultfill;
            evsd.routing = defaultRouting;
            evsd.size = defaultsize;
            evsd.style = defaultstyle;
            
            dataMap.put(obj, evsd);
        }
    }

    public void setLineColor(Color color) {
        if (evsd != null) {
            evsd.color = color;
        }
    }

    public Color getLineColor() {
        if (evsd == null) {
            return null;
        }
        return evsd.color;
    }

    public void refresh() {
    	if (edge != null) {
    		graph.refresh(edge);
    	}
    }

    public int getRouting() {
        if (evsd == null) {
            return 0;
        }
        return evsd.routing;
    }

    public int getStyle() {
        if (evsd == null) {
            return 0;
        }
        return evsd.style;
    }

    public void setRouting(int index) {
        if (evsd == null) {
            return;
        }
        evsd.routing = index;
    }

    public void setStyle(int index) {
        if (evsd == null) {
            return;
        }
        evsd.style = index;
    }

    public void setLineEnd(int index) {
        if (evsd == null) {
            return;
        }
        evsd.end = index;
    }

    public int getLineEnd() {
        if (evsd == null) {
            return 0;
        }
        return evsd.end;
    }

    public List getPoints() {
        if ( evsd == null ) {
            return null;
        }
        return evsd.points;
    }

    public void setPoints(List l) {
        if (evsd == null) {
            return;
        }
        evsd.points = l;
    }

    public void setDefaultStyle(int selectedIndex) {
        defaultstyle = selectedIndex;
    }
    
    class EdgeVSdata {
        protected Color color;
        protected float size;
        protected int end;
        protected boolean fill;
        protected int routing;
        protected int style;
        protected List points;
		protected float[] dash;
    }

    public float getDefaultEdgeSize() {
        return defaultsize;
    }

    public boolean getDefaultEdgeEndFill() {
        return defaultfill;
    }

    public int getDefaultStyle() {
        return defaultstyle;
    }

    @Override
	public void setDash(float[] dashArray) {
		if (evsd == null) {
			return;
		}
		evsd.dash = dashArray;
	}

    public void setDash(String dashID) {
    	setDash((float[])m_pattern.get(dashID));
    }

	
    @Override
	public float[] getDash() {
		if (evsd == null) {
			return null;
		}
		return evsd.dash;
	}
	
    public String getDashID() {
    	Iterator it = m_pattern.entrySet().iterator();
    	float[] dash = getDash();
    	while (it.hasNext()) {
    		Entry e = (Entry)it.next();
    		if (e.getValue() == dash) {
    			return (String)e.getKey();
    		}
    	}
    	return null;
    }
	public float[] getPattern(int i) {
		return (float[])m_pattern.get(v_patternID.get(i));
	}
	@Override
	public Color getDefaultEdgeColor() {
		return default_color;
	}

	@Override
	public void copyFrom(EdgeAttributesReader fereader) {
		setLineColor(fereader.getLineColor());
		setStyle(fereader.getStyle());
		setRouting(fereader.getRouting());
		setPoints(fereader.getPoints());
		setLineEnd(fereader.getLineEnd());
		setLineWidth(fereader.getLineWidth());
		setDash(fereader.getDash());
 }
 
	@Override
	public void copyDefaultFrom(EdgeAttributesReader fvreader) {
		setDefaultEdgeSize(fvreader.getDefaultEdgeSize());
		setDefaultEdgeEndFill(fvreader.getDefaultEdgeEndFill());
		setDefaultStyle(fvreader.getDefaultStyle());
	}

	
}

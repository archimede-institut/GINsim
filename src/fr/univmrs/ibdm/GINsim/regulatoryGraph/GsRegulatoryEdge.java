package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import fr.univmrs.ibdm.GINsim.data.GsAnnotation;

/**
 * a regulatory edge, ie an interaction, it should not be used directly, but hidden in a GsRegulatoryMultiEdge
 */
public class GsRegulatoryEdge {

    /** array of sign's names */
    static public final String[] SIGN = {"positive","negative","unknown"};
    /** array of sign's short names */
    static public final String[] SIGN_SHORT = {"+","-","?"};
    /** a positive edge */
    static public final short SIGN_POSITIVE = 0;
    /** a negative edge */
    static public final short SIGN_NEGATIVE = 1;
    /** an unknown edge */
    static public final short SIGN_UNKNOWN = 2;

    private short min = 1;
    private short max = -1;
    private short sign;
    
    private GsAnnotation gsAnnotation = new GsAnnotation();
    
    /**
     * @param sign
     */
    public GsRegulatoryEdge(int sign) {
        this.sign = (short)sign;
    }

    /**
     * 
     */
    public GsRegulatoryEdge() {
        this(0);
    }

    /**
     * @param minvalue
     * @param maxvalue
     * @param sign
     */
    public GsRegulatoryEdge(short minvalue, short maxvalue, short sign) {
        min = minvalue;
        max = maxvalue;
        this.sign = sign;
    }

    /**
     * @return the upperlimit of the source gene for which this edge is active.
     */
    public short getMax() {
        return max;
    }
    /**
     * change the max value.
     * @param max the new max value.
     * @see #getMax()
     */
    public void setMax(short max) {
        if (max != -1 && max < 1) {
            return;
        }
        this.max = max;
        if (max != -1 && max < min) {
            min = max;
        }
    }
    /**
     * @return the lesser limit of the source gene for which this edge is active.
     */
    public short getMin() {
        return min;
    }
    /**
     * change the min value.
     * @param min the new min value.
     * @see #getMin()
     */
    public void setMin(short min) {
        if (min < 1) {
            return;
        }
        this.min = min;
        if (max != -1 && min > max) {
            this.max = min;
        }
    }
    /**
     * @return the annotation attached to this edge.
     */
    public GsAnnotation getGsAnnotation() {
        return gsAnnotation;
    }
    /**
     * change this edge's sign.
     * @param sign
     */
    public void setSign(short sign) {
        this.sign = sign;
    }

    /**
     * @return the sign of this edge
     */
    public short getSign() {
        return sign;
    }
    
    public String toString() {
        if (max == -1) {
            return "["+min+",Max] ; "+SIGN[sign];
        }
        return "["+min+","+max+"] ; "+SIGN[sign];
    }

	/**
	 * if the max value of the source vertex change, we may want to propagate it
	 * to avoid building malformed regulatory graphs.
	 * 
	 * @param vertex the source vertex.
	 */
	public void applyNewMaxValue(GsRegulatoryVertex vertex) {
		short newmax = vertex.getMaxValue();
		if (max > newmax) {
			max = newmax;
		}
		if (min > newmax) {
			min = newmax;
		}
	}
    protected void setGsAnnotation(GsAnnotation gsAnnotation) {
        this.gsAnnotation = gsAnnotation;
    }
}

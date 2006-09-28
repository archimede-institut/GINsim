//package fr.univmrs.ibdm.GINsim.jgraph;
//
//import java.util.Vector;
//
//import org.jgraph.graph.AttributeMap;
//import org.jgraph.graph.DefaultGraphCell;
//import org.jgraph.graph.GraphConstants;
//
///**
// * trying to get back graphic attributes editing
// */
//public class GsJgraphVertex {
//
//    static private Vector v_lineEndDescr = new Vector();
//    static private Vector v_lineEndHelper = new Vector();
//    static private Vector v_vertexShapeDescr = new Vector();
//    static private Vector v_vertexShapeHelper = new Vector();
//    
//    public static void addLineEnd(String descr, Object helper) {
//        v_lineEndDescr.add(descr);
//        v_lineEndHelper.add(helper);
//    }
//    
//    public static void addVertexShape(String descr, Object helper) {
//        v_vertexShapeDescr.add(descr);
//        v_vertexShapeHelper.add(helper);
//    }
//    
//    public static Vector getVertexShape() {
//        return v_vertexShapeDescr;
//    }
//    
//    public static Vector getLineEnd() {
//        return v_lineEndDescr;
//    }
//    
//    DefaultGraphCell cell;
//    AttributeMap map = null;
//
//    public int getBorderStyle() {
//        GraphConstants.getBorder(map);
//        
//        return 0;
//    }
//    
//}

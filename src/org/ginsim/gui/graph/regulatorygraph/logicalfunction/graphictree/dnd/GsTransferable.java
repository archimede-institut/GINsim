package org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Arrays;
import java.util.List;

import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeElement;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeExpression;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeParam;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeString;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeValue;


public class GsTransferable implements Transferable {
  public static final DataFlavor FUNCTION_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Functions");
  public static final DataFlavor VALUE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Values");
  public static final DataFlavor PARAM_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Parameters");
  public static final DataFlavor MIXED_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Mixed elements");
  public static final DataFlavor PLAIN_TEXT_FLAVOR = DataFlavor.getTextPlainUnicodeFlavor();
  public static final DataFlavor[] dataFlavors = { FUNCTION_FLAVOR, VALUE_FLAVOR, PARAM_FLAVOR, MIXED_FLAVOR, PLAIN_TEXT_FLAVOR };
  public static final List dataFlavorsList = Arrays.asList(dataFlavors);

  private TreeElement[] nodes;
  private DataFlavor currentFlavor;
  private TreeElement pParent;
  private boolean oneValue;

  public GsTransferable(TreeElement[] el) {
    super();
    nodes = el;
    pParent = null;
    oneValue = true;
    int value = -1;
  	
    for (int i = 0; i < nodes.length; i++)
    	if (oneValue && (nodes[i] instanceof TreeParam)) 
    		if (value == -1) 
    			value = ((TreeValue)nodes[i].getParent().getParent()).getValue();
    		else if (value != ((TreeValue)nodes[i].getParent().getParent()).getValue())
    			oneValue = false;
    
    if (nodes[0] instanceof TreeExpression)
    	currentFlavor = FUNCTION_FLAVOR;
		else if (nodes[0] instanceof TreeValue)
			currentFlavor = VALUE_FLAVOR;
		else if (nodes[0] instanceof TreeParam)
			currentFlavor = PARAM_FLAVOR;
		else if (nodes[0] instanceof TreeString)
			currentFlavor = PLAIN_TEXT_FLAVOR;
  	for (int i = 1; i < nodes.length; i++)
   		if (nodes[i] instanceof TreeExpression && currentFlavor != FUNCTION_FLAVOR) {
	      currentFlavor = MIXED_FLAVOR;
	      break;
	    }
	    else if (nodes[i] instanceof TreeValue && currentFlavor != VALUE_FLAVOR) {
	      currentFlavor = MIXED_FLAVOR;
	      break;
	    }
	    else if (nodes[i] instanceof TreeParam && currentFlavor != PARAM_FLAVOR) {
	      currentFlavor = MIXED_FLAVOR;
	      break;
	    }
	    else if (nodes[i] instanceof TreeString && currentFlavor != PLAIN_TEXT_FLAVOR) {
	      currentFlavor = MIXED_FLAVOR;
	      break;
	    }
  }
  public boolean isOneValue() {
  	return oneValue;
  }
  public boolean containsValue() {
  	for (int i = 0; i < nodes.length; i++)
  		if (nodes[i] instanceof TreeValue) return true;
  	return false;
  }
  public boolean containsFunction() {
  	for (int i = 0; i < nodes.length; i++)
  		if (nodes[i] instanceof TreeExpression) return true;
  	return false;
  }
  public boolean containsRoot() {
  	for (int i = 0; i < nodes.length; i++)
  		if (nodes[i] instanceof TreeString) return true;
  	return false;
  }
  public boolean containsParameter() {
  	for (int i = 0; i < nodes.length; i++)
  		if (nodes[i] instanceof TreeParam) return true;
  	return false;
  }
  public DataFlavor getCurrentFlavor() {
    return currentFlavor;
  }
  public TreeElement[] getNodes() {
    return nodes;
  }
  public TreeElement getPParent() {
    return pParent;
  }
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
    if (flavor == PLAIN_TEXT_FLAVOR) {
      StringBuffer text = new StringBuffer();
      for (int i = 0; i < nodes.length; i++) text.append(nodes[i].toString() + "\n");
      return text.toString();
    }
    else if (dataFlavorsList.contains(flavor))
    	return nodes;
    else
    	throw new UnsupportedFlavorException(flavor);
  }
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return dataFlavorsList.contains(flavor);
  }
  public DataFlavor[] getTransferDataFlavors() {
    return dataFlavors;
  }
}

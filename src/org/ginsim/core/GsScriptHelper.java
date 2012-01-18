package org.ginsim.core;

import java.awt.Color;
import java.io.File;

import org.ginsim.common.OptionStore;
import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeAttributeReaderImpl;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.service.ServiceManagerProxy;

/**
 * A helper when running GINsim without a GUI.
 * It provides a convenient API for common actions:
 * - open and save graphs
 * - run services
 * - write reports
 * 
 * @author Aurelien Naldi
 */
public class GsScriptHelper {

	private static GsScriptHelper instance = null;
	private static boolean isInit = false;
	
	private static void initOptionStore() {
		if (isInit) {
			return;
		}
		isInit = true;
		try {
			OptionStore.init( GsScriptHelper.class.getPackage().getName());
	    	OptionStore.getOption( EdgeAttributeReaderImpl.EDGE_COLOR, new Integer(-13395457));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_BG, new Integer(-26368));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_FG, new Integer(Color.WHITE.getRGB()));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_HEIGHT, new Integer(30));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_WIDTH, new Integer(55));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_SHAPE, NodeShape.RECTANGLE.name());
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_BORDER, NodeBorder.SIMPLE.name());
		} catch (Exception e) {
			LogManager.error("Could not init the Option Store");
		}
	}

	public static GsScriptHelper getInstance() {
		if (instance == null) {
			instance = new GsScriptHelper();
		}
		return instance;
	}

	public final ServiceManagerProxy services = new ServiceManagerProxy();
	
	private GsScriptHelper() {
		initOptionStore();
	}
	
	public Graph<?, ?> open(String filename) throws GsException {
		File file = new File(filename);
		return GraphManager.getInstance().open( file);
	}
	
}

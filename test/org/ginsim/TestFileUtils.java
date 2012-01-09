package org.ginsim;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.io.File;

import org.ginsim.common.OptionStore;
import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.common.NodeAttributeReaderImpl;
import org.ginsim.core.graph.regulatorygraph.BasicRegulatoryGraphTest;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;

public class TestFileUtils {

	// The directory where are stored the predefined files for the tests
	public static File testFileDirectory = new File( ".", "testFiles");
	private static boolean isInit = false;
	
	
	public static void initOptionStore() {
		if (isInit) {
			return;
		}
		isInit = true;
		try {
			OptionStore.init( BasicRegulatoryGraphTest.class.getPackage().getName());
	    	OptionStore.getOption( EdgeAttributeReaderImpl.EDGE_COLOR, new Integer(-13395457));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_BG, new Integer(-26368));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_FG, new Integer(Color.WHITE.getRGB()));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_HEIGHT, new Integer(30));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_WIDTH, new Integer(55));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_SHAPE, NodeShape.RECTANGLE.name());
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_BORDER, NodeBorder.SIMPLE.name());
		} catch (Exception e) {
			fail( "Initialisation of OptionStore failed : " + e);
		}

	}
	
	/**
	 * Returns the directory where test files are stored
	 * 
	 * @return the directory where test files are stored
	 */
	public static File getTestFileDir(){
		
		return testFileDirectory;
	}
	
	/**
	 * Returns a directory under the main test file directory with the given name
	 * 
	 * @param module the name of the sub-directory
	 * @return a directory under the main test file directory with the given name
	 */
	public static File getTestFileDirectory( String module){
		
		File file = new File( testFileDirectory, module);
		
		if( !file.exists()){
			file.mkdir();
		}
		
		return file;
	}
	
	/**
	 * Returns a temporary directory under the dedicated directory of the given module
	 * 
	 * @param module the name of the sub-directory
	 * @return a directory under the main test file directory with the given name
	 */
	public static File getTempTestFileDirectory( String module){
		File f = new File( getTestFileDirectory( module), "tmp");
		if (!f.exists()) {
			f.mkdirs();
		}
		return f;
	}
	
	/**
	 * Remove and rebuild the temporary directory under the dedicated directory of the given module
	 * 
	 * @param module the name of the sub-directory
	 */
	public static void cleanTempTestFileDirectory( String module){
		
		File file = getTempTestFileDirectory( module);
		if( file.exists()){
			IOUtils.deleteDirectory( file);
		}
		file.mkdir();
	}

	/**
	 * Load a graph from the given file
	 * 
	 * @param file the file to load
	 */
	public static RegulatoryGraph loadGraph( File file){
		initOptionStore();
		try{
			RegulatoryGraph graph = (RegulatoryGraph) GraphManager.getInstance().open( file);
			
			assertNotNull( "Load graph : graph is null", graph);
			return graph;
		}
		catch ( GsException gse) {
			fail( "Save graph : Exception catched : " + gse);
			return null;
		}
	}


}

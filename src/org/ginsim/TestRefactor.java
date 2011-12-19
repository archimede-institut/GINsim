package org.ginsim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.OptionStore;
import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.common.utils.Translator;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.resource.ImageLoader;
import org.ginsim.gui.shell.AboutDialog;


/**
 * Simple, stupid launcher to test the ongoing refactoring
 * 
 * @author Aurelien Naldi
 */
public class TestRefactor {

	/**
	 * @param args
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		detectGSDir();
		
        List<String> open = new ArrayList<String>();

        /*
         * parse arguments:
         *  - open files
         *  - script mode
         *  - help
         */
        for (int i = 0; i < args.length; i++) {
        	if (args[i].equals("-s")) {
                if (args.length == i+1) {
                    System.out.println("Script mode requires a filename argument");
                    return;
                }
                
                i++;
                GINsimPy.runJython(args[i]);
                return;
            }
            
        	if (args[i].startsWith("-")) {
            	if (!args[i].equals("-h")) {
                    System.out.println("Unknown option: "+args[i]);
            	}
                System.out.println("available options:");
                System.out.println("\t-s <file>: run \"script\" from <file> [TODO: script arguments]");
                System.out.println("\t-h: display this message");
                return;
            }
        	
            File f = new File(args[i]);
            if (f.exists()) {
                open.add(args[i]);
            } else {
            	LogManager.error("Required file does not exist: " + f);
            }
        }
		
		initGUI();
		Graph graph = null;
		if (open.size() > 0) {
			for (String filename: open) {
				try {
					graph = GraphManager.getInstance().open(filename);
					GUIManager.getInstance().newFrame( graph);

				} catch (GsException e) {
					LogManager.error(e);
				}
			}
		}
		if( graph == null){
			graph = GUIManager.getInstance().newFrame();
		}
		// Test whatToDoWithGraph frame
//		new WhatToDoWithGraph( graph);
		// Test of Notifications
//		String[] options_names = new String[]{ "STR_OK", "STR_cancel"};
//		NotificationResolution resolution = new NotificationResolution( options_names);
//		NotificationManager.publishResolvableError( graph, "STR_unableToOpen", graph, null, resolution);
//		NotificationManager.publishWarning( graph, "STR_unableToSave");
//		NotificationManager.publishError( graph, "STR_wantToStop_title");
		
	}

	/**
	 * detect the current directory
	 * It will be needed for plugins, dynamic classpath...
	 */
	private static void detectGSDir() {
		String basedir = System.getProperty("user.dir");
		
		Class<?> cl = TestRefactor.class;
		String clname = cl.getName().replace(".",	"/") + ".class";
		String path = cl.getClassLoader().getResource(clname).toString();
		if (path.startsWith("file:")) {
			basedir = path.substring(5,  path.length() - clname.length());
		} else if (path.startsWith("jar:file:")) {
			File jar = new File(path.substring(9,  path.length() - clname.length() - 2));
			basedir = jar.getParent();
		}
		
		try{
			LogManager.init( basedir, 2, true);
		}
		catch( IOException io){
			System.out.println("TestRefactor.main() : Unable to initialize the debugger");
		}
	}
	
	/**
	 * Init method for GINsim GUI.
	 * This method will only load all required resources, it will not create the first window.
	 */
	private static void initGUI() {
		Translator.pushBundle("org.ginsim.gui.resource.messages");
		ImageLoader.pushSearchPath("/org/ginsim/gui/resource/icon");
		ImageLoader.pushSearchPath("/org/ginsim/gui/resource/icon/action");
		AboutDialog.setDOAPFile("/org/ginsim/gui/resource/GINsim-about.rdf");
		try {
			OptionStore.init(TestRefactor.class.getPackage().getName());
		} catch (Exception e) {
			GUIMessageUtils.openErrorDialog(e, null);
		}
		GUIManager.initializeOptions();
		
	}

}

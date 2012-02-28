package org.ginsim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.OSXAdapter;
import org.ginsim.common.OptionStore;
import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.common.utils.Translator;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.resource.ImageLoader;
import org.ginsim.gui.shell.AboutDialog;


/**
 * Simple launcher: parse command line arguments, show some help, open files or run the script mode accordingly.
 * 
 * @author Aurelien Naldi
 */
public class Launcher {

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
        		ScriptLauncher script = new ScriptLauncher();
                if (args.length == i+1) {
                	script.help();
                    return;
                }
                
                i++;
                String[] pyargs = new String[args.length-(i+1)];
                System.arraycopy(args, i+1, pyargs, 0, pyargs.length);
                script.run(args[i], pyargs);
                
                // force exit as jython seems to often delay it
                System.exit(0);
                return;
            }
            
        	if (args[i].startsWith("-")) {
            	if (!args[i].equals("-h")) {
                    System.out.println("Unknown option: "+args[i]);
            	}
                System.out.println("Available options:");
                System.out.println("\t<file>: open <file> on startup.");
                System.out.println("\t-s: display the script help message.");
                System.out.println("\t-s <file>: run \"script\" from <file>. Extra arguments are script arguments.");
                System.out.println("\t-h:  display this message.");
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
		GUIManager.getInstance().startup( open);
	}

	/**
	 * detect the current directory
	 * It will be needed for plugins, dynamic classpath...
	 */
	private static void detectGSDir() {
		String basedir = System.getProperty("user.dir");
		
		Class<?> cl = Launcher.class;
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
			OptionStore.init(Launcher.class.getPackage().getName());
		} catch (Exception e) {
			GUIMessageUtils.openErrorDialog(e, null);
		}
		registerForMacOSXEvents();
		
	}
	
    public static void registerForMacOSXEvents() {
        if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            try {
            	OSXCallBack osxCallBack = new OSXCallBack();
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setQuitHandler(osxCallBack, osxCallBack.getClass().getDeclaredMethod("quit", (Class[])null));
                //OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[])null));
                //OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
                OSXAdapter.setFileHandler(osxCallBack, osxCallBack.getClass().getDeclaredMethod("loadGINMLfile", new Class[] { String.class }));
            } catch (Exception e) {
                System.err.println("Error while loading the OSXAdapter:");
                e.printStackTrace();
            }
        }
    }
	
 }
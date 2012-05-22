package org.ginsim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.application.CurrentOS;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.OSXAdapter;
import org.ginsim.common.application.OptionStore;
import org.ginsim.common.application.Translator;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.commongui.utils.ImageLoader;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.shell.AboutDialog;


/**
 * Simple launcher: parse command line arguments, show some help, open files or run the script mode accordingly.
 * 
 * @author Aurelien Naldi
 */
public class Launcher {

	/**
	 * True if the developers extras (toolkit menu, under-developement...) should be enabled
	 */
	public static boolean developer_mode = false;
	
	/**
	 * The main "main" function, launching GINsin GUI or script handler.
	 * 
	 * @param args
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		init();
		
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
            } else if (args[i].equals("-n")) {
        		open.add(null);
        		continue;
        	} else if (args[i].equals("--dev")) {
        		developer_mode = true;
        		continue;
            } else if (args[i].startsWith("-")) {
            	if (!args[i].equals("-h")) {
                    System.out.println("Unknown option: "+args[i]);
            	}
                System.out.println("Available options:");
                System.out.println("\t<file>: open <file> on startup.");
                System.out.println("\t-n: start with a new regulatory graph.");
                System.out.println("\t-s: display the script help message.");
                System.out.println("\t-s <file>: run \"script\" from <file>. Extra arguments are script arguments.");
                System.out.println("\t-h:  display this message.");
                System.out.println("\t--dev : enable the developer's options.");
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
	 * Initialise GINsim core: detect the current directory, load services
	 */
	private static void init() {
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
		
		ServiceManager.getManager();
	}
	
	/**
	 * Initialisation method for GINsim GUI.
	 * This method will only load all required resources, it will not create the first window.
	 */
	private static void initGUI() {
		Translator.pushBundle("org.ginsim.messages");
		ImageLoader.pushSearchPath("/org/ginsim/icon");
		ImageLoader.pushSearchPath("/org/ginsim/icon/action");
		AboutDialog.setDOAPFile("/GINsim-about.rdf");
		try {
			OptionStore.init(Launcher.class.getPackage().getName());
		} catch (Exception e) {
			GUIMessageUtils.openErrorDialog(e, null);
		}
		
    	// register OSX callback if appropriate
        if (CurrentOS.os == CurrentOS.SYS_MACOSX) {
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
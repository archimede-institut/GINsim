package org.ginsim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.utils.log.LogManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.resource.ImageLoader;
import org.ginsim.gui.resource.Translator;
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
         * parse args:
         *  - run in script mode
         *  - set ginsim dir
         *  - choose locale
         *  - give some help
         */
        for (int i = 0; i < args.length; i++) {
        	if (args[i].equals("--lang")) {
                if (args.length > i) {
                    i++;
                    String lang = args[i];
                    if ("C".equals(lang)) {
                        Translator.setLocale(Locale.ENGLISH);
                    } else if ("FR".equals(lang)) {
                        Translator.setLocale(Locale.FRENCH);
                    }
                } else {
                    System.out.println(args[i]+": missing argument");
                    return;
                }
            } else if (args[i].equals("--run")) {
                if (args.length == i+1) {
                    System.out.println("Script mode requires a filename argument");
                    return;
                }
                
                i++;
                GINsimPy.runJython(args[i]);
                return;
            }
            else if (args[i].startsWith("-")) {
            	if (!args[i].equals("--help")) {
                    System.out.println("Unknown option: "+args[i]);
            	}
                System.out.println("avaible options");
                System.out.println("\t--lang <lang>: choose the lang (avaible: C, FR)");
                System.out.println("\t--run <file>: run \"script\" from <file> [TODO: pass other args to the script]");
                System.out.println("\t--ginsimdir <dir>: define GINsim install dir");
                System.out.println("\t--help: display this message");
                return;
            } else {
                File f = new File(args[i]);
                if (f.exists()) {
                    open.add(args[i]);
                } else {
                	LogManager.error("Required file does not exist: " + f);
                }
            }
        }
		
		initGUI();
		if (open.size() == 0) {
			GUIManager.getInstance().newFrame();
		} else {
			for (String filename: open) {
				try {
					Graph<?,?> g = GraphManager.getInstance().open(filename);
					GUIManager.getInstance().newFrame(g);
				} catch (GsException e) {
					LogManager.error(e);
				}
			}
		}
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
		Translator.pushBundle("org.ginsim.gui.resources.messages");
		ImageLoader.pushSearchPath("/org/ginsim/gui/resources/icons");
		AboutDialog.setDOAPFile("/org/ginsim/gui/resources/GINsim-about.rdf");
	}

}

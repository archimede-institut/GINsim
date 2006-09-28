package fr.univmrs.ibdm.GINsim.global;

import java.io.File;
import java.util.Locale;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.graph.GsGinsimGraphDescriptor;
import fr.univmrs.ibdm.GINsim.gui.GsOpenAction;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;

/**
 * this class is used to run GINsim: parse args and so on
 */
public class GsMain {

	/**
	 * @param args try to run it with --help for more info
	 */
	public static void main(String[] args) {

		String gsdir = ".";
		boolean startui = true;
		Vector commands = new Vector(0);
        Vector open = new Vector(0);
		
	   /* 
	    * parse args:
		* 	- run without GUI
		* 	- set ginsim dir
		*   - choose locale
		* 	- give some help
		*/ 
		for (int i=0 ; i<args.length ; i++) {
			// help
			if (args[i].equals("--help")) {
				System.out.println("avaible options");
				System.out.println("\t--lang <lang>: choose the lang (avaible: C, FR)");
				System.out.println("\t--noui: don't run UI [completly useless until \"run\" does something]");
				System.out.println("\t--run <file>: run \"script\" from <file> [not really supported yet]");
				System.out.println("\t--ginsimdir <dir>: define GINsim install dir");
				System.out.println("\t--help: display this message");
				return;
			} else if (args[i].equals("--ginsimdir")) {
				if (args.length > i) {
					i++;
					gsdir = args[i];
				} else {
                    System.out.println("\"--ginsimdir\": missing argument");
				    return;
                }
			} else if (args[i].equals("--lang")) {
				if (args.length > i) {
					i++;
					String lang = args[i];
					if ("C".equals(lang)) {
					    Translator.setLocale(Locale.ENGLISH);
					} else if ("FR".equals(lang)) {
					    Translator.setLocale(Locale.FRENCH);
					}
                } else {
                    System.out.println("\"--lang\": missing argument");
                    return;
                }
			} else if (args[i].equals("--noui")) {
				startui = false;
			} else if (args[i].equals("--run")) {
				if (args.length > i) {
					i++;
					commands.add(args[i]);
				}
			} else {
                if (args[i].startsWith("-")) {
                    System.out.println("unknown option, try --help for a list");
                } else {
                    File f = new File(args[i]);
                    if (f.exists()) {
                        open.add(args[i]);
                    }
                }
			}
		}
		GsEnv.setGinsimDir(gsdir);
		
		for (int i=0 ; i<commands.size() ; i++) {
			System.out.println("run: " + commands.get(i) + "[not yet done]");
		}
		
		if(startui) {
            if (open.size() > 0) {
                for (int i=0 ; i<open.size() ; i++) {
                    GsOpenAction.open(GsGinsimGraphDescriptor.getInstance(), GsEnv.newMainFrame(), null, (String)open.get(i));
                }
            } else {
                GsEnv.newMainFrame();
            }
		}
	}
}

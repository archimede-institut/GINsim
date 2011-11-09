package fr.univmrs.tagc.GINsim.global;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.ginsim.graph.Graph;
import org.python.util.PythonInterpreter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.tagc.GINsim.graph.GsGinsimGraphDescriptor;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.managerresources.ImageLoader;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.AboutDialog;
import fr.univmrs.tagc.common.xml.XMLHelper;

/**
 * this class is used to run GINsim: parse args and so on
 */
public class GsMain {

    public static void loadCore() {
        // update classpath
        // hacky for now, dynamic stuff later on
        String[] dirs = {"/cobelix/naldi/Bureau/tb"};
        updateClassPath(dirs);

        try {
            GsEnv.readConfig("/fr/univmrs/tagc/GINsim/resources/GINsim-config.xml");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    /**
     * @param args
     * try to run it with --help for more info
     */
    public static void main(String[] args) {
    	loadCore();

        String gsdir = ".";
        Vector open = new Vector(0);

        /*
         * parse args:
         *  - run in script mode
         *  - set ginsim dir
         *  - choose locale
         *  - give some help
         */
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--ginsimdir")) {
                if (args.length > i) {
                    i++;
                    gsdir = args[i];
                } else {
                    System.out.println(args[i]+": missing argument");
                    return;
                }
            } else if (args[i].equals("--addconfig")) {
                if (args.length > i) {
                    i++;
                    try {
						GsEnv.readConfig(args[i]);
					} catch (Exception e) {
						e.printStackTrace();
					}
                } else {
                    System.out.println(args[i]+": missing argument");
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
                    System.out.println(args[i]+": missing argument");
                    return;
                }
            } else if (args[i].equals("--run")) {
                if (args.length == i+1) {
                    System.out.println("Script mode requires a filename argument");
                    return;
                }
                
                i++;
                
                File f = new File(args[i]);
                if (!f.exists()) {
                    System.out.println("No such script: "+args[i]);
                	return;
                }

                GsEnv.setGinsimDir(gsdir);
                Tools.HASGUI = false;

                PythonInterpreter pi = new PythonInterpreter();
                
                
				try {
	                InputStream is = Tools.getStreamForPath("/fr/univmrs/tagc/GINsim/jython/GINsim.py");
	                pi.execfile(is, "GS.py");
	                is.close();
	                pi.exec("GINsim = GINsim()");
				} catch (Exception e) {
	                System.out.println("Script mode failed");
	                return;
				}

				// TODO: pass remaining arguments
				
                pi.execfile(args[i]);
                return;
            }
            else {
                if (args[i].startsWith("-")) {
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
                    }
                }
            }
        }
        GsEnv.setGinsimDir(gsdir);

        if (open.size() > 0) {
            for (int i = 0; i < open.size(); i++) {
                GsOpenAction.open(GsGinsimGraphDescriptor.getInstance(),
                        GsEnv.newMainFrame(), null, (String) open.get(i));
            }
        } else {
        	File autoexec = new File("autoexec.zginml");
        	if (autoexec.exists()) {
        		GsOpenAction.open(GsGinsimGraphDescriptor.getInstance(),
                        GsEnv.newMainFrame(), null, "autoexec.zginml");
        		return;
        	}
        	autoexec = new File("autoexec.ginml");
        	if (autoexec.exists()) {
        		GsOpenAction.open(GsGinsimGraphDescriptor.getInstance(),
                        GsEnv.newMainFrame(), null, "autoexec.ginml");
        		return;
        	}                
        	GsEnv.newMainFrame();
        }
    }


    private static List l = new ArrayList();
    URLClassLoader cloader = new URLClassLoader(new URL[] {}, ClassLoader.getSystemClassLoader());
    public static ClassLoader getClassLoader() {
        URL[] t = new URL[l.size()];
        for (int i=0 ; i<t.length ; i++) {
            File f = new File((String)l.get(i));
            if (!(f.exists() && f.canRead())) {
                continue;
            }
            try {
                t[i] = f.toURI().toURL();
            } catch (MalformedURLException e) {}
        }
        return new URLClassLoader(t);
    }

    public static void updateClassPath(String[] files) {
        for (int i=0 ; i<files.length ; i++) {
            File f = new File(files[i]);
            if (!(f.exists() && f.canRead())) {
                continue;
            }
            if (f.isDirectory()) {
                String[] content = f.list();
                for (int j=0 ; j<content.length ; j++) {
                    if (content[j].endsWith(".jar")) {
                        l.add(f + File.pathSeparator + content[j]);
                    }
                }
            } else {
                l.add(files[i]);
            }
        }
    }
}

/**
 * This class reads GINsim's generic config file, loads plugins and so on.
 */
class ReadConfig extends XMLHelper {

	public String getFallBackDTD() {
		return null;
	}

	public Graph getGraph() {
		return null;
	}

	ClassLoader cloader = GsMain.getClassLoader();

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("plugin".equals(qName)) {
			String s = attributes.getValue("load");
			if (null == s || "true".equals(s)) {
				s = attributes.getValue("mainClass");
				try {
					Class cl = Class.forName(s, true, cloader);
					GsPlugin plugin = (GsPlugin)cl.newInstance();
					plugin.registerPlugin();
				} catch (Throwable e) {
				    if (!"fr.univmrs.tagc.GINsim.gui.tbclient.GsTBPlugin".equals(s)) {
    					System.err.println("unable to load plugin from class: "+s);
    				}
				}
			}
		} else if ("graph".equals(qName)) {
			String s = attributes.getValue("load");
			if ("true".equals(s)) {
				s = attributes.getValue("mainClass");
				try {
					Class cl = Class.forName(s, true, cloader);
					GsEnv.addGraphType((GsGraphDescriptor)cl.newInstance());
				} catch (Exception e) {
					System.err.println("unable to add graphType from class: "+s);
				}
			}
		} else if ("imagePath".equals(qName)) {
			ImageLoader.pushSearchPath(attributes.getValue("path"));
		} else if ("messagesPath".equals(qName)) {
			Translator.pushBundle(attributes.getValue("path"));
		} else if ("doap".equals(qName)) {
                    AboutDialog.setDOAPFile(attributes.getValue("path"));
		}
	}

}

package fr.univmrs.ibdm.GINsim.global;

import java.io.File;
import java.util.Locale;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.ibdm.GINsim.graph.GsGinsimGraphDescriptor;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.ibdm.GINsim.gui.GsOpenAction;
import fr.univmrs.ibdm.GINsim.plugin.GsClassLoader;
import fr.univmrs.ibdm.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.common.manageressources.ImageLoader;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.AboutDialog;
import fr.univmrs.tagc.common.xml.XMLHelper;

/**
 * this class is used to run GINsim: parse args and so on
 */
public class GsMain {

    /**
     * @param args
     * try to run it with --help for more info
     */
    public static void main(String[] args) {

        String gsdir = ".";
        boolean startui = true;
        Vector commands = new Vector(0);
        Vector open = new Vector(0);
        try {
        	GsEnv.readConfig("/fr/univmrs/ibdm/GINsim/ressources/GINsim-config.xml");
        } catch (Exception e) {
        	e.printStackTrace();
        	System.exit(1);
        }
        /*
         * parse args: - run without GUI - set ginsim dir - choose locale - give
         * some help
         */
        for (int i = 0; i < args.length; i++) {
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

        for (int i = 0; i < commands.size(); i++) {
            System.out.println("run: " + commands.get(i) + "[not yet done]");
        }

        if (startui) {
            if (open.size() > 0) {
                for (int i = 0; i < open.size(); i++) {
                    GsOpenAction.open(GsGinsimGraphDescriptor.getInstance(),
                            GsEnv.newMainFrame(), null, (String) open.get(i));
                }
            } else {
                //                try {
                //                    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                //                    //System.out.println(UIManager.getSystemLookAndFeelClassName());
                //                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                //                } catch (Exception e) {
                //                    e.printStackTrace();
                //                }

                GsEnv.newMainFrame();
            }
        }
    }
}

/**
 * This class reads GINsim's generic config file, loads plugins and so on.
 */
class ReadConfig extends XMLHelper {

	GsClassLoader cloader;
	
	protected ReadConfig(GsClassLoader cloader) {
		this.cloader = cloader;
	}
	
	public String getFallBackDTD() {
		return null;
	}

	public GsGraph getGraph() {
		return null;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("plugin".equals(qName)) {
			String s = attributes.getValue("load");
			if ("true".equals(s)) {
				s = attributes.getValue("mainClass");
				try {
					Class cl = cloader.loadClass(s);
					GsPlugin plugin = (GsPlugin)cl.newInstance();
					plugin.registerPlugin();
				} catch (Exception e) {
					System.out.println("unable to load plugin from class: "+s);
				}
			}
		} else if ("graph".equals(qName)) {
			String s = attributes.getValue("load");
			if ("true".equals(s)) {
				s = attributes.getValue("mainClass");
				try {
					Class cl = cloader.loadClass(s);
					GsEnv.addGraphType((GsGraphDescriptor)cl.newInstance());
				} catch (Exception e) {
					System.out.println("unable to add graphType from class: "+s);
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
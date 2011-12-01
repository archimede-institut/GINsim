package org.ginsim;

import java.io.File;

import org.python.util.PythonInterpreter;

import fr.univmrs.tagc.common.Debugger;

/**
 * Launch GINsim in Jython scripting mode.
 * 
 * @author Aurelien Naldi
 *
 */
public class GINsimPy {
	
	public static void runJython(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            System.out.println("No such script: "+filename);
        	return;
        }

        PythonInterpreter pi = new PythonInterpreter();
        
		try {
			// FIXME: helper file for script mode
			
            //InputStream is = Tools.getStreamForPath("/org/ginsim/gui/service/tool/jython/GINsim.py");
            //pi.execfile(is, "GS.py");
            //is.close();
            //pi.exec("GINsim = GINsim()");
		} catch (Exception e) {
            Debugger.error("Script mode failed");
            Debugger.error(e);
            return;
		}

        pi.execfile(filename);
	}

}

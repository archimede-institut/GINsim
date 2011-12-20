package org.ginsim;

import java.io.File;

import org.ginsim.common.utils.log.LogManager;
import org.python.util.PythonInterpreter;


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
            LogManager.error( "No such script: "+filename);
        	return;
        }

        PythonInterpreter pi = new PythonInterpreter();
        
		try {
			// FIXME: helper file for script mode
			
            //InputStream is = IOUtils.getStreamForPath("/org/ginsim/gui/service/tool/jython/GINsim.py");
            //pi.execfile(is, "GS.py");
            //is.close();
            //pi.exec("GINsim = GINsim()");
		} catch (Exception e) {
            LogManager.error("Script mode failed");
            LogManager.error(e);
            return;
		}

        pi.execfile(filename);
	}

}

package org.ginsim;

import java.io.File;

import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.GsScriptHelper;
import org.python.util.PythonInterpreter;


/**
 * Launch GINsim in Jython scripting mode.
 * 
 * @author Aurelien Naldi
 *
 */
public class GINsimPy {
	
	public static void runJython(String filename, String[] args) {
        File f = new File(filename);
        if (!f.exists()) {
            LogManager.error( "No such script: "+filename);
        	return;
        }

        GsScriptHelper.getInstance(args);
        PythonInterpreter pi = new PythonInterpreter();
        
		try {
			String s_scriptHelper = GsScriptHelper.class.getSimpleName();
			String s_scriptHelper_package = GsScriptHelper.class.getPackage().getName();
			pi.exec("from "+s_scriptHelper_package + " import "+s_scriptHelper);
			pi.exec("gs = "+s_scriptHelper+".getInstance()");
			
		} catch (Exception e) {
            LogManager.error("Script mode failed");
            LogManager.error(e);
            return;
		}

        pi.execfile(filename);
        // force exit as jython seems to often delay it
        System.exit(0);
	}

}

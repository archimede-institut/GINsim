package org.ginsim.core.graph.view.css;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.objectassociation.BasicGraphAssociatedManager;

public class CSSFilesAssociatedManager extends BasicGraphAssociatedManager {

	public static final String KEY = "cssfiles";
	
	public CSSFilesAssociatedManager() {
		super(KEY, null);
	}
	
	@Override
	public boolean needSaving(Graph graph) {
		Object o = getObject(graph);
		return (o != null && ((List)o).size() > 0);
	}

	@Override
	public void doSave(OutputStreamWriter out, Graph graph) throws GsException {
		try {
			for (CSSFile cssFile : 	(List<CSSFile>)getObject(graph)) {
					out.append('@');
					out.write(cssFile.name);
					out.append('\n');
					out.write(cssFile.css.toString());
					out.append('\n');
					cssFile.saved = true;
			}
		} catch (IOException e) {
			LogManager.error("Error while saving the associated css files : "+e);
		}
	}

	@Override
	public Object doOpen(InputStream is, Graph graph) throws GsException {
		ArrayList<CSSFile> cssFiles = new ArrayList<CSSFile>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			CSSFile currentFile = null;
			StringBuffer content = new StringBuffer();
			while ((line = br.readLine()) != null) {
				if (line.length() > 0 && line.charAt(0) == '@') {
					if (currentFile != null) {
						currentFile.css = CascadingStyleSheet.newFromText(content.toString());
						currentFile.saved = true;
						cssFiles.add(currentFile);
					}
					currentFile = new CSSFile(line.substring(1));
				} else {
					content.append(line);
				}
			}
			return cssFiles;
		} catch (IOException e) {
			LogManager.error("Error while opening the associated css files : "+e);
			return cssFiles;
		} catch (CSSSyntaxException e) {
			LogManager.error("Error while opening the associated css files : "+e);
			return cssFiles;
		}
	}

	@Override
	public Object doCreate(Graph graph) {
		return new ArrayList<CSSFile>();
	}


}

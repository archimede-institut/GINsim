package fr.univmrs.tagc.common.document;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.Tools;

/**
 * A FileChooser with a proper filter for all the document supported
 *
 *
 * <b>Example</b>
 * 			try {
 *				Object[] fileAndFormat = GenericDocumentFileChooser.saveDialog(<i>the key to save the currentDirectory</i>, <i>the frame</i>);
 *				if (fileAndFormat != null) {
 *					DocumentWriter doc = (DocumentWriter)((GenericDocumentFormat)fileAndFormat[1]).documentWriterClass.newInstance();
 *					doc.setOutput((File)fileAndFormat[0]);
 *					<i>Your action here....</i>
 *				}
 *			} catch (Exception e1) {
 *				Tools.error("An error has occurred while saving", this.frame);
 *			}
 *
 */
public class GenericDocumentFileChooser extends JFileChooser {
	private static final long serialVersionUID = -4427826474458648004L;
	
	public GenericDocumentFileChooser() {
		super();
		initFilter();
	}
	
	public GenericDocumentFileChooser(String currentDirectory) {
		super(currentDirectory);
		initFilter();
	}
	
	public GenericDocumentFileChooser(File currentDirectory) {
		super(currentDirectory);
		initFilter();
	}
	
	public GenericDocumentFileChooser(File curDir, Vector formats) {
		super(curDir);
		for (Iterator it = formats.iterator(); it.hasNext();) {
			GenericDocumentFormat format = (GenericDocumentFormat) it.next();
			GenericDocumentFileFilter filter = new GenericDocumentFileFilter();
			filter.setExtensionList(format);
			addChoosableFileFilter(filter);			
		}
	}

	private void initFilter() {
		for (Iterator it = GenericDocumentFormat.getAllFormats().iterator(); it.hasNext();) {
			GenericDocumentFormat format = (GenericDocumentFormat) it.next();
			GenericDocumentFileFilter filter = new GenericDocumentFileFilter();
			filter.setExtensionList(format);
			addChoosableFileFilter(filter);			
		}
	}
	
	/**
	 * Return the format corresponding to the selected file filter. 
	 * 
	 * @param parentWindow to display the error
	 * @return the format or null
	 */
	public GenericDocumentFormat getSelectedFormat(Component parentWindow) {
		try {
			GenericDocumentFileFilter filter = (GenericDocumentFileFilter)getFileFilter();
			return filter.getFormat();
		} catch (ClassCastException e) {
			Tools.error("You must choose a valid extension", parentWindow);
			return null;
		}
	}
	
	/**
	 * Open a dialog asking to choose a save file location
	 * 
	 * @param optionStoreDirectory  the key to load/store the initial directory of the panel
	 * @param parentWindow the window to attach the panel to
	 * 
	 * @return an array of two element : [0] is the file, [1] is the GenericDocumentFormat
	 * 
	 * @throws IOException
	 * @throws GsException
	 */
	public static Object[] saveDialog(String optionStoreDirectory, Component parentWindow) throws IOException, GsException {
		GenericDocumentFileChooser jfc = GenericDocumentFileChooser.createFileChooser(optionStoreDirectory);
		int returnVal = jfc.showSaveDialog(parentWindow);
        if (null != jfc.getSelectedFile() && returnVal == JFileChooser.APPROVE_OPTION) {
    		if (optionStoreDirectory != null) {
    			OptionStore.setOption(optionStoreDirectory, jfc.getCurrentDirectory().getPath());
    		}
    		GenericDocumentFormat format = jfc.getSelectedFormat(parentWindow);
    		if (format == null) {
    			return null;
    		}
        	String filePath = jfc.getSelectedFile().getPath();
        	String extension = "."+format.defaultExtension;
        	if (extension != null && ! filePath.endsWith(extension)) {
        		filePath += extension;
        	}
            if (Tools.isFileWritable(filePath, parentWindow)) {               
                Object[] v = new Object[2];
                v[0] = new File(filePath);
                v[1] = format;
                return v;
            }
        }
        return null;
	}

	/**
	 * Open a dialog asking to choose a save file location
	 * 
	 * @param optionStoreDirectory  the key to load/store the initial directory of the panel
	 * @param parentWindow the window to attach the panel to
	 * 
	 * @return an array of two element : [0] is the file, [1] is the GenericDocumentFormat
	 * 
	 * @throws IOException
	 * @throws GsException
	 */
	public static Object[] saveDialog(String optionStoreDirectory, Component parentWindow, Vector formats) throws IOException, GsException {
		GenericDocumentFileChooser jfc = GenericDocumentFileChooser.createFileChooser(optionStoreDirectory, formats);
		int returnVal = jfc.showSaveDialog(parentWindow);
        if (null != jfc.getSelectedFile() && returnVal == JFileChooser.APPROVE_OPTION) {
    		if (optionStoreDirectory != null) {
    			OptionStore.setOption(optionStoreDirectory, jfc.getCurrentDirectory().getPath());
    		}
    		GenericDocumentFormat format = jfc.getSelectedFormat(parentWindow);
    		if (format == null) {
    			return null;
    		}
        	String filePath = jfc.getSelectedFile().getPath();
        	String extension = "."+format.defaultExtension;
        	if (extension != null && ! filePath.endsWith(extension)) {
        		filePath += extension;
        	}
            if (Tools.isFileWritable(filePath, parentWindow)) {               
                Object[] v = new Object[2];
                v[0] = new File(filePath);
                v[1] = format;
                return v;
            }
        }
        return null;
	}

	private static GenericDocumentFileChooser createFileChooser(String optionStoreDirectory) {
       File curDir = null;
       String path = (String)OptionStore.getOption(optionStoreDirectory);
       if (path != null) {
           curDir = new File(path);
       }
       if (curDir != null && !curDir.exists()) {
           curDir = null;
       }
      return new GenericDocumentFileChooser(curDir);
   }
	private static GenericDocumentFileChooser createFileChooser(String optionStoreDirectory, Vector formats) {
	       File curDir = null;
	       String path = (String)OptionStore.getOption(optionStoreDirectory);
	       if (path != null) {
	           curDir = new File(path);
	       }
	       if (curDir != null && !curDir.exists()) {
	           curDir = null;
	       }
	      return new GenericDocumentFileChooser(curDir, formats);
	   }

}

/**
 * A simple file filter working with a GenericDocumentFormat
 *
 */
class GenericDocumentFileFilter extends FileFilter {
	private GenericDocumentFormat format;

	public void setExtensionList(GenericDocumentFormat format) {
		this.format = format;
	}

	/**
     *Accept all directories and files with the good extension.
	 * @param f
	 * @return true if the file is accepted
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = f.getName().substring(f.getName().lastIndexOf(".")+1,f.getName().length());
        
        if (format.extensionArray == null) {
        		return true;
        }
        for (int i=0 ; i<format.extensionArray.length ; i++) {
	        	if (extension.equalsIgnoreCase(format.extensionArray[i])) {
	        		return true;
	        	}
        }
        return false;
    }

    public String getDescription() {
        return format.filterDescr;
    }
    
    public GenericDocumentFormat getFormat() {
    	return format;
    }
}

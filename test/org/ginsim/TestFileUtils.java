package org.ginsim;

import java.io.File;

import org.ginsim.common.utils.IOUtils;

public class TestFileUtils {

	// The directory where are stored the predefined files for the tests
	public static File testFileDirectory = new File( ".", "testFiles");
	
	/**
	 * Returns the directory where test files are stored
	 * 
	 * @return the directory where test files are stored
	 */
	public static File getTestFileDir(){
		
		return testFileDirectory;
	}
	
	/**
	 * Returns a directory under the main test file directory with the given name
	 * 
	 * @param module the name of the sub-directory
	 * @return a directory under the main test file directory with the given name
	 */
	public static File getTestFileDirectory( String module){
		
		File file = new File( testFileDirectory, module);
		
		if( !file.exists()){
			file.mkdir();
		}
		
		return file;
	}
	
	/**
	 * Returns a temporary directory under the dedicated directory of the given module
	 * 
	 * @param module the name of the sub-directory
	 * @return a directory under the main test file directory with the given name
	 */
	public static File getTempTestFileDirectory( String module){
		
		return new File( getTestFileDirectory( module), "tmp");
	}
	
	/**
	 * Remove and rebuild the temporary directory under the dedicated directory of the given module
	 * 
	 * @param module the name of the sub-directory
	 */
	public static void cleanTempTestFileDirectory( String module){
		
		File file = getTempTestFileDirectory( module);
		if( file.exists()){
			IOUtils.deleteDirectory( file);
		}
		file.mkdir();
	}
	
	
	
	
}

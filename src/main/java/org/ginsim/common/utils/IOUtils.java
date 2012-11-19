package org.ginsim.common.utils;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.biojava.bio.seq.io.StreamReader;
import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.Translator;

/**
 * Collection of helpers to open and parse files, or streams, 
 * allowing to access files inside jar archives from the JVM classpath.
 * 
 * @author Aurelien Naldi
 */
public class IOUtils {

	/**
	 * Produces a FileInputStream initialized with the given path
	 * 
	 * @param path the path for which the InputSTream is desired
	 * @return a FileInputStream initialized with the given path
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static InputStream getStreamForPath(String path) throws IOException,	FileNotFoundException {
		
		URL url = IOUtils.class.getResource( path);
		if (url != null) {
			return url.openStream();
		}
		return new FileInputStream(path);
	}

	/**
	 * get an input stream for a file inside a given package.
	 * 
	 * @param pack the package in which to lookup
	 * @param filename the name of the desired file in this package
	 * @return a FileInputStream initialized with the given path
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static InputStream getStreamForPath(Package pack, String filename) throws IOException,	FileNotFoundException {
		String path = pack.getName().replace(".", File.separator);
		return getStreamForPath(File.separator+path+File.separator+filename);
	}

	
	/**
	 * Produces a StringBuffer initialized with the given path
	 * 
	 * @param file_path the path for which the StringBuffer is desired
	 * @return a StringBuffer initialized with the given path
	 * @throws IOException
	 */
	public static StringBuffer readFromFile(String file_path) throws IOException {
		
		StringBuffer sb = new StringBuffer(1024);
		readFromFile(file_path, sb);
		return sb;
	}

	/**
	 * Fill the given StringBuffer with the content of the file located at the given path
	 * 
	 * @param file_path the path to the file to read
	 * @param sb the StringBuffer to fill
	 * @throws IOException
	 */
	public static void readFromFile(String file_path, StringBuffer sb) throws IOException {
		read(new FileReader(file_path), sb);
	}
	
	public static StringBuffer readFromResource(InputStream istream) throws IOException {
		StringBuffer sb = new StringBuffer(1024);
		read(new InputStreamReader(istream), sb);
		return sb;
	}

	public static void read(Reader rd, StringBuffer sb) throws IOException {
		BufferedReader reader = new BufferedReader(rd);
		//BufferedReader reader = new BufferedReader();
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			sb.append(buf, 0, numRead);
		}
		reader.close();
	}

	
	/**
	 * Verify if the file at the given path can be opened in write mode
	 * 
	 * @param path the path of the file to test
	 * @return true if file exists and is writable or file does not exists and can be created
	 * @throws GsException
	 */
	public static boolean isFileWritable( String path) throws GsException {
		
		if (path == null || path.equals("")) {
			return false;
		}
		
		File file = new File(path);
		if (file.exists()) {

			if (file.isDirectory()) {
				throw new GsException(GsException.GRAVITY_ERROR,
						Translator.getString("STR_error_isdirectory"));
			}
			if (!file.canWrite()) {
				throw new GsException(GsException.GRAVITY_ERROR,
						Translator.getString("STR_error_notWritable"));
			}

		}
		try {
			if (!file.createNewFile()) {
				throw new GsException(GsException.GRAVITY_ERROR,
						Translator.getString("STR_error_cantcreate"));
			}
			file.delete();
			return true;
		} catch (Exception e) {
			throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_error_io"));
		}
	}
	
	
	/**
	 * Remove all files and directory from the given path
	 * 
	 * @param path the file to delete
	 * @return true if the file was properly deleted, false otherwise
	 */
	public static boolean deleteDirectory( File path) {
		
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return path.delete();
	}

}

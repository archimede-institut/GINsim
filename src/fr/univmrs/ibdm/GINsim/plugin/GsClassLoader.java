package fr.univmrs.ibdm.GINsim.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * this class offer a class loader to help loading plugins
 */
public class GsClassLoader extends ClassLoader {

	private static char dirSeparator=System.getProperty("file.separator").charAt(0);
	private Hashtable classes = new Hashtable();

	/**
	 * @param file
	 * @return the jarFile ready to be processed
	 */
	public JarFile getJarFile(File file) {
		try {		
			return new JarFile(file);
		} catch (IOException e) {return null;}
	}

	/**
	 * @param jarFile
	 * @return the extracted manifest file
	 */
	public Manifest getManifest(JarFile jarFile) {
		try {
			return jarFile.getManifest();
		} catch (IOException e){ return null; }
	}

	/**
	 * @param jarFile
	 * @param name
	 * @return the value of the attribute
	 */
	public Attributes getManifestAttributes(JarFile jarFile, String name) {
		try {
			return jarFile.getManifest().getAttributes(name);
		} catch (IOException e){ return null; }
	}

	/**
	 * @param className
	 * @return the loaded Class
	 * @throws ClassNotFoundException
	 */
	public Class loadClass(String className) throws ClassNotFoundException {
		return (loadClass(className, true));
	}

	/**
	 * @param jarFile
	 * @param className
	 * @param resolve
	 * @return the loaded Class
	 * @throws ClassNotFoundException
	 */
	public synchronized Class loadClass(JarFile jarFile, String className, boolean resolve) throws ClassNotFoundException {
		if (classes.containsKey(className)) return (Class)classes.get(className);
		BufferedInputStream bis = null;
		byte[] res = null;
		try {
			ZipEntry zipEntry = jarFile.getEntry(className.replace('.', dirSeparator) + ".class");
			
			res = new byte[(int)zipEntry.getSize()];
			bis = new BufferedInputStream(jarFile.getInputStream(zipEntry));
			bis.read(res, 0, res.length);
		} catch (Exception ex) {
		} finally {
			if (bis!=null) {
				try {
					bis.close();
				} catch (IOException ioex) {}
			}
			if (jarFile!=null) {
				try {
					jarFile.close();
				} catch (IOException ioex) {}
			}
		}

		if (res == null) return super.findSystemClass(className);

		Class clazz = defineClass(className, res, 0, res.length);
		if (clazz == null) throw new ClassFormatError();

		if (resolve) resolveClass(clazz);
		classes.put(className, clazz);
		return(clazz);
	}
}

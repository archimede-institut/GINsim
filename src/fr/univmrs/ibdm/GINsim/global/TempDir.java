package fr.univmrs.ibdm.GINsim.global;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class TempDir {
	private static DirDeleter deleterThread;

	static {
		deleterThread = new DirDeleter();
		Runtime.getRuntime().addShutdownHook(deleterThread);
	}

	/**
	 * Creates a temp directory with a generated name (given a certain prefix)
	 * in a given directory. The directory (and all its content) will be
	 * destroyed on exit.
	 */
	public static File createGeneratedName(String prefix, File directory)
			throws IOException {
		File tempFile;
		if (directory == null) {
			tempFile = File.createTempFile(prefix, "");
		} else {
			tempFile = File.createTempFile(prefix, "", directory);
		}
		if (!tempFile.delete())
			throw new IOException();
		if (!tempFile.mkdir())
			throw new IOException();
		deleterThread.add(tempFile);
		return tempFile;
	}

	/**
	 * Creates a temp directory with a given name in a given directory. The
	 * directory (and all its content) will be destroyed on exit.
	 */
	public static File createNamed(String name, File directory)
			throws IOException {
		File tempFile = new File(directory, name);
		if (!tempFile.mkdir())
			throw new IOException();
		deleterThread.add(tempFile);
		return tempFile;
	}
}

class DirDeleter extends Thread {
	private ArrayList dirList = new ArrayList();

	public synchronized void add(File dir) {
		dirList.add(dir);
	}

	public void run() {
		synchronized (this) {
			Iterator iterator = dirList.iterator();
			while (iterator.hasNext()) {
				File dir = (File) iterator.next();
				deleteDirectory(dir);
				iterator.remove();
			}
		}
	}

	private void deleteDirectory(File dir) {
		File[] fileArray = dir.listFiles();

		if (fileArray != null) {
			for (int i = 0; i < fileArray.length; i++) {
				if (fileArray[i].isDirectory())
					deleteDirectory(fileArray[i]);
				else
					fileArray[i].delete();
			}
		}
		dir.delete();
	}
}

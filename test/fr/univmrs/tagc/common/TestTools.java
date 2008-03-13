package fr.univmrs.tagc.common;

import java.io.File;
import java.io.IOException;


public class TestTools {

	private static File testDir = null;
	
	public static File getTestDir() {
		if (testDir == null) {
			testDir = new File("testCase");
			if (!testDir.exists()) {
				testDir.mkdir();
			}
		}
		return testDir;
	}
	
	public static File getTempDir() {
		try {
			return TempDir.createNamed("tmp", getTestDir());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

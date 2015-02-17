package com.oculosopressor.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	public static  List<File> getListFiles(File parentDir) {
		ArrayList<File> inFiles = new ArrayList<File>();
		File[] files = parentDir.listFiles();
		
		if(files!=null)
		for (File file : files) {
			if (file.isDirectory()) {
				inFiles.addAll(getListFiles(file));
			} else {
				if (file.getName().endsWith(".jpg")) {
					inFiles.add(file);
				}
			}
		}
		
		return inFiles;
	}

}

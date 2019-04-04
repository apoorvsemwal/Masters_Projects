package com.risk.file;

import java.util.List;

import com.risk.model.file.File;

/**
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
 * @version 0.0.1
 */
public interface IManageFile {

	/**
	 * This Function reads various sections and their related information from map
	 * file.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @return Map File Object After Reading Entire Map File
	 */
	File retreiveFileObject();

	/**
	 * This function is use to write map data to new or existing map file.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param file      File Object
	 * @param file_name The name of map file for saving purpose
	 * @return File Write Status Message
	 */
	Boolean saveFileToDisk(File file, String file_name);

	/**
	 * This function is use to fetch all map files from Resource Folder
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @return Name List of Map Files
	 */
	List<String> fetchMapFilesFromResource();

}

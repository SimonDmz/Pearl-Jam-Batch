package fr.insee.pearljam.batch.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Operation on paths
 * - getTimestampForPath
 * - isDirContainsErrorFile
 * - isDirectoryExist
 * - isDirContainsFileExtension
 * - getListFileName
 * - isFileExist
 * - getExtensionByStringHandling
 * - getFileNameWithoutExtension
 *  
 * @author Claudel Benjamin
 * 
 */
public class PathUtils {
	private static final Logger logger = LogManager.getLogger(PathUtils.class);

	private PathUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * This method get the current time for the naming of files
	 * @return the exact time in String format
	 */
	public static String getTimestampForPath() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");  
		Date dateNow = new Date();
		return formatter.format(dateNow);
	}
	
	/**
	 * This method check if a directory contains an error file created after the step "clean and reset"
	 * @param pathToDirectory
	 * @param fileType
	 * @param extension
	 * @return boolean
	 */
	public static boolean isDirContainsErrorFile(Path pathToDirectory, String fileType, String extension) {
		boolean isDirContainsErrorFile = false;
		List<String> fileNames = getListFileName(pathToDirectory);
		for(int i=0; i < fileNames.size(); i++) {
			if(fileNames.get(i).contains(fileType) && fileNames.get(i).contains(extension)) {
				isDirContainsErrorFile = true;
			}
		}
		return isDirContainsErrorFile;
	}
	
	/**
	* Check if a directory exists
	* 
	* @param path path of directory
	* @return true if directory exists
	*/
	public static boolean isDirectoryExist(String path) {
		File tmpDir = new File(path);
		return tmpDir.exists() && tmpDir.isDirectory();
	}
	
	/**
	* Check if a directory contains a file with a given extension
	* 
	* @param directory directory to check
	* @return true if directory contains the given file extension
	*/
	public static boolean isDirContainsFileExtension(final Path directory, String filename) {
		try{
			Iterator<Path> i = Files.newDirectoryStream(directory).iterator();
			while (i.hasNext()) {
				if(filename.equals(i.next().getFileName().toString())){
					return true;
				}
			}
		} catch (IOException e) {
			logger.log(Level.ERROR, e.getMessage(), e);
			return false;
		}
		return false;
	}
	
	/**
	* get the list of filenames in a given directory
	* 
	* @param directory directory to explore
	* @return the list of filenames in the directory
	*/
	public static List<String> getListFileName(final Path directory) {
		List<String> listFileName = new ArrayList<>();
		try{
			Iterator<Path> i = Files.newDirectoryStream(directory).iterator();
			while (i.hasNext()) {
				String fileName = i.next().getFileName().toString();
				listFileName.add(fileName);
			}
		} catch (IOException e) {
			logger.log(Level.ERROR, e.getMessage(), e);
		}
		return listFileName;
	}

	/**
	* Check if a file exists
	* 
	* @param fileName fileName to check
	* @return true if file exists
	*/
	public static boolean isFileExist(String fileName) {
		File tmpDir = new File(fileName);
		return tmpDir.exists() && tmpDir.isFile();
	}

	/**
	* get the extention of a given filename
	* 
	* @param filename filename to check
	* @return the extention of the file
	*/
	public static Optional<String> getExtensionByStringHandling(String filename) {
		return Optional.ofNullable(filename).filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf('.') + 1));
	}
	
	/**
	* get the file name without extention of a given filename
	* 
	* @param filename filename to check
	* @return the filename without extention
	*/
	public static String getFileNameWithoutExtension(String filename) {
		return filename.replaceFirst("[.][^.]+$", "");
	}
}

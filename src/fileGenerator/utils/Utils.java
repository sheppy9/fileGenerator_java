package fileGenerator.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Utils {

	private static final String EMPTY_STRING = "";
	private static final String FULL_STOP = ".";

	public static Path createFolderIfNotExist(String... folders) {
		String strPath = folders != null && folders.length > 0
				? Arrays.stream(folders).filter(folder -> !folder.contains(FULL_STOP)).collect(Collectors.joining(File.separator))
				: EMPTY_STRING;
		String fileName = Arrays.stream(folders).filter(folder -> folder.contains(FULL_STOP)).findFirst().orElse(EMPTY_STRING);
		Path path = Paths.get(strPath);
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			System.out.println("Failed to create folders: " + strPath);
		}

		return hasText(fileName) ? path.resolve(fileName) : path;
	}

	public static List<Path> getFilesFromFolder(Path folder) {
		return getFilesFromFolder(folder.toString());
	}

	public static List<Path> getFilesFromFolder(String folder) {
		Path path = Paths.get(hasText(folder) ? folder : EMPTY_STRING);

		if (path.toFile().exists()) {
			try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path);) {
				return StreamSupport.stream(dirStream.spliterator(), false).collect(Collectors.toList());
			} catch (IOException e) {
				System.out.println("Unable to get list of files from folder: " + folder);
			}
		}
		return Collections.emptyList();
	}

	public static boolean hasText(String text) {
		return text != null && !text.equalsIgnoreCase("");
	}
}

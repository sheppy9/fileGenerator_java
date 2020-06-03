package fileGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import fileGenerator.utils.Utils;

public class ConsoleFileGenerator {

	// Creating windows shortcuts
	// Enter the following in "Target"
	// --> java - jar fileGenerator_java.jar
	// Enter the following in "Start in"
	// --> %HOMEDRIVE%%HOMEPATH%\Desktop
	// change "Desktop" or the whole "Start in" to the path of the runnable jar

	private static final String version = "2";
	private static final String exitStr = "bye";
	private static final String PACKAGE_NAME = "(packageName)";
	private static final String LOWER = "(lower)";

	private static final String FOLDER_FORMAT = LOWER;

	public static void main(String[] args) {
		System.out.println("########################################");
		System.out.println("########## File Generator v" + version + " ##########");
		System.out.println("########################################");
		System.out.println();

		while (true) {
			Scanner scanner = new Scanner(System.in);

			System.out.print("Package [core/ biz] name: ");
			String packageName = scanner.nextLine();
			checkExit(scanner, packageName);

			System.out.print("Entity name: ");
			String entityName = scanner.nextLine();
			checkExit(scanner, entityName);

			generateFiles(packageName, entityName);
			System.out.println();
		}
	}

	private static void checkExit(Scanner scanner, String value) {
		if (value.equalsIgnoreCase(exitStr)) {
			scanner.close();
			System.out.println("Thank for using File Generator. Exited");
			System.exit(0);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map<String, String> getReplaceValues(String packageName, String entityName) {
		Map<String, String> values = new HashMap();

		values.put(PACKAGE_NAME, packageName);
		values.put("(lower)", entityName.toLowerCase());
		values.put("(lowerFirst)", entityName.substring(0, 1).toLowerCase() + entityName.substring(1, entityName.length()));
		values.put("(upper)", entityName.toUpperCase());
		values.put("(upperFirst)", entityName.substring(0, 1).toUpperCase() + entityName.substring(1, entityName.length()));
		values.put("(remain)", entityName);

		return values;
	}

	private static void generateFiles(String packageName, String entityName) {
		Path folder = Paths.get("files");
		List<Path> files = Utils.getFilesFromFolder(folder);
		packageName = Utils.hasText(packageName) && (packageName.equalsIgnoreCase("c") || packageName.equalsIgnoreCase("core")) ? "core" : "biz";
		Map<String, String> values = getReplaceValues(packageName, entityName);

		System.out.println("Generating " + files.size() + " file(s) for " + entityName);

		for (Path file : files) {
			System.out.println("Replacing content of " + file.getFileName());

			String content = null;
			try {
				content = new String(Files.readAllBytes(file));
			} catch (IOException e) {
				System.out.println("[Error] Failed to read template content: " + e.getMessage());
			}

			if (content != null) {
				String fileName = file.getFileName().toString();
				for (Entry<String, String> value : values.entrySet()) {
					content = content.replace(value.getKey(), value.getValue());
					fileName = fileName.replace(value.getKey(), value.getValue());
				}

				Path outputFile = Utils.createFolderIfNotExist(System.getProperty("user.home"), "desktop", values.get(FOLDER_FORMAT), fileName);

				try {
					Files.write(outputFile, content.getBytes("UTF-8"), StandardOpenOption.CREATE);
					System.out.println("File Generated at: " + outputFile);
				} catch (IOException e) {
					System.out.println("[Error] Failed to write to file: " + e.getMessage());
				}
			}
		}
	}
}

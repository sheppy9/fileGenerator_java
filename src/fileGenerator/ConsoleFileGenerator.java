package fileGenerator;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fileGenerator.utils.Utils;

public class ConsoleFileGenerator {

	// Creating windows shortcuts
	// Enter the following in "Target"
	// --> java - jar fileGenerator_java.jar
	// Enter the following in "Start in"
	// --> %HOMEDRIVE%%HOMEPATH%\Desktop
	// change "Desktop" or the whole "Start in" to the path of the runnable jar

	private static final String VERSION = "2.0.1";
	private static final String EXIT_CODE = "bye";
	private static final String LOWER = "(lower)";

	private static final String FOLDER_FORMAT = LOWER;
	private static final String DESKTOP = System.getProperty("user.home") + File.separator + "desktop";

	public static void main(String[] args) {
		print("########################################");
		print("########## File Generator v" + VERSION + " ##########");
		print("########################################");
		print();
		Scanner scanner = new Scanner(System.in);

		while (true) {
			print("Entity name: ");
			String entityName = scanner.nextLine();

			if (EXIT_CODE.equalsIgnoreCase(entityName)) {
				scanner.close();
				print("Thank for using File Generator. Exited");
				System.exit(0);
				scanner.close();
				break;
			}

			generateFiles(entityName);

			try {

				Desktop.getDesktop().open(new File(DESKTOP));
			} catch (IOException e) {
				print("Failed to open file explorer: " + e.getMessage());
			}
			print();
		}

		scanner.close();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map<String, String> getReplaceValues(String entityName) {
		Map<String, String> values = new HashMap();

		values.put(LOWER, entityName.toLowerCase());
		values.put("(lowerFirst)", entityName.substring(0, 1).toLowerCase() + entityName.substring(1, entityName.length()));
		values.put("(upper)", entityName.toUpperCase());
		values.put("(upperFirst)", entityName.substring(0, 1).toUpperCase() + entityName.substring(1, entityName.length()));
		values.put("(remain)", entityName);

		return values;
	}

	private static void generateFiles(String entityName) {
		Path folder = Paths.get("files");
		List<Path> files = Utils.getFilesFromFolder(folder);
		Map<String, String> values = getReplaceValues(entityName);

		print("Generating " + files.size() + " file(s) for " + entityName);

		for (Path file : files) {
			print("Replacing content of " + file.getFileName());

			String content = null;

			try {
				content = new String(Files.readAllBytes(file));
			} catch (IOException e) {
				print("[Error] Failed to read template content: " + e.getMessage());
			}

			if (content != null) {
				String fileName = file.getFileName().toString();

				for (Entry<String, String> value : values.entrySet()) {
					content = content.replace(value.getKey(), value.getValue());
					fileName = fileName.replace(value.getKey(), value.getValue());
				}

				try {
					Path outputFile = Utils.createFolderIfNotExist(DESKTOP, values.get(FOLDER_FORMAT), fileName);
					Files.write(outputFile, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
					print("File Generated at: ", outputFile);
				} catch (IOException e) {
					print("[Error] Failed to write to file: ", e.getMessage());
				}
			}
		}
	}

	private static void print(Object... values) {
		System.out.println(Stream.of(values).map(Object::toString).collect(Collectors.joining()));
	}
}

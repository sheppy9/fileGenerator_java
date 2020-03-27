package fileGenerator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fileGenerator.utils.Utils;

public class FileGenerator extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static JFrame frame;
	private static JPanel panel;
	private static JTextField packageText;
	private static JTextField objectName;

	private static final String NAME = "(name)";
	private static final String PACKAGE_NAME = "(packageName)";
	private static final String LOWER = "(lower)";
	private static final String LOWER_FIRST = "(lowerFirst)";
	private static final String UPPER = "(upper)";
	private static final String UPPER_FIRST = "(upperFirst)";
	private static final String REMAIN = "(remain)";

	FileGenerator() {
	}

	public static void main(String[] args) {
		FileGenerator fileGenerator = new FileGenerator();

		frame = new JFrame("File Generator");
		panel = new JPanel();

		JLabel packageLabel = new JLabel("Package Name - biz[b] or core[c]?: ");
		JLabel objectNameLabel = new JLabel("Object Name: ");

		packageLabel.setPreferredSize(new Dimension(200, 15));
		objectNameLabel.setPreferredSize(new Dimension(200, 15));

		packageLabel.setHorizontalAlignment(JTextField.RIGHT);
		objectNameLabel.setHorizontalAlignment(JTextField.RIGHT);

		packageText = new JTextField(15);
		objectName = new JTextField(15);

		JButton button = new JButton("Submit");

		frame.setPreferredSize(new Dimension(400, 300));

		button.addActionListener(fileGenerator);

		button.setLayout(new FlowLayout(FlowLayout.TRAILING));

		packageText.addKeyListener(keyListener());
		objectName.addKeyListener(keyListener());

		panel.add(packageLabel);
		panel.add(packageText);

		panel.add(objectNameLabel);
		panel.add(objectName);

		panel.add(button, BorderLayout.EAST);

		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
	}

	private static KeyListener keyListener() {
		return new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String packageVal = packageText.getText();
				String objectVal = objectName.getText();
				if (Utils.hasText(packageVal) || Utils.hasText(objectVal)) {
					if ("bye".equalsIgnoreCase(packageVal) || "bye".equalsIgnoreCase(objectVal)) {
						frame.dispose();
					}

					if ("enter".equalsIgnoreCase(e.getKeyText(e.getKeyCode()))) {
						generateFile(packageVal, objectVal);
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String packageVal = packageText.getText();
		String objectVal = objectName.getText();
		if (e.getActionCommand().equalsIgnoreCase("submit")) {
			generateFile(packageVal, objectVal);
		}
	}

	private static void generateFile(String packageName, String objName) {
		if (Utils.hasText(packageName) && Utils.hasText(objName)) {
			Path folder = Paths.get("files");
			List<Path> files = Utils.getFilesFromFolder(folder);
			packageName = packageName.equalsIgnoreCase("c") || packageName.equalsIgnoreCase("core") ? "core" : "biz";

			for (Path file : files) {
				try {
					String lowerFirst = objName.substring(0, 1).toLowerCase() + objName.substring(1, objName.length());

					String content = new String(Files.readAllBytes(file));
					content = content.replace(PACKAGE_NAME, packageName);

					content = content.replace(LOWER, objName.toLowerCase());
					content = content.replace(LOWER_FIRST, lowerFirst);

					content = content.replace(UPPER, objName.toUpperCase());
					content = content.replace(UPPER_FIRST, objName.substring(0, 1).toUpperCase() + objName.substring(1, objName.length()));

					content = content.replace(REMAIN, objName);

					System.out.println(file.getFileName().toString().replace(NAME, objName));

					Path outputFile = Utils.createFolderIfNotExist(System.getProperty("user.home"), "desktop", lowerFirst,
							file.getFileName().toString().replace(NAME, objName));

					Files.write(outputFile, content.getBytes("UTF-8"), StandardOpenOption.CREATE);
					System.out.println("File Generated at: " + outputFile);
				} catch (IOException e) {
					System.out.println("Failed to read " + file + " contents.");
				}
			}

			packageText.setText("");
			objectName.setText("");

			packageText.requestFocus();
		}
	}
}

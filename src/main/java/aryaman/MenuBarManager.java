package aryaman;

import org.fife.ui.rsyntaxtextarea.*;

import javax.swing.*;
import java.io.*;

public class MenuBarManager {
    private JFrame frame;
    private RSyntaxTextArea textArea;
    private JTextArea consoleArea;

    public MenuBarManager(JFrame frame, RSyntaxTextArea textArea, JTextArea consoleArea) {
        this.frame = frame;
        this.textArea = textArea;
        this.consoleArea = consoleArea;
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);

        JMenu runMenu = new JMenu("Run");
        JMenuItem compileRun = new JMenuItem("Compile & Run");
        runMenu.add(compileRun);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem findReplace = new JMenuItem("Find & Replace");
        JMenuItem search = new JMenuItem("Search");
        JMenuItem goToLine = new JMenuItem("Go to Line");
        editMenu.add(findReplace);
        editMenu.add(search);
        editMenu.add(goToLine);

        JMenu languageMenu = new JMenu("Language");
        JMenuItem javaLanguage = new JMenuItem("Java");
        JMenuItem pythonLanguage = new JMenuItem("Python");
        languageMenu.add(javaLanguage);
        languageMenu.add(pythonLanguage);

        menuBar.add(fileMenu);
        menuBar.add(runMenu);
        menuBar.add(editMenu);
        menuBar.add(languageMenu);

        newFile.addActionListener(e -> textArea.setText(""));
        openFile.addActionListener(e -> openFile());
        saveFile.addActionListener(e -> saveFile());
        compileRun.addActionListener(e -> compileAndRun());
        findReplace.addActionListener(e -> findReplace());
        search.addActionListener(e -> search());
        goToLine.addActionListener(e -> goToLine());
        javaLanguage.addActionListener(e -> textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA));
        pythonLanguage.addActionListener(e -> textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON));

        return menuBar;
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                textArea.read(br, null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                textArea.write(bw);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void compileAndRun() {
        String code = textArea.getText();
        consoleArea.setText(""); // Clear the console area
        try {
            String className = extractClassName(code);
            if (className == null) {
                consoleArea.append("Error: No public class found in the code.\n");
                return;
            }

            File tempFile = new File(System.getProperty("java.io.tmpdir"), className + ".java");
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
                bw.write(code);
            }

            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", tempFile.getAbsolutePath());
            compileProcessBuilder.redirectErrorStream(true);
            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor();
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            consoleArea.append(output.toString());

            if (compileProcess.exitValue() == 0) {
                consoleArea.append("Compilation successful.\n");

                ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "-cp", tempFile.getParent(), className);
                runProcessBuilder.redirectErrorStream(true);
                Process runProcess = runProcessBuilder.start();
                runProcess.waitFor();

                output.setLength(0);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }
                consoleArea.append(output.toString());
            } else {
                consoleArea.append("Compilation failed.\n");
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            consoleArea.append("Exception occurred: " + ex.getMessage() + "\n");
        }
    }

    private String extractClassName(String code) {
        String[] lines = code.split("\\r?\\n");
        for (String line : lines) {
            if (line.startsWith("public class ")) {
                String[] tokens = line.split(" ");
                return tokens[2].split("\\{")[0];
            }
        }
        return null;
    }

    private void findReplace() {
        String find = JOptionPane.showInputDialog(frame, "Find:");
        if (find == null || find.isEmpty()) {
            return;
        }
        String replace = JOptionPane.showInputDialog(frame, "Replace with:");
        if (replace == null) {
            return;
        }
        String content = textArea.getText();
        content = content.replace(find, replace);
        textArea.setText(content);
    }

    private void search() {
        String searchTerm = JOptionPane.showInputDialog(frame, "Search:");
        if (searchTerm == null || searchTerm.isEmpty()) {
            return;
        }
        String content = textArea.getText();
        int index = content.indexOf(searchTerm);
        if (index >= 0) {
            textArea.setCaretPosition(index);
            textArea.requestFocus();
            textArea.select(index, index + searchTerm.length());
        } else {
            JOptionPane.showMessageDialog(frame, "Text not found.");
        }
    }

    private void goToLine() {
        String lineStr = JOptionPane.showInputDialog(frame, "Go to Line:");
        if (lineStr == null || lineStr.isEmpty()) {
            return;
        }
        try {
            int lineNumber = Integer.parseInt(lineStr);
            textArea.setCaretPosition(textArea.getDocument().getDefaultRootElement().getElement(lineNumber - 1).getStartOffset());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid line number.");
        }
    }
}

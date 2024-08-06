package aryaman;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class IDE {
    private JFrame frame;
    private RSyntaxTextArea textArea;
    private JTextArea consoleArea;

    public IDE() {
        frame = new JFrame("IDE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        createMenuBar();
        createTextArea();
        createConsoleArea();

        frame.setVisible(true);
    }

    private void createMenuBar() {
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

        frame.setJMenuBar(menuBar);

        newFile.addActionListener(e -> textArea.setText(""));
        openFile.addActionListener(e -> {
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
        });
        saveFile.addActionListener(e -> {
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
        });
        compileRun.addActionListener(e -> {
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
        });

        findReplace.addActionListener(e -> {
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
        });

        search.addActionListener(e -> {
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
        });

        goToLine.addActionListener(e -> {
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
        });

        javaLanguage.addActionListener(e -> textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA));
        pythonLanguage.addActionListener(e -> textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON));
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

    private void createTextArea() {
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setLineNumbersEnabled(true);  // Enable line numbers
        frame.add(sp, BorderLayout.CENTER);
    }

    private void createConsoleArea() {
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        scrollPane.setPreferredSize(new Dimension(frame.getWidth(), 100));
        frame.add(scrollPane, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(IDE::new);
    }
}

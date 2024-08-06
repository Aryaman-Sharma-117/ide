package aryaman;

import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class RunMenuManager {
    private RSyntaxTextArea textArea;
    private JTextArea consoleArea;

    public RunMenuManager(RSyntaxTextArea textArea, JTextArea consoleArea) {
        this.textArea = textArea;
        this.consoleArea = consoleArea;
    }

    public JMenu createRunMenu() {
        JMenu runMenu = new JMenu("Run");
        JMenuItem compileRun = new JMenuItem("Compile & Run");

        runMenu.add(compileRun);

        compileRun.addActionListener(new CompileRunActionListener());

        return runMenu;
    }

    private class CompileRunActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
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
    }
}

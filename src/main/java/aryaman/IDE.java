package aryaman;

import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


public class IDE {
    private JFrame frame;
    private RSyntaxTextArea textArea;
    private JTextArea consoleArea;
    private TextAreaManager textAreaManager;
    private MenuBarManager menuBarManager;
    private ConsoleAreaManager consoleAreaManager;

    public IDE() {
        frame = new JFrame("IDE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        textAreaManager = new TextAreaManager(frame);
        textArea = textAreaManager.createTextArea();

        consoleAreaManager = new ConsoleAreaManager(frame);
        consoleArea = consoleAreaManager.createConsoleArea();

        menuBarManager = new MenuBarManager(frame, textArea, consoleArea);
        frame.setJMenuBar(menuBarManager.createMenuBar());

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(IDE::new);
    }
}

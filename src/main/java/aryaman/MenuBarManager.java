package aryaman;

import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

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

        FileMenuManager fileMenuManager = new FileMenuManager(frame, textArea);
        RunMenuManager runMenuManager = new RunMenuManager(textArea, consoleArea);
        EditMenuManager editMenuManager = new EditMenuManager(frame, textArea);
        LanguageMenuManager languageMenuManager = new LanguageMenuManager(textArea);

        menuBar.add(fileMenuManager.createFileMenu());
        menuBar.add(runMenuManager.createRunMenu());
        menuBar.add(editMenuManager.createEditMenu());
        menuBar.add(languageMenuManager.createLanguageMenu());

        return menuBar;
    }
}

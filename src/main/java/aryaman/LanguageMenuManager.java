package aryaman;

import org.fife.ui.rsyntaxtextarea.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LanguageMenuManager {
    private RSyntaxTextArea textArea;

    public LanguageMenuManager(RSyntaxTextArea textArea) {
        this.textArea = textArea;
    }

    public JMenu createLanguageMenu() {
        JMenu languageMenu = new JMenu("Language");
        JMenuItem javaLanguage = new JMenuItem("Java");
        JMenuItem pythonLanguage = new JMenuItem("Python");

        languageMenu.add(javaLanguage);
        languageMenu.add(pythonLanguage);

        javaLanguage.addActionListener(new LanguageActionListener(SyntaxConstants.SYNTAX_STYLE_JAVA));
        pythonLanguage.addActionListener(new LanguageActionListener(SyntaxConstants.SYNTAX_STYLE_PYTHON));

        return languageMenu;
    }

    private class LanguageActionListener implements ActionListener {
        private String syntaxStyle;

        public LanguageActionListener(String syntaxStyle) {
            this.syntaxStyle = syntaxStyle;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            textArea.setSyntaxEditingStyle(syntaxStyle);
        }
    }
}

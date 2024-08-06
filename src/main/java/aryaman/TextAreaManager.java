package aryaman;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

import javax.swing.*;
import java.awt.*;

public class TextAreaManager {
    private JFrame frame;

    public TextAreaManager(JFrame frame) {
        this.frame = frame;
    }

    public RSyntaxTextArea createTextArea() {
        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setLineNumbersEnabled(true);  // Enable line numbers
        frame.add(sp, BorderLayout.CENTER);
        return textArea;
    }
}

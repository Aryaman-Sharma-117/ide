package aryaman;

import javax.swing.*;
import java.awt.*;

public class ConsoleAreaManager {
    private JFrame frame;

    public ConsoleAreaManager(JFrame frame) {
        this.frame = frame;
    }

    public JTextArea createConsoleArea() {
        JTextArea consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        scrollPane.setPreferredSize(new Dimension(frame.getWidth(), 100));
        frame.add(scrollPane, BorderLayout.SOUTH);
        return consoleArea;
    }
}

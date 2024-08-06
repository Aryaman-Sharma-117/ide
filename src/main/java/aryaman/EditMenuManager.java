package aryaman;

import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditMenuManager {
    private JFrame frame;
    private RSyntaxTextArea textArea;

    public EditMenuManager(JFrame frame, RSyntaxTextArea textArea) {
        this.frame = frame;
        this.textArea = textArea;
    }

    public JMenu createEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        JMenuItem findReplace = new JMenuItem("Find & Replace");
        JMenuItem search = new JMenuItem("Search");
        JMenuItem goToLine = new JMenuItem("Go to Line");

        editMenu.add(findReplace);
        editMenu.add(search);
        editMenu.add(goToLine);

        findReplace.addActionListener(new FindReplaceActionListener());
        search.addActionListener(new SearchActionListener());
        goToLine.addActionListener(new GoToLineActionListener());

        return editMenu;
    }

    private class FindReplaceActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
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
    }

    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
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
    }

    private class GoToLineActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
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
}

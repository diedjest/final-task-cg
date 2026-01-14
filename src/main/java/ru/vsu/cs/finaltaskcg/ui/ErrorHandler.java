package ru.vsu.cs.finaltaskcg.ui;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorHandler {
    public static void showErrorDialog(JFrame parent, String title, String message, Exception exception) {
        String errorMessage = message;
        if (exception != null) {
            errorMessage += "\n\nДетали ошибки:\n" + getStackTrace(exception);
        }

        JTextArea textArea = new JTextArea(errorMessage);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        JOptionPane.showMessageDialog(parent, scrollPane, title, JOptionPane.ERROR_MESSAGE);
    }

    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
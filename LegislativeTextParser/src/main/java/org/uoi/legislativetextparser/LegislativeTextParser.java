package org.uoi.legislativetextparser;

import org.uoi.legislativetextparser.gui.MainMenu;
import javax.swing.*;

/**
 * Main class to start the application.
 */
public class LegislativeTextParser {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.createMainMenu();
        });
    }
}
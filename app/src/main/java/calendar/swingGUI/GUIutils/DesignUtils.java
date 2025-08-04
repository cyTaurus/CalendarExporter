package calendar.swingGUI.GUIutils;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatDarkLaf;

public class DesignUtils {

    public static void flatLaf() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());        //laden des LookAndFeel
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    
}

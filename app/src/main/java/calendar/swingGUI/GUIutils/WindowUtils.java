package calendar.swingGUI.GUIutils;

import calendar.swingGUI.About;
import calendar.swingGUI.Settings;
import calendar.swingGUI.MainWindow;

public class WindowUtils {

    // ---- Settings öffnen ---- // 
    public static void openSettings(MainWindow window) {
        Settings settings = window.getSettingsWindow();

        if (settings == null || !settings.isDisplayable()) { //wenn die Einstellungen noch nicht geöffnet wurden / geschlossen wurden
            settings = new Settings(window);                 //öffne ein neues Einstellungsfenster
            window.setSettingsWindow(settings);
        } else {                                             //ansonsten hole das offene Fenster in den Fokus nach vorne
            settings.toFront();
            settings.requestFocus();
        }
    }

    // ---- About öffnen ---- // 
     public static void openAbout(MainWindow window) {      //analog für das Aboutfenster
        About about = window.getAboutWindow();

        if (about == null || !about.isDisplayable()) {
            about = new About();
            window.setAboutWindow(about);
        } else {
            about.toFront();
            about.requestFocus();
        }
    }
    
    
}

package calendar.swingGUI.GUIutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import calendar.swingGUI.MainWindow;

public class FileUtils {

    //******************//
    //  FILE CHOOSER    //
    //******************//

    public static String customSaveFile(MainWindow window) { 
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select storage location");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV-Dateien","csv");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(window); 

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            String filePath = saveFile.getAbsolutePath();

            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }
            TableUtils.saveTable(window, window.getEventTable(), filePath); //Problem
            window.setLastPath(filePath);
            writeLastPath(filePath);
            return filePath;
            
        } 
        return null; //Abbruch
    }

    //******************//
    //  WRITE AND READ  //
    //******************//

    // ---- speichert Pfad zur .CSV-Datei ---- //
    public static void writeLastPath(String path) {
        File dir = new File("data");            //aus Gründen der Übersichtlichkeit soll der Pfad unter einem "data"-Ordner gespeichert werden
        if (!dir.exists()) {                              //existiert solch ein Ordner nicht, wird er erstellt
            dir.mkdirs();
        }

        File lastPathFile = new File(dir, "lastpath.txt");  //die Datei, in der der Pfad gespeichert wird


        try (BufferedWriter pathWrite = new BufferedWriter(new FileWriter(lastPathFile))) {
            pathWrite.write(path);
        } catch (IOException e) {
        e.printStackTrace();
    }  
    } 


    // ---- laden der .CSV-Datei ---- //
    public static String readLastPath() {
        File lastPathFile = new File("data/lastpath.txt");
        if (!lastPathFile.exists()) return null;

        try (BufferedReader pathReader = new BufferedReader(new FileReader(lastPathFile))) {
            return pathReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ---- vor dem Verlassen speichern? ---- //
    public static void confirmExit(MainWindow window) {
        String path = window.getLastPath();
        
        if (!window.getUnsavedChanges()) {              //wenn es keine ungespeicherten Änderungen gibt, einfach beenden
            System.exit(0);
            return;
        }

        int confirm = JOptionPane.showOptionDialog(null, "You have unsaved changes. Save changes before exiting?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[] {"Save", "Don't Save", "Cancel"}, "Save");
        //Option: Speichern
        if (confirm == 0) {
            if (path != null) {
                TableUtils.saveTable(window, window.getEventTable(), path);
            }
            System.exit(0);                     //nach dem Speichern kann das Programm schließen
        //Option: nicht speichern und schließen
        } else if (confirm == 1) { 
            System.exit(0);
        //Option: Cancel
        } else {
            //nichts tun, im Programm bleiben
        }
    }
    
}

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

        //Dateien sollen als .CSV gespeichert werden
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV-Dateien","csv");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(window); 

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            String filePath = saveFile.getAbsolutePath();

            //wenn bei Namenseingabe nicht explizit .csv hintendran geschrieben wird
            if (!filePath.endsWith(".csv")) {
                //Dateiendung anfügen
                filePath += ".csv";
            }
            //speichern der Datei
            TableUtils.saveTable(window, window.getEventTable(), filePath); 
            //speichern des Pfads zur Datei
            window.setLastPath(filePath);
            writeLastPath(filePath);
            return filePath;
            
        } 
        //bei Abbruch nicht speichern, FileChooser schließt
        return null; 
    }

    //******************//
    //  WRITE AND READ  //
    //******************//

    // ---- speichert Pfad zur .CSV-Datei ---- //
    public static void writeLastPath(String path) {
        //aus Gründen der Übersichtlichkeit soll der Pfad unter einem "data"-Ordner gespeichert werden
        File dir = new File("data");
        //existiert solch ein Ordner nicht, wird er erstellt            
        if (!dir.exists()) {                              
            dir.mkdirs();
        }

        //die Datei, in der der Pfad gespeichert wird
        File lastPathFile = new File(dir, "lastpath.txt");  

        //schreibt den Pfad der zuletzt gespeicherten Datei in lastpath.txt
        try (BufferedWriter pathWrite = new BufferedWriter(new FileWriter(lastPathFile))) {
            pathWrite.write(path);
        } catch (IOException e) {
        e.printStackTrace();
    }  
    } 


    // ---- laden der .CSV-Datei ---- //
    public static String readLastPath() {
        File lastPathFile = new File("data/lastpath.txt");
        //wenn lastpath.txt leer ist, gibt es keine zuletzt gespeicherte Datei
        //Methode verlassen, es wird nichts geladen
        if (!lastPathFile.exists()) return null;

        try (BufferedReader pathReader = new BufferedReader(new FileReader(lastPathFile))) {
            //falls die lastpath.txt beschrieben ist, die Datei Zeile für Zeile auslesen
            return pathReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ---- vor dem Verlassen speichern? ---- //
    public static void confirmExit(MainWindow window) {
        String path = window.getLastPath();
        
        //wenn es keine ungespeicherten Änderungen gibt, einfach beenden
        if (!window.getUnsavedChanges()) {              
            System.exit(0);
            return;
        }

        int confirm = JOptionPane.showOptionDialog(null, "You have unsaved changes. Save changes before exiting?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[] {"Save", "Don't Save", "Cancel"}, "Save");
        //Option: Speichern
        if (confirm == 0) {
            if (path != null) {
                TableUtils.saveTable(window, window.getEventTable(), path);
            }
            //nach dem Speichern kann das Programm schließen
            System.exit(0);                     
        //Option: nicht speichern und schließen
        } else if (confirm == 1) { 
            System.exit(0);
        //Option: Cancel
        } else {
            //nichts tun, im Programm bleiben
        }
    }

    //*************//
    //   IMPORT    //
    //*************//

    //importiere existierene .CSV-Datei, sd. man nicht gebunden ist an die zuletzt gespeicherte Datei
    public static void openCSV(MainWindow window) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose file to open");
        //suche nur nach .CSV-Dateien
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV-Dateien","csv");
        fileChooser.setFileFilter(filter);

        int selection = fileChooser.showOpenDialog(window);

        if (selection == JFileChooser.APPROVE_OPTION) {
            File chosenFile = fileChooser.getSelectedFile();
            String filePath = chosenFile.getAbsolutePath();

            //Tabelle laden, falls die gewählte Datei valide ist
            if (isValid(chosenFile, window)) {
                TableUtils.loadTable(window.getEventTable(), filePath);
                //neuen LastPath setzen
                window.setLastPath(filePath);
            } else {
                JOptionPane.showMessageDialog(window, "Invalid .csv file!", "Invalid file", JOptionPane.ERROR_MESSAGE);
            }
        }
    } 
    
    //überprüfe, ob die .CSV-Datei auch tatsächlich für dieses Programm gedacht ist
    public static boolean isValid(File file, MainWindow window) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            //prüft, ob der Header der geöffneten Datei dem erwarteten Header entspricht
            String[] expected = {"Ereignis", "Von", "Bis", "Beschreibung"};

            String firstLine = reader.readLine();

              //wenn es schon gar keinen Header gibt, ist die Datei nicht valide
              if (firstLine == null) {
                return false;
            }

            //Header korrekt nach Komma splitten
            String[] header = firstLine.split(",");

            //wenn sich die Datei im Header in irgendeiner Weise unterscheided, kann sie nicht valide sein
            //das gilt auch für andere Dateiformate, weil sie nach einem Komma nicht so wie bei CSV eine Spalte teilen

            if (header.length !=  expected.length) {
                JOptionPane.showMessageDialog(window, "Unexpected number of columns!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            for (int i = 0; i < expected.length; i++) {
                if (!header[i].equals(expected[i])) {
                    JOptionPane.showMessageDialog(window, "Invalid file!", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    //*****************//
    //  SAVE LOCATION  //
    //*****************//

    //entscheide, wie die Datei gespeichert werden soll
    public static void saveStorage(MainWindow window) {
        String path = window.getLastPath();

        if (path != null) {
            //wenn die Datei schon existiert, einfach überschreiben
            TableUtils.saveTable(window, window.getEventTable(), path);     
        } else {
            //neue Datei: öffne File Chooser
            String selectedPath = customSaveFile(window);  
            //speichern nur durchführen, wenn auch wirklich ein Pfad ausgewählt, d.h. nicht abgebrochen wurde o.ä.                 
            if (selectedPath != null) {                                     
                TableUtils.saveTable(window, window.getEventTable(), selectedPath);
            }
        
        }
    }
    
}

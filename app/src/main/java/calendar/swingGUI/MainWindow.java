package calendar.swingGUI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Dimension;
//import java.awt.event.*;
//import java.io.IOException;
import java.util.List;
import com.google.api.services.calendar.model.Event;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.util.Date;
import calendar.GoogleServices;

import java.awt.Component;


public class MainWindow extends JFrame {
    
    //-------------------------------------------//
    //               METHODEN                    //
    //-------------------------------------------//

    //********************//
    // Instanzvariablen   //
    //********************//

    //Tabelle
    private DefaultTableModel tableModel;
    private JTable eventTable;                  

    //besagt, dass noch kein Settings-Fenster geöffnet ist
    private Settings settingsWindow = null;
    //besagt, dass noch kein About-Fenster geöffnet ist
    private About aboutWindow = null;

    //********************//
    //      Settings      //
    //********************//

    //Settings öffnen. Es muss geprüft werden, ob ein Settings Window schon auf ist, ansonsten kann ein User die Fenster beliebig oft öffnen
    private void openSettings() {
        if (settingsWindow == null || !settingsWindow.isDisplayable()) {
            settingsWindow = new Settings(this);
        } else {
            settingsWindow.toFront();
            settingsWindow.requestFocus();
        }
    }

    //********************//
    //      About         //
    //********************//

    //About öffnen. Es muss geprüft werden, ob ein About Window schon auf ist, ansonsten kann ein User die Fenster beliebig oft öffnen
    private void openAbout() {
        if (aboutWindow == null || !aboutWindow.isDisplayable()) {
            aboutWindow = new About();
        } else {
            aboutWindow.toFront();
            aboutWindow.requestFocus();
        }
    }

    //********************//
    //      Tabelle       //
    //********************//

    //Anzeigen der Events in der Tabelle
    public void updateTable(List<Event> events) {
       DefaultTableModel model = (DefaultTableModel) eventTable.getModel();

        for (Event event : events) {
            //prüfe, ob start bzw. end ganztägig sind. Wenn ja: getDateTime. Wenn nein: getDate. 
            String start = (event.getStart().getDateTime() != null) ? event.getStart().getDateTime().toStringRfc3339() : event.getStart().getDate().toStringRfc3339();
            String end = (event.getEnd().getDateTime() != null) ? event.getEnd().getDateTime().toStringRfc3339() : event.getEnd().getDate().toStringRfc3339();
            //prüfe, ob es eine Summary bzw. eine Description gibt. Wenn ja: Summary bzw. Description wird geholt. Wenn nein: schreibe einen leeren String (Vermeidung NullPointerExceptions)
            String summary = event.getSummary() != null ? event.getSummary() : "";
            String description = event.getDescription() != null ? event.getDescription() : "";
            //neue Zeile mit Startdatum, Enddatum, Summary und Description zur Tabelle hinzufügen
            model.addRow(new Object[] {start, end, summary, description});
        }
    } 

    //Methode, um die Events aus dem Kalender zu holen
    public void fetchData(String calendarId, Date start, Date end) {
        try {
            //Zugang zu Google Kalender
            Calendar service = GoogleServices.getCalendarService();
            //Umwandlung der Daten in DateTime, sd. die API sie verwenden kann
            DateTime startDate = new DateTime(start);
            DateTime endDate = new DateTime(end);

            //holt alle Events basierend auf den Übergabewerten
            List<Event> events = GoogleServices.fetchEvents(service, calendarId, startDate, endDate);
            //Übergabe der Daten an updateTable
            updateTable(events);
        } catch (GoogleJsonResponseException e) {
            //prüfe auf invalide Kalender-ID
            if (e.getStatusCode() == 404) {
                JOptionPane.showMessageDialog(null, "Calendar-ID is not valid!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //*********************//
    // Speichern und Laden //
    //*********************//

    //Tabelleninhalt speichern 
    public void saveTable(JTable table, String filePath) {
    //öffne die Datei zum Schreiben, sowie die Spalten und Zeilen aus der Tabelle
    try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath))) {
        TableModel model = table.getModel();
        int columnCount = model.getColumnCount();
        int rowCount = model.getRowCount();

        //speichert die Überschriften der Spalten
        for (int i = 0; i < columnCount; i++) {
            fileWriter.write(model.getColumnName(i));
            if (i < columnCount - 1) fileWriter.write(",");
        }
        fileWriter.newLine();

        //Iteration über jede Zelle in der Tabelle
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                //holt den Inhalt einer Zelle. Wenn sie leer ist (zB bei Beschreibungen), dann speichere darin einen leeren String (um NullPointerExceptions zu vermeiden)
                Object value = model.getValueAt(row, col);
                String safe = (value != null) ? value.toString() : "";  
                //Wenn in der Zelle ein Komma steht, dann ...
                if (safe.contains(",")) {
                    safe = "\"" + safe.replace("\"", "\"\"") + "\"";
                }
                //schreibt den Zelleninhalt in die Datei. Am Ende jeder Zelle (bis auf die letzte Spalte) wird ein Komma eingefügt für erhöhte Lesbarkeit
                fileWriter.write(safe);
                if (col < columnCount - 1) fileWriter.write(",");
            }
            fileWriter.newLine();
        }

        System.out.println("Table saved in: " + filePath);
    } catch (IOException e) {
        e.printStackTrace();
    }
}


//lädt gespeicherte Daten
public void loadTable(JTable table, String filePath) {
        //prüfe, ob die Datei existiert. Wenn nicht, einfach Methode beenden.
        File file = new File(filePath);
        if (!file.exists()) return;

        //öffne und lese die Datei
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            //hole die tabelle
            DefaultTableModel model = (DefaultTableModel) eventTable.getModel();
            //model.setRowCount(0);
            //eine Zeile in der Datei 
            String line;
            //Spaltenüberschrift
            boolean firstLine = true;

            //lese jede Zeile, bis es keine mehr gibt
            while ((line = reader.readLine()) != null) {
                //Spaltenüberschrift muss nicht extra nochmal geladen werden, diese ist bei der Tabelle fest mit dabei. firstLine ist damit "abgehakt"
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                //jede Zeile in der CSV-Datei wird nach einem Komma gespalten. Die -1 ist nötig, damit auch leere Zellen beibehalten werden
                String[] values = line.split(",", -1); 
                //prüfe, ob weniger Spalten befüllt sind, als die erwarteten vier
                if (values.length < model.getColumnCount()) {
                    //erstelle ein Array mit der passenden Spaltenanzahl
                    String[] fill = new String[model.getColumnCount()];
                    //kopiere alle Werte in das Array fill
                    System.arraycopy(values, 0, fill, 0, values.length);
                    //füge die gefüllte Zeile zu der Tabelle hinzu
                    model.addRow(fill);
                } else {
                //wenn alle Spalten befüllt sind, kann man die Werte direkt in die Zeile einfügen
                model.addRow(values);
                }
            }
            System.out.println("Table loaded!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //*****************//
   //  File Chooser  //
   //****************//

   //der Nutzer soll den Speicherort und Namen der Datei selber wählen können
   public String customSaveFile(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select storage location");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV-Dateien","csv");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(parent); 

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            String filePath = saveFile.getAbsolutePath();

            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }
            saveTable(eventTable, filePath);
            writeLastPath(filePath);
            return filePath;
            
        } 
        return null; //Abbruch
    }

   //*****************//
   //  Path to File  //
   //****************//

    //Speichern des vom Nutzer gewähltem Pfad zur CSV Datei
    public void writeLastPath(String path) {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File lastPathFile = new File(dir, "lastpath.txt");


        try (BufferedWriter pathWrite = new BufferedWriter(new FileWriter(lastPathFile))) {
            pathWrite.write(path);
        } catch (IOException e) {
        e.printStackTrace();
        }  
    } 

    //laden der CSV Datei vom gespeicherten Pfad
    public String readLastPath() {
        File lastPathFile = new File("data/lastpath.txt");
        if (!lastPathFile.exists()) return null;

        try (BufferedReader pathReader = new BufferedReader(new FileReader(lastPathFile))) {
            return pathReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //-------------------------------------------//
    //               HAUPTFENSTER                //
    //-------------------------------------------//

    public MainWindow() {
        this.setTitle("Calendar-Exporter");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(900,500));
        this.setBackground(Color.DARK_GRAY);
        this.setLocationRelativeTo(null);

    //-------------------------------------------//
    //             MENÜ (APPLICATION)            //
    //-------------------------------------------//

        //Menüleiste (allgemeingültig für alle Menüeinträge)
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(Color.BLUE);
        menuBar.setPreferredSize(new Dimension(900, 90));

        //Menü (Application)
        JMenu menuApp = new JMenu("Application");
        menuBar.add(menuApp);

        //Menü zu Settings
        JMenuItem settings = new JMenuItem("Settings");
        settings.addActionListener(e -> openSettings()); //beim Settings-Feld einen Action Listener registrieren
        menuApp.add(settings);

    //-------------------------------------------//
    //                MENÜ (HELP)                //
    //-------------------------------------------//

        //Menü (Help)
        JMenu menuHelp = new JMenu("Help");
        menuBar.add(menuHelp);

        //Menü zu About
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> openAbout()); 
        menuHelp.add(about);

    //-------------------------------------------//
    //                 TABELLE                   //
    //-------------------------------------------//

        tableModel = new DefaultTableModel(new Object[]{"Ereignis", "Von", "Bis","Beschreibung"},0);
        eventTable = new JTable(tableModel);
        eventTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); //funktioniert noch nicht :/
        
        //Tabellendaten aus SavedEvents.csv laden
        String filePath = "data/SavedEvents.csv";
        loadTable(eventTable, filePath);

        //Buttons
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveTable(eventTable, filePath));
        this.add(saveButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> dispose());
        this.add(exitButton);
        

    //-------------------------------------------//
    //               HAUPTFENSTER                //
    //-------------------------------------------//

        //MainWindow zusammenbauen
        this.setLayout(new FlowLayout());
        this.add(new JScrollPane(eventTable));
        this.setJMenuBar(menuBar);
        

        //MainWindow ausgeben
        this.pack();
        this.setVisible(true);
    }

}

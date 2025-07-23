package calendar.swingGUI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import com.google.api.services.calendar.model.Event;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import calendar.GoogleServices;

//TO DO:
//alles nötige kommentieren
//Überprüfe, ob ich this.set nicht ohne this schreiben kann
//ZWEITES MENÜ: File, mit Save&Exit bzw. würd ich es eig. lieber mit Buttons machen... 
//Starten im Fullscreen / öffnen in Mitte des Bildschirms
//evtl. Settings & About öffnen im selben Fenster wie MainWindow statt öffnen separater Fenster (dann braucht man die methoden nicht mehr)
// Tabelle befüllen lassen durch User
//Design! aber erst als letztes :(
//OPTIONAL:
//Settings.java: Input-Validierung
//Settings.java: Datumseingabe mit JSpinner
//Einstellungen speichern und beim nächsten öffnen des Programms laden

public class MainWindow extends JFrame {
    
    //-------------------------------------------//
    //               METHODEN                    //
    //-------------------------------------------//


    //Tabelle Instanzariablen
    private DefaultTableModel tableModel;
    private JTable eventTable;                  //???

    //besagt, dass noch kein Settings-Fenster geöffnet ist
    private Settings settingsWindow = null;
    //besagt, dass noch kein About-Fenster geöffnet ist
    private About aboutWindow = null;

    //Settings öffnen. Es muss geprüft werden, ob ein Settings Window schon auf ist, 
    //ansonsten kann ein User die Fenster beliebig oft öffnen (schlecht :) )
    private void openSettings() {
        if (settingsWindow == null || !settingsWindow.isDisplayable()) {
            settingsWindow = new Settings(this);
        } else {
            settingsWindow.toFront();
            settingsWindow.requestFocus();
        }
    }

    //About öffnen. Es muss geprüft werden, ob ein About Window schon auf ist, 
    //ansonsten kann ein User die Fenster beliebig oft öffnen (schlecht :) )
    private void openAbout() {
        if (aboutWindow == null || !aboutWindow.isDisplayable()) {
            aboutWindow = new About();
        } else {
            aboutWindow.toFront();
            aboutWindow.requestFocus();
        }
    }

    public void updateTable(List<Event> events) {
        tableModel.setRowCount(0);

        for (Event event: events) {
            String title = event.getSummary();
            String description = event.getDescription();
            
            DateTime start = event.getStart().getDateTime();
            DateTime end = event.getEnd().getDateTime();

            //ganztägig
            if (start == null) {
                start = event.getStart().getDate();
            }
            if (end == null) {
                end = event.getEnd().getDate();
            }

            //Formatierung
            String startStr = start.toStringRfc3339();
            String endStr = end.toStringRfc3339();

            //Hinzufügen
            tableModel.addRow(new Object[]{title, startStr, endStr, description});
        }
    } 

    //Methode, um die Tabelle mit allen Events aus dem angegebenen Zeitraum zu befüllen
    public void calendarData(Calendar service, DateTime startDate, DateTime endDate) throws IOException{
        List<Event> events = GoogleServices.fetchEvents(service, startDate, endDate);
        updateTable(events);
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

        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Ereignis", "Von", "Bis","Beschreibung"},0);

        JTable table = new JTable(tableModel);

        //Position anpassen & Funktion geben!
        this.add(new JButton("Save"));
        this.add(new JButton("Exit"));

    //-------------------------------------------//
    //               HAUPTFENSTER                //
    //-------------------------------------------//

        //MainWindow zusammenbauen
        this.setLayout(new FlowLayout());
        this.add(new JScrollPane(table));
        this.setJMenuBar(menuBar);
        

        //MainWindow ausgeben
        this.pack();
        this.setVisible(true);
    }

}

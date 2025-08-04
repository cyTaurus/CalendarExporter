package calendar.swingGUI;

import java.io.File;

import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import calendar.swingGUI.GUIutils.*;


public class MainWindow extends JFrame {

    //********************//
    // Instanzvariablen   //
    //********************//

    // ---- Tabelle ---- //
    private DefaultTableModel tableModel;
    private JTable eventTable; 

    public JTable getEventTable() {                     
        return eventTable;
    }

    // ---- Settings ---- //
    private Settings settingsWindow = null;              //besagt, dass noch kein Settings-Fenster geöffnet ist

    public Settings getSettingsWindow() {               
        return settingsWindow;
    }

    public void setSettingsWindow(Settings window) {   
        this.settingsWindow = window;
    }

    // ---- About ---- //
    private About aboutWindow = null;                   //besagt, dass noch kein About-Fenster geöffnet ist

    public About getAboutWindow() {                               
        return aboutWindow;
    }

    public void setAboutWindow(About window) {                     
        this.aboutWindow = window;
    }

    // ---- Saved Path ---- //
    private String lastPath;                            //lastPath speichert den Pfad der zuletzt gespeicherten Datei

    public String getLastPath() {                               
        return lastPath;
    }

    public void setLastPath(String path) {                       
        this.lastPath = path;
    }

    // ---- Unsaved Changes ---- //
    public boolean unsavedChanges = false;              //beim Öffnen der Anwendung sind noch keine Änderungen aufgetreten, die man speichern müsste

    public boolean getUnsavedChanges() {                        
        return unsavedChanges;
    } 

    public void setUnsavedChanges(boolean unsavedChanges) {    
        this.unsavedChanges = unsavedChanges;
    }


    //-------------------------------------------//
    //               HAUPTFENSTER                //
    //-------------------------------------------//

    public MainWindow() {
        this.setTitle("Calendar-Exporter");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(900,500));
        this.setLocationRelativeTo(null);

    //**************//
    //     MENÜ     //
    //**************//

        //Menüleiste (allgemeingültig für alle Menüeinträge)
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setPreferredSize(new Dimension(900, 50));

        //Menü (Application)
        JMenu menuApp = new JMenu("Application");
        
        //Menü zu Settings
        JMenuItem settings = new JMenuItem("Settings");
        settings.addActionListener(e -> WindowUtils.openSettings(this)); //beim Settings-Feld einen Action Listener registrieren
        

        //Menü (Help)
        JMenu menuHelp = new JMenu("Help");
        
        //Menü zu About
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> WindowUtils.openAbout(this)); 

        //Menü (Import)
        JMenu menuImport = new JMenu("Import");

        //Menü zu Open file
        JMenuItem open = new JMenuItem("Open file");
        open.addActionListener(e -> FileUtils.openCSV(this));
        

    //**************//
    //   TABELLE    //
    //**************//

        tableModel = new DefaultTableModel(new Object[]{"Ereignis", "Von", "Bis","Beschreibung"},0);
        eventTable = new JTable(tableModel);
        TableUtils.render(eventTable);
        eventTable.setRowSelectionAllowed(true);
        eventTable.setColumnSelectionAllowed(false);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       
        
        //Tabellendaten laden aus letzter gespeicherter Datei
        lastPath = FileUtils.readLastPath();
        if (lastPath != null && new File(lastPath).exists()) {
            TableUtils.loadTable(eventTable, lastPath);
        } else {
            System.out.println("Keine gespeicherte Datei gefunden");
        }

        //Buttons
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e ->  FileUtils.saveStorage(this));

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> TableUtils.deleteRow(this));
        
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> FileUtils.confirmExit(this));
        
        

    //-------------------------------------------//
    //                 LAYOUT                    //
    //-------------------------------------------//

        this.setLayout(new BorderLayout());

        //GUI-Elemente

        //Menü
        this.setJMenuBar(menuBar);
        menuBar.add(menuApp);
        menuApp.add(settings);
        menuBar.add(menuImport);
        menuImport.add(open);
        menuBar.add(menuHelp);
        menuHelp.add(about);

        //Tabelle
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(eventTable);
        this.add(new JScrollPane(eventTable));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        //Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);


        //MainWindow ausgeben
        this.pack();
        this.setVisible(true);
    }

}

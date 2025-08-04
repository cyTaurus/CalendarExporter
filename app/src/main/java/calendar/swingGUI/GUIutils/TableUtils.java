package calendar.swingGUI.GUIutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import java.awt.Component;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import calendar.GoogleServices;
import calendar.swingGUI.MainWindow;

public class TableUtils {

    //****************//
    //  SAVE & LOAD   //
    //****************//

    // ---- Tabelleninhalt speichern mit Save-Button ---- //
    public static void saveTable(MainWindow window, JTable table, String filePath) {
    try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath))) {
        TableModel model = table.getModel();
        int columnCount = model.getColumnCount();
        int rowCount = model.getRowCount();

        
        for (int i = 0; i < columnCount; i++) {             //speichert die Überschriften der Spalten
            fileWriter.write(model.getColumnName(i));
            if (i < columnCount - 1) fileWriter.write(",");
        }
        fileWriter.newLine();

        
        for (int row = 0; row < rowCount; row++) {          //Iteration über jede Zelle in der Tabelle
            for (int col = 0; col < columnCount; col++) {
                Object value = model.getValueAt(row, col);
                String safe = (value != null) ? value.toString() : "";  
                //Wenn in der Zelle selbst ein Komma steht, dann...
                if (safe.contains(",")) {
                    safe = "\"" + safe.replace("\"", "\"\"") + "\"";
                }
                fileWriter.write(safe);
                if (col < columnCount - 1) fileWriter.write(",");
            }
            fileWriter.newLine();
        }

        System.out.println("Tabelle gespeichert in: " + filePath);
        window.setUnsavedChanges(false); 
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    // ---- Tabelle laden  ---- //
    public static void loadTable(JTable table, String filePath) {
    

        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",", -1); //leere Felder bleiben erhalten, zB die Beschreibung
                if (values.length < model.getColumnCount()) {
                    String[] fill = new String[model.getColumnCount()];
                    System.arraycopy(values, 0, fill, 0, values.length);
                    model.addRow(fill);
                } else {
                model.addRow(values);
                render(table);
                }
            }
            System.out.println("Table loaded!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---- Tabelle updaten mit neuen Events ---- //
     public static void updateTable(JTable eventTable, List<Event> events) {
        DefaultTableModel model = (DefaultTableModel) eventTable.getModel();

        for (Event event : events) {
            //prüfe, ob start bzw. end ganztägig sind. Wenn ja: getDateTime. Wenn nein: getDat
            String start = (event.getStart().getDateTime() != null) ? event.getStart().getDateTime().toStringRfc3339() : event.getStart().getDate().toStringRfc3339();
            String end = (event.getEnd().getDateTime() != null) ? event.getEnd().getDateTime().toStringRfc3339() : event.getEnd().getDate().toStringRfc3339();
            //prüfe, ob es eine Summary bzw. eine Beschreibung gibt. Wenn ja: Summary bzw. Beschreibung wird geholt. Wenn nein: schreibe einen leeren String
            String summary = event.getSummary() != null ? event.getSummary() : "";
            String description = event.getDescription() != null ? event.getDescription() : "";

            //Überprüfe auf schon vorhandene Events in der Tabelle
            boolean exists = false;

            for (int i = 0; i < model.getRowCount(); i++) {
                String summaryExists = (String) model.getValueAt(i, 0);
                String startExists = (String) model.getValueAt(i, 1);
                String endExists = (String) model.getValueAt(i, 2);
                String descExists = (String) model.getValueAt(i, 3);

                if (summary.equals(summaryExists) && start.equals(startExists) && end.equals(endExists) && description.equals(descExists)) {
                    exists = true;
                    break;
                }
            }
                if (!exists) {
                    model.addRow(new Object[] {summary, start, end, description});
                    render(eventTable);
                }
           
        }
    }

    // ---- holen der Daten ---- //
    public static void fetchData(MainWindow window, String calendarId, Date start, Date end) {
        try {
            //Zugang zu Google Kalender
            Calendar service = GoogleServices.getCalendarService();
            //Umwandlung der Daten in DateTime, sd. die API sie verwenden kann
            DateTime startDate = new DateTime(start);
            DateTime endDate = new DateTime(end);

            //holt alle Events basierend auf den Übergabewerten
            List<Event> events = GoogleServices.fetchEvents(service, calendarId, startDate, endDate);
            //Übergabe der Daten an updateTable
            updateTable(window.getEventTable(), events);
            window.setUnsavedChanges(true); 

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


    //****************//
    //    DELETE      //
    //****************//

    public static void deleteRow(MainWindow window) {
        JTable table = window.getEventTable();
        int selectedRow = table.getSelectedRow();
        
        //nachfragen, ob User auch wirklich löschen will
        if (selectedRow != -1) {
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected row?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.removeRow(selectedRow);
            window.setUnsavedChanges(true); 
        } else {
        //nichts tun eigentlich
        }
    }  else {
        //wenn Nutzer auf Delete drückt, ohne eine Reihe ausgewählt zu haben
        JOptionPane.showMessageDialog(window, "Please choose a row to delete", "Hint", JOptionPane.INFORMATION_MESSAGE);
        }
    } 
    
    //****************//
    //    RENDER      //
    //****************//

    //Tabelle Text Wrap

    private static class CellRenderer extends JTextArea implements TableCellRenderer {
        public CellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());

            setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);
            int preferredHeight = getPreferredSize().height;

            if (table.getRowHeight(row) != preferredHeight) {
            table.setRowHeight(row, preferredHeight);
        }
         return this;

        }

    }

    //wrapper
    public static void render(JTable table) {
        TableCellRenderer renderer = new CellRenderer();

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

}



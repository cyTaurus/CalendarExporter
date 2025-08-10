package calendar.swingGUI.GUIutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        //man braucht sowohl die ganze Tabelle als auch ihre Spalten und Zeilen, um jede Zelle der Tabelle speichern zu können
        TableModel model = table.getModel();
        int columnCount = model.getColumnCount();
        int rowCount = model.getRowCount();

        //speichert die Überschriften der Spalten
        for (int i = 0; i < columnCount; i++) {             
            fileWriter.write(model.getColumnName(i));
            //solange die Spalte nicht die letzte ist, setze ein Komma, sd. in neue Spalte geschrieben wird. Sonst KEIN komma setzen!
            //Kommas dienen bei CSV-Dateien als Spaltentrennung
            if (i < columnCount - 1) fileWriter.write(",");
        }
        fileWriter.newLine();

        //Iteration über jede Zelle in der Tabelle
        for (int row = 0; row < rowCount; row++) {          
            for (int col = 0; col < columnCount; col++) {
                //hole Wert aus Zelle
                Object value = model.getValueAt(row, col); 
                //wenn die Zelle befüllt ist, zu String formatieren, ansonsten leeren String einfügen. 
                //wenn eine Zelle leer bleibt, wird sie ignoriert. Dann gibt es eine Spalte weniger und führt zu einem ArrayOutOfBounds.
                String safe = (value != null) ? value.toString() : ""; 
                //Wenn in der Zelle selbst ein Komma steht, dann " " um Text setzen. 
                //Damit sorgt ein Komma nicht für einen falschen Split des Textes in eine neue Spalte
                if (safe.contains(",") || safe.contains("\"")) {
                    safe = "\"" + safe.replace("\"", "\"\"") + "\"";
                }
                //eigentliches schreiben, nachdem der Zelleninhalt in safe gespeichert wurde
                fileWriter.write(safe); 
                //solange die Spalte nicht die letzte ist, setze ein Komma, sd. in neue Spalte geschrieben wird. Sonst KEIN komma setzen!
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
    
        //Dateiobjekt für übergebenen Pfad
        File file = new File(filePath);
        //falls keine Datei zum laden existiert, Methode beenden
        if (!file.exists()) return;

        //lesen der Zeilen
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            //alter Tabelleninhalt wird gelöscht, bevor neue Daten geladen werden
            //damit vermeidet man, dass beim laden Termine einer offenen Datei hinzugefügt werden
            model.setRowCount(0);
            String line;
            //Header
            boolean header = true;

            //lese solange, bis keine Zeile mehr zum Einlesen vorhanden ist
            while ((line = reader.readLine()) != null) {
                //Header überspringen, da dieser ja sowieso schon immer vorhanden ist und nicht in die Tabelle selbst soll
                if (header) {
                    header = false;
                    continue;
                }
                //trenne Text bei Komma, aber NUR wenn es außerhalb von einem " "-Block steht
                //Bedingung: nach einem Komma folgt das Muster:
                // es folgt eine beliebige Anzahl von " - Paaren
                //es folgen bis zum Ende keine weiteren (einzelnen) " (sonst: ungerade Anzahl " -> kein gültiger Split-Punkt)
                //-------------------------------------------------------------------------------------------------------------------------------------------------
                // SPLIT: "a", "b", "c" (nach Komma 1 folgen 4 ", nach Komma 2 folgen 2 " -> geschlossener String -> Komma splittet)
                // KEIN SPLIT: "a", "b, c" (nach Komma 1 folgen 2 ", also nach a splitten, ABER nach Komma 2 folgt ein einzelnes " -> gehört zusammen, kein split)
                //-------------------------------------------------------------------------------------------------------------------------------------------------
                //-1 sorgt dafür, dass leere Spalte nicht verloren geht. Stattdessen kommt da ein leerer String rein.
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); 

                //sorgt beim laden für bessere Lesbarkeit
                for (int i = 0; i < values.length; i++) {
                    //entfernt äußere Anführungszeichen
                    //Zelleninhalt: hallo, "Welt"
                    //beim Speichern: "hallo, ""Welt"""
                    //beim laden: hallo, ""Welt""
                    values[i] = values[i].replaceAll("^\"|\"$", ""); 
                    //ersetzt doppelte Anführungszeichen mit nur einem
                    //also: hallo, "Welt"
                    values[i] = values[i].replace("\"\"", "\"");
                }

                //wenn nicht alle Zellen befüllt sind
                if (values.length < model.getColumnCount()) {
                    //erstelle ein Array 'fill' mit der korrekten Spaltenanzahl der Tabelle
                    String[] fill = new String[model.getColumnCount()];
                    //kopiere vorhandene Werte hinein, der Rest bleibt 0 -> leer
                    System.arraycopy(values, 0, fill, 0, values.length);
                    //das fill Array als Reihe hinzufügen
                    model.addRow(fill);
                } else {
                //wenn alle 4 Spalten befüllt sind, dann kann man direkt die Reihe hinzufügen
                model.addRow(values);
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
                //in der Tabelle existierende Events. Sie müssen so angepasst werden, dass KEINE Unterschiede zum tatsächlichen Zelleninhalt auftretten
                //d.h. Leerzeichen anfangs oder am Ende mit trim entfernen
                //ersetze doppelte Anführungszeichen durch ein einzelnes
                String summaryExists = ((String) model.getValueAt(i, 0)).trim().replaceAll("\"\"", "\"");
                String startExists = ((String) model.getValueAt(i, 1)).trim().replaceAll("\"\"", "\"");
                String endExists = ((String) model.getValueAt(i, 2)).trim().replaceAll("\"\"", "\"");   
                String descExists = ((String) model.getValueAt(i, 3)).trim().replaceAll("\"\"", "\"");

                //wenn ein einzufügendes Event übereinstimmt mit einem vorhandenen Event, überspringe das (erneute) Hinzufügen dieses Events
                //auch hier müssen die Werte in den 'originalen' Variablen, die Kommas und Anführungszeichen beinhalten genau wie oben ersetzt werden
                //ansonsten klappt der Vergleich mit equals nicht
                if (summary.trim().replaceAll("\"\"", "\"").equals(summaryExists) 
                    && prettyDate(start).trim().replaceAll("\"\"", "\"").equals(startExists) 
                    && prettyDate(end).trim().replaceAll("\"\"", "\"").equals(endExists) 
                    && description.trim().replaceAll("\"\"", "\"").equals(descExists)) {

                    exists = true;
                    break;
                }
                
            }
                    if (!exists) {
                    String prettyStart = prettyDate(start);
                    String prettyEnd = prettyDate(end);
                    model.addRow(new Object[] {summary, prettyStart, prettyEnd, description});
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

    // ---- neue Tabelle ---- //
    public static void newTable(MainWindow window, JTable table) {
            int option = JOptionPane.showConfirmDialog(null, "Create new file? Unsaved changes will be lost!", "Create new file",  JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            //Cancel
            if (option == 1) {
                return; //abbrechen 
            //speichern und neu     
        } else if (option == 0) {
            //saveTable(window, window.getEventTable(),window.getLastPath());
            TableUtils.clearTable(window, table);
        }     
}

    // ---- Tabelle leeren ---- //
    public static void clearTable(MainWindow window, JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        window.setLastPath(null); 
        window.setUnsavedChanges(true); 
    }


    //****************//
    //    DELETE      //
    //****************//

    public static void deleteRow(MainWindow window) {
        JTable table = window.getEventTable();
        int selectedRow = table.getSelectedRow();
        
        //nachfragen, ob User auch wirklich löschen will
        //wenn eine Zeile ausgewählt wurde, frage nach Bestätigung
        if (selectedRow != -1) {
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected row?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        //wird YES gewählt
        //dann wird die ausgewählte Reihe gelöscht
        if (response == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.removeRow(selectedRow);
            window.setUnsavedChanges(true); 
        } else {
        //nichts tun 
        }
    }  else {
        //wenn Nutzer auf Delete drückt, ohne eine Reihe ausgewählt zu haben
        JOptionPane.showMessageDialog(window, "Please choose a row to delete", "Hint", JOptionPane.INFORMATION_MESSAGE);
        }
    } 
    
    //****************//
    //    RENDER      //
    //****************//

    
    // --- Nested Class: Cell Renderer ---- //
    //CellRenderer erbt von TableCellRenderer
    private static class CellRenderer extends JTextArea implements TableCellRenderer {
        public CellRenderer() {
            //Zeilenumbruch
            setLineWrap(true);
            //Umbruch nicht mitten im Wort
            setWrapStyleWord(true);
            //wichtig fürs Erscheinen von Hintergrundfarbe beim Auswählen einer Zeile
            setOpaque(true);
        }

        //default Renderverhalten von getTableCellRenderer überschreiben mit custom Verhalten
        @Override
        //Aufruf Methode bei Anzeige einer Zelle
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            //wenn Zelle leer ist: leerer String, ansonsten zu String konvertieren
            setText(value == null ? "" : value.toString());

            //Farben bei Auswahl
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            setFont(table.getFont());

            //passt Zeilenhöhe an Textumbruch an
            //getPreferredHeight braucht die maximal verfügbare Spaltenbreite, um zu entscheiden, wo ein Umbruch passieren soll
            //bei keiner Angabe = unendlich viel Platz in einer Spalte -> kein Umbruch
            setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);
            //preferredHeight: wie hoch die Zelle sein muss, damit der Text trotz Umbruch sichtbar ist
            int preferredHeight = getPreferredSize().height;

            //wenn die Zeile nicht so hoch ist, wie sie sein müsste
            if (table.getRowHeight(row) != preferredHeight) {
            //passe an
            table.setRowHeight(row, preferredHeight);
        }
        //Rückgabe der 'korrekt' gerenderten Tabelle
         return this;

        }

    }

    // ---- Rendern der Tabelle, sd. Zellen bei viel Text breiter werden ---- //
    public static void render(JTable table) {
        TableCellRenderer renderer = new CellRenderer();

        //durch Spalten laufen und custom rendering anwenden
        //
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    //zum leserlichen Darstellen der Daten in der Tabelle
    private static String prettyDate(String input) {
        try {
            OffsetDateTime odt = OffsetDateTime.parse(input);
            return odt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } catch (DateTimeParseException e) {
            LocalDate ld = LocalDate.parse(input);
            return ld.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
    }
}



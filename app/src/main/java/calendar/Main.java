package calendar;


import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.swing.SwingUtilities;

import calendar.swingGUI.MainWindow;
import calendar.swingGUI.GUIutils.DesignUtils;


public class Main {
  public static void main(String[] args) throws IOException, GeneralSecurityException {


  //-------------------------------------------//
  //                 GUI                       //
  //-------------------------------------------//

    DesignUtils.flatLaf();

    //Swing MainWindow laden
    SwingUtilities.invokeLater(() -> new MainWindow());

  //-------------------------------------------//
  //               USER INPUT                  //
  //-------------------------------------------//

    //prüfe, ob alle Argumente korrekt eingegeben wurden 
    if (args.length < 3) {
      System.out.println("Syntax: java calendar.Main <Startdatum> <Enddatum> <Outputdatei> <Kalender-ID>");
      return;
    }

    //Argumente, die der Nutzer im Endeffekt eingeben kann: Startdatum, Enddatum, Ausgabedatei
    String startDateStr = args[0];
    String endDateStr = args[1];
    String outputFile = args[2];
    //wenn kein viertes Argument (die Kalender-ID) existiert, dann Id 'primary' (Hauptkalender) nutzen
    String calendarId = (args.length >= 4) ? args[3] : "primary";

  //-------------------------------------------//
  //            DIENSTE / ANDERES              //
  //-------------------------------------------//

    //Umwandeln der Daten in das DateTime-Format   
    DateTime timeMin = new DateTime(startDateStr + "T00:00:00Z");
    DateTime timeMax = new DateTime(endDateStr + "T23:59:59Z");


    //Kalenderdienst
    Calendar service = GoogleServices.getCalendarService();
    

    //Liste aller Events 
    List<Event> items = GoogleServices.fetchEvents(service, calendarId, timeMin, timeMax);
   
    //Schreiben der Events in die .ics Datei
    ICalExport.writeICal(items, outputFile);


  }
}
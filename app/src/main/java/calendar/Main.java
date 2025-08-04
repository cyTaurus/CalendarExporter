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

    DesignUtils.flatLaf();

  //-------------------------------------------//
  //                 SWING                     //
  //-------------------------------------------//

    //Swing MainWindow laden
    SwingUtilities.invokeLater(() -> new MainWindow());

  //-------------------------------------------//
  //               USER INPUT                  //
  //-------------------------------------------//

    //prüfe, ob alle Argumente korrekt eingegeben wurden
    if (args.length < 3) {
      System.out.println("Syntax: java Main <Startdatum> <Enddatum> <Outputdatei>");
      return;
    }

    //Argumente, die der Nutzer im Endeffekt eingeben kann: Startdatum, Enddatum, Ausgabedatei
    String startDateStr = args[0];
    String endDateStr = args[1];
    String outputFile = args[2];

  //-------------------------------------------//
  //            DIENSTE / ANDERES              //
  //-------------------------------------------//

    //Umwandeln der Daten in das DateTime-Format   
    DateTime timeMin = new DateTime(startDateStr + "T00:00:00Z");
    DateTime timeMax = new DateTime(endDateStr + "T23:59:59Z");
    String calendarId = "primary"; //später dynamisch statt hardcoded


    //Kalenderdienst
    Calendar service = GoogleServices.getCalendarService();
    

    //Liste aller Events 
    List<Event> items = GoogleServices.fetchEvents(service, calendarId, timeMin, timeMax);
   
    //Schreiben der Events in die .ics Datei
    ICalExport.writeICal(items, outputFile);


  }
}
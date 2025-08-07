package calendar;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

public class ICalExport {
   
    public static void writeICal(List<Event> events, String outputFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile)); 

//-------------------------------------------//
//                 WRITER                    //
//-------------------------------------------//

//folgt dem Schema von RFC2445

writer.write("BEGIN:VCALENDAR\r\n");
writer.write("VERSION:2.0\r\n");
writer.write("PRODID:-//SaJo//DE\r\n"); 


for (Event googleEvent : events) {                       //die Schleife durchläuft jedes Event im vom Nutzer angegebenen Zeitraum
writer.write("BEGIN:VEVENT\r\n");

DateTime start = googleEvent.getStart().getDateTime();   //holt das Datum und die Zeit des Events
if (start == null) {                                     //wenn start == null, dann ist das Event ganztägig und wir holen nur das Datum
  start = googleEvent.getStart().getDate();
}

writer.write("DTSTART:" + formatToICal(start) + "\r\n"); //das Datum wird mit der Hilfmethode formatiert, sodass die Google API die Zeitdaten nutzen kann

DateTime end = googleEvent.getEnd().getDateTime();
if (end == null) {                                       //wenn end == null, dann ist das Event ganztägig und wir holen nur das Datum
  end = googleEvent.getEnd().getDate();
}
writer.write("DTEND:" + formatToICal(end) + "\r\n");     //wie bei start

writer.write("SUMMARY: " + googleEvent.getSummary() + "\r\n"); //Titel

writer.write("END:VEVENT\r\n");
}

writer.write("END:VCALENDAR");


writer.close();

}

  //-------------------------------------------//
  //               METHODEN                    //
  //-------------------------------------------//

//Hilfsmethode zur korrekten Umformatierung der Zeitdaten, Datum Google API -> Format .ics-Standard
 private static String formatToICal(DateTime dt) {
        //Instant-Objekt (exakter Zeitpunkt) = Zeitstempel des Events in Millisekunden-Genauigkeit
        Instant instant = Instant.ofEpochMilli(dt.getValue());
        //Umwandlung in String nach .ics-Standard. UTC-Zeitzone muss nochmal spezifiziert werden, weil Instant keine Zeitzonen hat (ohne .withZone wird Systemzeit verwendet)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);
        return formatter.format(instant);
    }
}
    


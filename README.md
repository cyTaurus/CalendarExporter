# CalendarExporter
Der Kalender-Exporter ist ein Projekt, welches im Rahmen des Moduls "Programmieren in Java" enstanden ist.

---
## Funktionen: Terminal
Mit dem Kalender-Exporter kann man alle Ereignisse zwischen einem angegebenen Zeitraum von einem **Google-Kalender** in eine .ics-Datei speichern.<br>

***Syntax:*** gradlew run --args="Startdatum Enddatum OutputDatei.ics" <br>
(im Terminal im Verzeichnis, in dem auch gradlew liegt, ausführen!) <br> <br>
*Format Datum:* YYYY-MM-DD

## Funktionen: GUI
Es ist auch eine grafische Benutzeroberfläche vorhanden. 
Start der GUI mit dem Befehl *gradlew run* im korrekten Verzeichnis (s.o.).
Dort wird in einer Tabelle das Ereignis, das Startdatum und das Enddatum sowie eine Beschreibung (soweit vorhanden) angezeigt.

### Events hinzufügen
Mit einem Klick auf den Menüpunkt *Application > Settings* öffnet sich ein Fenster, in dem man die ID seines Google-Kalenders und das "Von" und "Bis"- Datum eingeben kann. Alle Events in diesem Zeitraum werden dann in die Tabelle im Hauptfenster eingefügt.

### Events löschen
Einfach auf die gewünschte Zeile klicken und dann den "Delete" - Button drücken.

### Datei speichern
Mit "Save" speichert man die Tabelle in einer .csv - Datei. Ist dass das erste Mal, dass eine Datei gespeichert wird, kann man den Speicherort und Namen der Datei aussuchen. Ansonsten wird eine schon existierende Datei überschrieben.

### Datei importieren
Man kann eine schon vorhandene Datei importieren und weiter bearbeiten. Dafür einfach auf *Import > Open file* drücken und die gewünschte Datei öffnen.

### Neue Datei erstellen
Möchte man an einer neuen Datei arbeiten, so kann man einfach auf "New" drücken.

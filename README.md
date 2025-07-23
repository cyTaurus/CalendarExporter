# CalendarExporter
Der Kalender-Exporter ist ein Projekt, welches im Rahmen des Moduls "Programmieren in Java" enstanden ist.

---
# Wichtige Information
Da das Google Cloud- Projekt nicht veröffentlicht wird, ist der Kalender-Exporter außerhalb des Rahmens der Uni eigentlich nicht verwendbar. Man müsste selbst ein Google Cloud-Projekt erstellen und die eigene credentials.json dann in den ressources-Ordern kopieren. :P

---
## Funktionen
Mit dem Kalender-Exporter kann man alle Ereignisse zwischen einem angegebenen Zeitraum von einem **Google-Kalender** in eine .ics-Datei speichern. 

*Syntax:* java Main <Startdatum> <Enddatum> <Outputdatei> **oder** gradlew run --args="Startdatum Enddatum OutputDatei.ics" (im Terminal im Verzeichnis, in dem auch gradlew liegt, ausführen!)
*Format Datum:* YYYY-MM-DD

Es ist auch eine grafische Benutzeroberfläche vorhanden. 
Start der GUI mit dem Befehl *gradlew run* im korrekten Verzeichnis (s.o.).
Dort wird in einer Tabelle das Ereignis, das Startdatum und das Enddatum sowie eine Beschreibung (soweit vorhanden) angezeigt.

---
## Hinzufügen von Events in der GUI
Mit einem Klick auf den Menüpunkt *Application > Settings* öffnet sich ein Fenster, in dem man die URL seines Google-Kalenders und das "Von" und "Bis"- Datum eingeben kann. Alle Events in diesem Zeitraum werden dann in die Tabelle im Hauptfenster eingefügt. 

*Android-App Funkzellenortung*


##### INFO #####


Die Android-App schreibt bei Nutzung des Mobilfunknetzes (Telefonie, SMS oder mobile Daten) den Zeitpunkt, die Richtung und den aktuellen Standort in eine CSV-Datei und soll dabei die gespeicherten Daten in den Datenbanken der Mobilfunkprovider simulieren. Der Standort besteht aus MCC, MNC, LAC und CellID der aktuell aktiven Funkzelle. Neben diesen Daten erfassen die Mobilfunkprovider auch Kenndaten der beteiligten Teilnehmer, wie z.B. Rufnummer. Diese Daten werden weder von der App abgerufen noch gespeichert, da der Fokus auf der Erstellung von Bewegungsprofilen für Bildungszwecke liegt. Fehlermeldungen in der CSV-Datei können durch unzureichende Berechtigungen der App, zu schnelle Funkzellenwechsel, Verbindungsabbruch und ab Android 8 durch Deaktivierung der Location Services (siehe Systemvoraussetzungen) begründet sein.
Die Android-App ist ein Bestandteil des Softwarepakets zur Visualisierung von Mobilfunkverbindungsdaten. Mehr Informationen auf adrian.henning-net.de/mobilfunk.


##### SYSTEMVORAUSETZUNGEN #####


* Android 4.3 (Jelly Bean) API 18
Ab Android 8.0 müssen Location Services über Mobilfunknetze aktiviert sein (Modus Battery Saving / Energiesparmodus)


##### BERECHTIGUNGEN #####


* Ungefährer Standort (ACCESS_COARSE_LOCATION) ist notwendig, um die Informationen über die Funkzelle abzurufen.
* Netzwerkstatus anzeigen (ACCESS_NETWORK_STATE) ist notwendig, um den Zustand der Internetverbindung zu prüfen.
* Telefonstatus lesen und identifizieren (READ_PHONE_STATE) ist notwendig, um den Zustand der Telefonverbindung zu prüfen.
* SMS oder MMS lesen (READ_SMS) ist notwendig, um auf ausgehende SMS zu prüfen.
* SMS empfangen (RECEIVE_SMS) ist notwendig, um auf eingehende SMS zu prüfen.
* Automatisch nach dem Booten starten (RECEIVE_BOOT_COMPLETED) ist notwendig, um das Tracken nach einem Neustart fortzusetzen.


###### KONTAKT ######


adrian.henning-net.de/mobilfunk


##### LIZENZ #####


Visualisierung von Mobilfunkverbindungsdaten darf unter der CC BY-SA 3.0 Lizenz (https://creativecommons.org/licenses/by-sa/3.0/) verwendet werden.

package de.henning_net.android.funkzellenortung;

/**
 * Erstellt von Adrian Henning im Frühling 2016 im Rahmen der Masterarbeit
 * "Entwicklung einer Visualisierungssoftware für Mobilfunkverbindungsdaten
 * zur Förderung Kompetenzen in einer digital geprägten Kultur innerhalb
 * einer Unterrichtsreihe im Fach Informatik" am Fachbereich Didaktik der
 * Informatik der Freien Universität Berlin. Die App ist ein Bestandteil
 * des Softwarepakets zur Visualiserung von Mobilfunkverbindungsdaten.
 * Mehr Informationen auf adrian.henning-net.de/mobilfunk.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import java.io.IOException;

public class Home extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) { // App wird geöffnet
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView status = (TextView)findViewById(R.id.status);
        if (isServiceRunning()) status.setText("Aktiviert"); // Funkzellenortung aktiv
        else status.setText("Deaktiviert"); // Funkzellenortung nicht aktiv
    }

    protected void onStart() { // App wird gestartet
        super.onStart();
        try {
            getAmount(); // Zähle erfasste Ortungen
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onRestart() {
        super.onRestart();
    }

    protected void onDestroy() { // Die Erfassung lebt im Service TrackService weiter
        super.onDestroy();
    }

    public void onStartClick(View v){ // Erfassung wird gestartet
        startService(new Intent(this, TrackService.class));  // Starte Service zum Starten der Überwachung
            TextView status = (TextView) findViewById(R.id.status);
            status.setText("Aktiviert");
    }

    public void onStopClick(View v){ // Erfassung wird gestoppt
        stopService(new Intent(this, TrackService.class)); // Stoppe Service zum Stoppen der Überwachung

        TextView status = (TextView)findViewById(R.id.status);
        status.setText("Deaktiviert");

        try {
            getAmount(); // Zähle erfasste Ortungen
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDataClick(View v){ // Datei anzeigen
        MyCSVReader myReader = new MyCSVReader(this); // Starte Reader zum CSV-Datei Anzeigen
        try {
            myReader.openFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onTextClick(View v){ // Auf Erklärtext geklickt
        MyCSVReader myReader = new MyCSVReader(this); // Starte Reader zum CSV-Datei Anzeigen
        try {
            myReader.openFolder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onReloadClick(View v) { // Anzeige aktualsieren
        try {
            getAmount(); // Zähle erfasste Ortungen
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onExportClick(View v){ // Datei exportieren
        MyCSVReader myReader = new MyCSVReader(this); // Starte Reader zum CSV-Datei Exportieren
        try {
            myReader.share();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getAmount() throws IOException { // Zähle wie viele Funkzellen bereits erfasst sind
        MyCSVReader myReader = new MyCSVReader(this); // Starte Reader zum CSV-Datei Einlesen
        String temp = myReader.getPath();
        TextView hint = (TextView) findViewById(R.id.hint);
        TextView counter = (TextView) findViewById(R.id.counter);
        hint.setText("Die Daten werden in der Datei "+myReader.getFilename()+" im Ordner "+myReader.getPath()+" gespeichert.");
        counter.setText(Integer.toString(myReader.readFile()));
    }

    private boolean isServiceRunning(){ // Läuft der Service im Hintergrund
        SharedPreferences settings = getSharedPreferences("settings", 0); // Status holen
        return (settings.getBoolean("running", false)); // Status ausgeben
    }
}

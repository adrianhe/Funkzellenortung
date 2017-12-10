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

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Home extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private View mLayout;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
    private static final int PERMISSION_ACCESS_NETWORK_STATE = 1;
    private static final int PERMISSION_READ_PHONE_STATE = 2;
    private static final int PERMISSION_READ_SMS = 3;
    private static final int PERMISSION_RECEIVE_SMS = 4;
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 5;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 6;
    private static final int PERMISSION_RECEIVE_BOOT_COMPLETED = 7;

    protected void onCreate(Bundle savedInstanceState) { // App wird geöffnet
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mLayout = findViewById(R.id.status);
        TextView status = (TextView)findViewById(R.id.status);
        if (isServiceRunning()) status.setText("Aktiviert"); // Funkzellenortung aktiv
        else status.setText("Deaktiviert"); // Funkzellenortung nicht aktiv
    }

    protected void onStart() { // App wird gestartet
        super.onStart();
        try {
            SharedPreferences settings = getSharedPreferences("settings", 0);
            SharedPreferences.Editor editor = settings.edit();
            if (!settings.contains("filepath")){ // einzigartigen aber beständigen Dateiname erzeugen
                String id = UUID.randomUUID().toString().substring(0,5); // Name auf 6 Zeichen kürzen
                String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                String dir = "/Funkzellenortung";
                String fileName = "Verbindungen_"+id+".csv";
                String filePath = baseDir + dir +  File.separator + fileName;
                editor.putString("filepath",filePath); // In Shared Pref speichern
                editor.putString("file",fileName); // In Shared Pref speichern
                editor.putString("dir",baseDir + dir); // In Shared Pref speichern
                File newDir = new File(baseDir + dir);
                newDir.mkdirs();
            }
            editor.apply();

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

    public void onStartClick(View v) { // Erfassung wird gestartet
        startTracking();
    }

    public void startTracking(){
        int error = 0;
        // Check if the permissions have been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
        } else {
            error++;
            // Permission is missing and must be requested.
            requestLocactionPermission();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
        } else {
            error++;
            // Permission is missing and must be requested.
            requestNetworkPermission();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
        } else {
            error++;
            // Permission is missing and must be requested.
            requestReadPhonePermission();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
        } else {
            error++;
            // Permission is missing and must be requested.
            requestReadSMSPermission();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
        } else {
            error++;
            // Permission is missing and must be requested.
            requestReceiveSMSPermission();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
        } else {
            error++;
            // Permission is missing and must be requested.
            requestReadStoragePermission();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
        } else {
            error++;
            // Permission is missing and must be requested.
            requestWriteStoragePermission();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
        } else {
            // Permission is missing and must be requested.
            requestBootPermission();
        }
        if (error == 0) {

            startService(new Intent(this, TrackService.class));  // Starte Service zum Starten der Überwachung
            TextView status = (TextView) findViewById(R.id.status);
            status.setText("Aktiviert");
        }
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSION_ACCESS_COARSE_LOCATION) {
            // Request for location permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Snackbar.make(mLayout, "Die App funktioniert nur mit allen Berechtigungen.",
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        }
        if (requestCode == PERMISSION_ACCESS_NETWORK_STATE) {
            // Request for location permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Snackbar.make(mLayout, "Die App funktioniert nur mit allen Berechtigungen.",
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        }
        if (requestCode == PERMISSION_READ_PHONE_STATE) {
            // Request for location permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Snackbar.make(mLayout, "Die App funktioniert nur mit allen Berechtigungen.",
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        }
        if (requestCode == PERMISSION_READ_SMS) {
            // Request for location permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Snackbar.make(mLayout, "Die App funktioniert nur mit allen Berechtigungen.",
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        }
        if (requestCode == PERMISSION_RECEIVE_SMS) {
            // Request for location permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Snackbar.make(mLayout, "Die App funktioniert nur mit allen Berechtigungen.",
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        }
        if (requestCode == PERMISSION_READ_EXTERNAL_STORAGE) {
            // Request for location permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Snackbar.make(mLayout, "Die App funktioniert nur mit allen Berechtigungen.",
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        }
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE) {
            // Request for location permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Snackbar.make(mLayout, "Die App funktioniert nur mit allen Berechtigungen.",
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        }
        if (requestCode == PERMISSION_RECEIVE_BOOT_COMPLETED) {
            // Request for location permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Snackbar.make(mLayout, "Die App funktioniert nur mit allen Berechtigungen.",
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    }

    private void requestLocactionPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "ACCESS_COARSE_LOCATION ist notwendig, um die Informationen über die Funkzelle abzurufen.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_ACCESS_COARSE_LOCATION);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting ACCESS_COARSE_LOCATION.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    private void requestNetworkPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_NETWORK_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "ACCESS_NETWORK_STATE ist notwendig, um den Zustand der Internetverbindung zu prüfen.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                            PERMISSION_ACCESS_NETWORK_STATE);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting ACCESS_NETWORK_STATE.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    private void requestReadPhonePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "READ_PHONE_STATE ist notwendig, um den Zustand der Telefonverbindung zu prüfen.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            PERMISSION_READ_PHONE_STATE);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting READ_PHONE_STATE.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_READ_PHONE_STATE);
        }
    }

    private void requestReadSMSPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_SMS)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "READ_SMS ist notwendig, um auf ausgehende SMS zu prüfen.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.READ_SMS},
                            PERMISSION_READ_SMS);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting READ_SMS.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS},
                    PERMISSION_READ_SMS);
        }
    }

    private void requestReceiveSMSPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECEIVE_SMS)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "RECEIVE_SMS ist notwendig, um auf eingehende SMS zu prüfen.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            PERMISSION_RECEIVE_SMS);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting RECEIVE_SMS.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                    PERMISSION_RECEIVE_SMS);
        }
    }

    private void requestReadStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "READ_EXTERNAL_STORAGE ist notwendig, um die CSV-Datei zu lesen.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_READ_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting READ_EXTERNAL_STORAGE.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_READ_EXTERNAL_STORAGE);
        }
    }

    private void requestWriteStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "WRITE_EXTERNAL_STORAGE ist notwendig, um in die CSV-Datei zu schreiben.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_WRITE_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting WRITE_EXTERNAL_STORAGE.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void requestBootPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECEIVE_BOOT_COMPLETED)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "RECEIVE_BOOT_COMPLETED ist notwendig, um das Tracken nach einem Neustart fortzusetzen.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                            PERMISSION_RECEIVE_BOOT_COMPLETED);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting RECEIVE_BOOT_COMPLETED.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    PERMISSION_RECEIVE_BOOT_COMPLETED);
        }
    }
}

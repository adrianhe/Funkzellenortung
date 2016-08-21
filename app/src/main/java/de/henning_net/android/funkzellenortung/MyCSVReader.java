package de.henning_net.android.funkzellenortung;

/**
 * Erstellt von Adrian Henning im Frühling 2016 im Rahmen der Masterarbeit
 * "Entwicklung einer Visualisierungssoftware für Mobilfunkverbindungsdaten
 * zur Förderung Kompetenzen in einer digital geprägten Kultur innerhalb
 * einer Unterrichtsreihe im Fach Informatik" am Fachbereich Didaktik der
 * Informatik der Freien Universität Berlin. Die App ist ein Bestandteil
 * des Softwarepakets zur Visualiserung von anfallenden Mobilfunkdaten.
 * Mehr Informationen auf adrian.henning-net.de/mobilfunk.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.webkit.MimeTypeMap;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MyCSVReader {
    private Context mContext;
    private String filePath;
    private String baseDir;
    private String dir;
    private String fileName;

    public String getPath(){
        return baseDir+dir;
    }

    public String getFilename(){
        return fileName;
    }


    public MyCSVReader(Context mContext){
        this.mContext = mContext;
        String id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID); // einzigartiger aber beständiger Dateiname
        id = id.substring(0,5); // Name auf 5 Zeichen kürzen
        baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        dir = "/Funkzellenortung";
        fileName = "Verbindungen_"+id+".csv";
        this.filePath = baseDir + dir +  File.separator + fileName;
    }

    public int readFile() throws IOException { // Anzahl gespeicherter Verbindungen erfassen
        int amount = 0;  // Anzahl der gespeicherten Dateieinträge
        File file = new File(filePath);
        // Existiert die Datei?
        if(file.exists() && !file.isDirectory()){
            FileReader myFileReader = new FileReader(filePath);
            CSVReader reader = new CSVReader(myFileReader, ',', CSVWriter.NO_QUOTE_CHARACTER, 1); // Header auslassen
            while ((reader.readNext()) != null) {
                    amount++; //Lese die Datei Zeile für Zeile und erhöhe amount
            }
            reader.close();
        }
        return amount;
    }

    public void openFile() throws IOException { // Datei mit Dateibetrachter öffnen
        if (readFile() > 0) { // Hat die Datei überhaupt Einträge?
            File file = new File(filePath);
            Uri fileUri = Uri.fromFile(file);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW); // Was soll gemacht werden?
            String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension); // Richtigen Dateityp wählen
            intent.setDataAndType(fileUri,mimetype);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) { // Wenn eine App existiert, die das Intent ausführen kann
                mContext.startActivity(Intent.createChooser(intent, "Datei öffnen")); // Datei öffnen
            } else {
                openFolder(); // Ansonsten versuchen den Ordner zu öffnen
            }
        }
    }

    public void openFolder() throws IOException { // Datei im Ordner anzeigen
        if (readFile() > 0) { // Hat die Datei überhaupt Einträge?
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW); // Was soll gemacht werden?
            final String folderPath = filePath.substring(0,filePath.lastIndexOf(File.separator)); // Ordner wählen
            File temp = new File(folderPath);
            Uri folderUri = Uri.fromFile(temp);
            intent.setDataAndType(folderUri, "resource/folder");
            if (intent.resolveActivity(mContext.getPackageManager()) != null) { // Wenn eine App existiert, die das Intent ausführen kann
                mContext.startActivity(Intent.createChooser(intent, "Ordner öffnen")); // Ordner öffnen
            }
        }
    }

    public void share() throws IOException { // Datei exportieren
        if (readFile() > 0) { // Hat die Datei überhaupt Einträge?
            File file = new File(filePath);
            Uri fileUri = Uri.fromFile(file);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND); // Was soll gemacht werden?
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension); // Richtigen Dateityp wählen
            shareIntent.setType(mimetype);
            if (shareIntent.resolveActivity(mContext.getPackageManager()) != null) { // Wenn eine App existiert, die das Intent ausführen kann
                mContext.startActivity(Intent.createChooser(shareIntent, "Daten exportieren")); // Datei exportieren
            }
        }
    }
}

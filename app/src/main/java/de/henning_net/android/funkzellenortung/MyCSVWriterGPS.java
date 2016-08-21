package de.henning_net.android.funkzellenortung;

/* Nur für Koordinaten
import android.content.Context;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.IOException;

public class MyCSVWriterGPS {

    private String time;
    private String service;
    private String type;
    private String lon;
    private String lat;
    private Context mContext;

    // Der CSVWriter bekommt bereits alle Angaben, die in die Datei geschrieben werden sollen
    public MyCSVWriterGPS(Context mContext, String time, String service, String type, String lon, String lat) {
        this.mContext = mContext;
        this.time = time;
        this.service = service;
        this.type = type;
        this.lon = lon;
        this.lat = lat;
    }

    public void writeToFile() throws IOException {
        // Verzeichnis und Datei bestimmen
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "FunkzellenOrtung_Koordinaten.csv"; // Dateiname im Wurzelverzeichnis
        String filePath = baseDir + File.separator + fileName;
        File file = new File(filePath );
        CSVWriter writer;
        if(file.exists() && !file.isDirectory()){  //Datei existiert bereits
            java.io.FileWriter myFileWriter = new java.io.FileWriter(filePath, true);
            writer = new CSVWriter(myFileWriter, ';', CSVWriter.NO_QUOTE_CHARACTER); // Semikolon trennt Dateieinträge
        }
        else { // Datei wird erstellt
            writer = new CSVWriter(new java.io.FileWriter(filePath), ';', CSVWriter.NO_QUOTE_CHARACTER); // Semikolon trennt Dateieinträge
        }
        String[] data = {time, service, type, lon, lat};
        writer.writeNext(data); // Schreibe alle Angaben in die Datei
        writer.close();
    }
} */

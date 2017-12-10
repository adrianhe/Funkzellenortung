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
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.IOException;

public class MyCSVWriterCell {

    private String time;
    private String service;
    private String type;
    private String mcc;
    private String mnc;
    private String lac;
    private String cellID;
    private Context mContext;

    // Der CSVWriter bekommt bereits Angaben, über die Zeit und die Art der Erfassung
    public MyCSVWriterCell(Context mContext, String service, String type) {
        this.mContext = mContext;
        this.service = service;
        this.type = type;
    }
    public void start(){
        getNetwork(); // Standort und Provider bestimmen
        getTime(); // Aktuelle Zeit einholen
        try {
            writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getNetwork(){


        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        try {

            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = "";
            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) { // Netzwerk Typ auf GSM prüfen. Keine CDMA Unterstützung
                networkOperator = tm.getNetworkOperator(); // Infos über Netzbetreiber holen
            }
            //MCC und MNC rausfiltern
            if (!TextUtils.isEmpty(networkOperator)) {
                mcc = networkOperator.substring(0, 3);
                mnc = networkOperator.substring(3);
            } else {
                mcc = "Unbekannt";
                mnc = "Unbekannt";
            }
            //CellID und LAC holen uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"
            GsmCellLocation cell = (GsmCellLocation) tm.getCellLocation();
            if (cell != null) {
                cellID = Integer.toString(cell.getCid());
                lac = Integer.toString(cell.getLac());
            } else {
                cellID = "ERROR";
                lac = "ERROR";
            }
        }
        catch(Exception ex) {
            mcc = "No Permission";
            mnc = "No Permission";
            cellID = "ERROR";
            lac = "ERROR";
        }
    }

    public void getTime(){
        TimeGet myTime = new TimeGet();
        time = myTime.getTime();
    }

    public void writeToFile() throws IOException {

            // Verzeichnis und Datei bestimmen
            String filePath = mContext.getSharedPreferences("settings", 0).getString("filepath",null);

            File file = new File(filePath);
            CSVWriter writer;
            if (file.exists() && !file.isDirectory()) {  //Datei existiert bereits
                java.io.FileWriter myFileWriter = new java.io.FileWriter(filePath, true);
                writer = new CSVWriter(myFileWriter, ',', CSVWriter.NO_QUOTE_CHARACTER); // Komma trennt Dateieinträge
            } else { // Datei wird erstellt
                writer = new CSVWriter(new java.io.FileWriter(filePath), ',', CSVWriter.NO_QUOTE_CHARACTER); // Komma trennt Dateieinträge
                String[] header = {"Datum und Uhrzeit", "Dienst", "Richtung", "MCC", "MNC", "LAC", "CellID"};
                writer.writeNext(header); // Schreibe Header der Datei
            }
            String[] data = {time, service, type, mcc, mnc, lac, cellID};
            writer.writeNext(data); // Schreibe alle Angaben in die Datei
            writer.close();
    }
}

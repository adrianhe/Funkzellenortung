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
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MyCSVWriterCell {

    private String time;
    private String service;
    private String type;
    private String mcc = "Unbekannt";
    private String mnc = "Unbekannt";
    private String lac = "ERROR";
    private String cellID = "ERROR";
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

    private void getNetwork(){


        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        try {

            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            //CellID und LAC holen uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"
            List<CellInfo> cells = tm.getAllCellInfo();
            if (cells != null){
                CellInfo cinfo = cells.get(0);
                if (cinfo instanceof CellInfoGsm){ //GSM Unterstützung
                    CellIdentityGsm cellIdentity = ((CellInfoGsm) cinfo).getCellIdentity();
                    mcc = Integer.toString(cellIdentity.getMcc());
                    mnc = Integer.toString(cellIdentity.getMnc());
                    cellID = Integer.toString(cellIdentity.getCid());
                    lac = Integer.toString(cellIdentity.getLac());
                }
                else if (cinfo instanceof CellInfoLte){ //LTE Unterstützung
                    CellIdentityLte cellIdentity = ((CellInfoLte) cinfo).getCellIdentity();
                    mcc = Integer.toString(cellIdentity.getMcc());
                    mnc = Integer.toString(cellIdentity.getMnc());
                    cellID = Integer.toString(cellIdentity.getCi());
                    lac = Integer.toString(cellIdentity.getTac());
                }
                else{
                    mcc = "No GSM or LTE Cell";
                    mnc = "No GSM or LTE Cell";
                }

            }
            else{
                mcc = "No Cellinfo found";
                mnc = "No Cellinfo found";
            }
        }
        catch(Exception ex) {
            mcc = "No Permission";
            mnc = "No Permission";
        }
    }

    public void getTime(){
        TimeGet myTime = new TimeGet();
        time = myTime.getTime();
    }

    public void writeToFile() throws IOException {

            // Verzeichnis und Datei bestimmen
            String filePath = mContext.getSharedPreferences("settings", 0).getString("filepath",null);
            File test = new File(mContext.getExternalFilesDir(null), "demo.csv");
            System.out.println(test);
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

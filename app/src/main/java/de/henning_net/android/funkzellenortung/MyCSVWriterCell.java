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
import android.content.SharedPreferences;
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
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
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
    private String radio = "ERROR";
    private String lac = "ERROR";
    private String cellID = "ERROR";
    private String psc = "ERROR"; //Primary Scrambling Code in Umts, Physical CellID in LTE
    private String signal = "ERROR"; // Signal Strength in dBm
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
                for (CellInfo cinfo:cells) {
                    if (cinfo instanceof CellInfoGsm) { //GSM Unterstützung
                        CellIdentityGsm cellIdentity = ((CellInfoGsm) cinfo).getCellIdentity();
                        CellSignalStrengthGsm signalStrength = ((CellInfoGsm) cinfo).getCellSignalStrength();
                        radio="gsm";
                        mcc = Integer.toString(cellIdentity.getMcc());
                        mnc = Integer.toString(cellIdentity.getMnc());
                        cellID = Integer.toString(cellIdentity.getCid());
                        lac = Integer.toString(cellIdentity.getLac());
                        psc = null;
                        signal = null; //RSSI
                        break;
                    } else if (cinfo instanceof CellInfoLte) { //LTE Unterstützung
                        CellIdentityLte cellIdentity = ((CellInfoLte) cinfo).getCellIdentity();
                        CellSignalStrengthLte signalStrength = ((CellInfoLte) cinfo).getCellSignalStrength();
                        radio="lte";
                        mcc = Integer.toString(cellIdentity.getMcc());
                        mnc = Integer.toString(cellIdentity.getMnc());
                        cellID = Integer.toString(cellIdentity.getCi());
                        lac = Integer.toString(cellIdentity.getTac());
                        psc = Integer.toString(cellIdentity.getPci());
                        signal = Integer.toString(signalStrength.getDbm()); //getRsrp()); Requires API 26 Android 8.0
                        break;
                    } else if (cinfo instanceof CellInfoWcdma) { //UMTS Unterstützung
                        CellIdentityWcdma cellIdentity = ((CellInfoWcdma) cinfo).getCellIdentity();
                        CellSignalStrengthWcdma signalStrength = ((CellInfoWcdma) cinfo).getCellSignalStrength();
                        radio = "umts";
                        mcc = Integer.toString(cellIdentity.getMcc());
                        mnc = Integer.toString(cellIdentity.getMnc());
                        cellID = Integer.toString(cellIdentity.getCid());
                        lac = Integer.toString(cellIdentity.getLac());
                        psc = Integer.toString(cellIdentity.getPsc());
                        signal = null;
                        break;
                    }
                    else{
                        radio = "cdma";
                        mcc = cinfo.toString(); //Keine CDMA Unterstützung
                        mnc = "CDMA Cell";
                    }
                }


            }
            else{
                radio = "No Cellinfo found";
                mcc = "No Cellinfo found";
                mnc = "No Cellinfo found";
            }
        }
        catch(Exception ex) {
            radio = "No Permission";
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
            File file = new File(filePath);
            CSVWriter writer;
            if (file.exists() && !file.isDirectory()) {  //Datei existiert bereits
                java.io.FileWriter myFileWriter = new java.io.FileWriter(filePath, true);
                writer = new CSVWriter(myFileWriter, ',', CSVWriter.NO_QUOTE_CHARACTER,CSVWriter.NO_ESCAPE_CHARACTER,CSVWriter.DEFAULT_LINE_END); // Komma trennt Dateieinträge
            } else { // Datei wird erstellt
                writer = new CSVWriter(new java.io.FileWriter(filePath), ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER,CSVWriter.DEFAULT_LINE_END); // Komma trennt Dateieinträge
                String[] header = {"Datum und Uhrzeit", "Dienst", "Richtung", "MCC", "MNC", "Radio", "LAC", "CellID", "PSC", "Signal"};
                writer.writeNext(header); // Schreibe Header der Datei
            }
            String[] data = {time, service, type, mcc, mnc, radio, lac, cellID, psc, signal};
            writer.writeNext(data); // Schreibe alle Angaben in die Datei
            writer.close();

        // Counter updaten
        SharedPreferences settings = mContext.getSharedPreferences("settings", 0);
        int amount = settings.getInt("amount",0);
        ++amount;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("amount", amount);
        editor.apply();
    }
}

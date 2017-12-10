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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.io.File;
import java.util.UUID;

public class TrackService extends Service {

    private SMSObserver co;
    private SmsListener mySmsListener;
    //private MmsListener myMmsListener;
    private TelephonyManager tm;
    private TelephonyManager tm2;
    private TeleListener myTeleListener;
    private DataListener myDataListener;

    public void onCreate() { // Beginne die Erfassung von SMS, Telefonie und Mobilen Daten
        SharedPreferences settings = getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("running", true);
        editor.apply();


            startSMSlisten();
            //  startMMSlisten(); // Keine Unterstützung für MMS Überwachung
            startCallListen();
            startMobileDataListen();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY; // Starte Service erneut, wenn vom System geschlossen
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onDestroy() { // Beende die Erfassung von SMS, Telefonie und Mobilen Daten
        try {
            stopSMSlisten();
            // stopMMSlisten(); // Keine Unterstützung für MMS Überwachung
            stopCallListen();
            stopMobileDataListen();
        }
        catch (Exception ex) {}
            SharedPreferences settings = getSharedPreferences("settings", 0);
            SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("running", false);
        editor.apply();
    }

    public void startSMSlisten(){ // starte das Erfassen von SMS
        //eingehende SMS
        mySmsListener = new SmsListener(getApplicationContext());
        IntentFilter filter = new IntentFilter();  // Erzeuge einen Filter, auf was reagiert wird
        filter.addAction("android.provider.Telephony.SMS_RECEIVED"); // SMS
        filter.addAction("android.provider.Telephony.SMS_CB_RECEIVED"); // Funkzellen Broadcast SMS
        filter.addAction("android.provider.Telephony.DATA_SMS_RECEIVED"); // Daten SMS
        registerReceiver(mySmsListener, filter);

        //ausgehende SMS
        co = new SMSObserver(new Handler(), getApplicationContext());
        this.getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true, co); // Überwache Änderungen im SMS Speicher
    }

    public void stopSMSlisten(){ // stoppe das Erfassen von SMS
        //eingehende SMS
        unregisterReceiver(this.mySmsListener);

        //ausgehende SMS
        if (co != null) {
            this.getContentResolver().unregisterContentObserver(co);
        }

    }
    /* KEINE UNTERSTÜTZUNG FÜR MMS ERFASSUNG

    public void startMMSlisten() throws IntentFilter.MalformedMimeTypeException{ // starte das Erfassen von MMS
        //eingehende MMS
        myMmsListener = new MmsListener(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.WAP_PUSH_RECEIVED");
        filter.addDataType("application/vnd.wap.mms-message");
        registerReceiver(myMmsListener, filter);

        //ausgehende MMS
        //TODO
    }

    public void stopMMSlisten(){ // stoppe das Erfassen von MMS
        //eingehende MMS
        unregisterReceiver(this.myMmsListener);

        //ausgehende MMS
        //TODO
    }
    */

    public void startCallListen(){ // starte das Erfassen von Anrufen
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myTeleListener = new TeleListener(getApplicationContext());
        tm.listen(myTeleListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void stopCallListen(){ // stoppe das Erfassen von Anrufen
        this.tm.listen(this.myTeleListener, PhoneStateListener.LISTEN_NONE);
    }

    public void startMobileDataListen(){ // starte das Erfassen von Mobilen Daten
        tm2 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myDataListener = new DataListener(getApplicationContext());
        tm2.listen(myDataListener, PhoneStateListener.LISTEN_DATA_ACTIVITY);
    }

    public void stopMobileDataListen(){ // stoppe das Erfassen von Mobilen Daten
        this.tm2.listen(this.myDataListener, PhoneStateListener.LISTEN_NONE);
    }

}

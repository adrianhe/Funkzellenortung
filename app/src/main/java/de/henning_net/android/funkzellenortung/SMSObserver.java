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
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

public class SMSObserver extends ContentObserver {

    private Context mContext;
    private static final Uri uriSMS = Uri.parse("content://sms/sent");
    private static int initialPos;
    private final String sms = "SMS";

    public SMSObserver(Handler handler, Context ctx) {
        super(handler);
        mContext = ctx;
        initialPos = getLastMsgId(); // Position der letzten gesendeten SMS zum Abgleich
    }

    public int getLastMsgId() { //Position der letzten gesendeten SMS holen
        Cursor cur = mContext.getContentResolver().query(uriSMS, null, null, null, null);
        if (cur.getCount() > 0){
             cur.moveToFirst();
             int lastMsgId = cur.getInt(cur.getColumnIndex("_id"));
             cur.close();
             return lastMsgId;
        }
        else{
            cur.close();
            return -1; //-1 wenn noch nie eine SMS gesendet wurde
        }
    }

    /* Wenn sich der Zustand des Speichers für gesendete SMS ändert,
    wird die letzte SMS verarbeitet*/
    public void onChange(boolean selfChange) {
        queryLastSentSMS();
    }

    protected void queryLastSentSMS() {
        new Thread(new Runnable() {
            public void run() {
                Cursor cur =
                        mContext.getContentResolver().query(uriSMS, null, null, null, null);
                // Zeiger auf die letzte gesendete SMS setzen
                if (cur.moveToNext()) { // Existiert neue SMS?
                    try {
                        if (initialPos != getLastMsgId()) { // Checkt ob die SMS schon verarbeitet wurde
                            MyCSVWriterCell newWriter = new MyCSVWriterCell(mContext, sms, "ausgehend");
                            newWriter.start();
                            initialPos = getLastMsgId(); // Position aktualisieren
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cur.close();
            }
        }).start();
    }
}
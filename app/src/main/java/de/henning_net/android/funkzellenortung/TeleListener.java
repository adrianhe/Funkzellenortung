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
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class TeleListener extends PhoneStateListener {

    private Context mContext;
    private int myState = 0;  // Checkt in welchem Vorzustand sich die Telefonverbindung befand
    private final String tel = "Telefonie";

    public TeleListener(Context mContext) {
        this.mContext = mContext;
    }

    /* Wenn sich der Zustand der Telefonverbindung ändert, wird je nach Status gehandelt.
    Nur bei eingehenden und ausgehenden Anrufen wird der Standort abgerufen.*/
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE: // Keine Telefonverbindung
                    myState = 0;
                    break;
            case TelephonyManager.CALL_STATE_OFFHOOK: // Aktives Gespräch
                    if (!(myState == 1)){ // Nur bei ausgehenden Telefonaten kein vorheriges Klingeln
                        MyCSVWriterCell newWriter = new MyCSVWriterCell(mContext, tel, "ausgehend");
                        newWriter.start();
                    }
                    myState = 2;
                    break;
            case TelephonyManager.CALL_STATE_RINGING: // Telefon klingelt, eingehendes Telefonat
                    MyCSVWriterCell newWriter = new MyCSVWriterCell(mContext, tel, "eingehend");
                    newWriter.start();
                    myState = 1;
                    break;
            default:
                    break;
            }
        }
}

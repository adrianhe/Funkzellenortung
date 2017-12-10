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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

public class SmsListener extends BroadcastReceiver {

    public SmsListener(Context mContext) {
        this.mContext = mContext;
    }
    private final String sms = "SMS";
    private Context mContext;

    /* Wenn eine Aktion des Filters ausgeführt wird, wird gecheckt,
    ob es sich um eine eingehende SMS o.ä. handelt.*/
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction()) || Telephony.Sms.Intents.SMS_CB_RECEIVED_ACTION.equals(intent.getAction()) || Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION.equals(intent.getAction())) {
    MyCSVWriterCell newWriter = new MyCSVWriterCell(mContext, sms, "eingehend");
            newWriter.start();
        }
    }
}
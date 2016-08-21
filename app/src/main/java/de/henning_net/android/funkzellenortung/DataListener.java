package de.henning_net.android.funkzellenortung;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class DataListener extends PhoneStateListener {

    private Context mContext;
    private final String data = "Mobile Daten";
    private boolean myState = true;  //Flagge, ob sich die Art der Verbindung geändert hat. Beim Start gesetzt, da sonst sofort ein Eintrag erfolgt

    public DataListener(Context mContext) {
        this.mContext = mContext;
    }

    /* Wenn sich der Zustand der Datenverbindung ändert, wird zuerst geguckt,
    ob es sich um eine mobile Datenverbindung handelt. Nur bei neuen
    eingehenden mobilen Verbindungen wird der Standort abgerufen. DATA_ACTIVITY_OUT
    wird zu oft ausgeführt.*/
    public void onDataActivity (int direction) {
            switch (direction) {
                case TelephonyManager.DATA_ACTIVITY_NONE: //Kein Datentransfer
                    myState = false;
                    break;
                case TelephonyManager.DATA_ACTIVITY_IN: //Eingehender Datentransfer
                    if (!(myState)) {
                        MyCSVWriterCell newWriter = new MyCSVWriterCell(mContext, data, "ein- und ausgehend");
                        newWriter.start();
                    }
                    myState = true;
                    break;
                case TelephonyManager.DATA_ACTIVITY_OUT:  //Ausgehender Datentransfer
                    myState = false;
                    break;
                case TelephonyManager.DATA_ACTIVITY_INOUT: //Ein- und ausgehender Datentransfer
                    if (!(myState)) {
                        MyCSVWriterCell newWriter = new MyCSVWriterCell(mContext, data, "ein- und ausgehend");
                        newWriter.start();
                    }
                    myState = true;
                    break;
                case TelephonyManager.DATA_ACTIVITY_DORMANT: // Verbindung besteht aber kein Datentransfer
                    myState = false;
                    break;
                default:
                    break;
            }
    }
}

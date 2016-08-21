package de.henning_net.android.funkzellenortung;

/* Nur für Koordinaten
import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import java.io.IOException;

public class NetworkGet {

    private Context mContext;
    private String io;
    private String type;
    private String mcc;
    private String mnc;
    private String cellId;
    private String lac;
    private final String apiKey = "4734ea44-c5b2-4580-87d0-2f072a3e8d93"; // Open CellID API-Key von adrian.henning@fu-berlin.de
    private String lon;
    private String lat;
    private String time;

    public NetworkGet(Context mContext, String type, String io) {
        this.mContext = mContext;
        this.type = type;
        this.io = io;
    }

    public void getNetwork(){
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = "";
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) { // Netzwerk Typ auf GSM prüfen. Keine CDMA Unterstützung
            networkOperator = tm.getNetworkOperator(); // Infos über Netzbetreiber holen
        }
        //MCC und MNC rausfiltern
        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = networkOperator.substring(0, 3);
            mnc = networkOperator.substring(3);
        }
        //CellID und LAC holen
        GsmCellLocation cell = (GsmCellLocation) tm.getCellLocation();
        cellId = Integer.toString(cell.getCid());
        lac = Integer.toString(cell.getLac());
        getTime(); // Aktuelle Zeit einholen
        getCoordinates(); // GPS-Koordinaten für die Funkzelle einholen
    }

    public void getTime(){
        TimeGet myTime = new TimeGet();
        this.time = myTime.getTime();
    }

    public void getCoordinates(){
        //RequestQueue queue = MySingleton.getInstance(mContext).getRequestQueue();
        // Open CellID Json API
        String url ="http://opencellid.org/cell/get?key="+apiKey+"&mcc="+mcc+"&mnc="+mnc+"&lac="+lac+"&cellid="+cellId+"&format=json";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    public void onResponse(JSONObject response) {
                            lon = response.optString("lon"); // Extrahiere Längengrad
                            lat = response.optString("lat"); // Extrahiere Breitengrad
                            MyCSVWriterGPS writer = new MyCSVWriterGPS(mContext, time, type, io, lon, lat);
                        try {
                            writer.writeToFile(); // Schreibe alle Angaben in die Datei
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    public void onErrorResponse(VolleyError error) {
                        lon = "Unbekannt";
                        lat = "Unbekannt";
                        MyCSVWriterGPS writer = new MyCSVWriterGPS(mContext, time, type, io, lon, lat);
                        try {
                            writer.writeToFile(); // Schreibe alle Angaben in die Datei
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        // Füge die Anfrage der Singelton Warteschlange hinzu
        MySingleton.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }

} */

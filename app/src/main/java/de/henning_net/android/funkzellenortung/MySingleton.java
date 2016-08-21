package de.henning_net.android.funkzellenortung;
/* Nur für Koordinaten
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {

    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private MySingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue(); // Hole die aktuelle Warteschlange
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) { // Existiert schon ein Singleton?
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() { // Benutze immer dieselbe Warteschlange für Webanfragen
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req); // Füge Webanfrage der Warteschlange hinzu
    }
}
*/
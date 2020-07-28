package com.example.gopal.quakereport2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Gopal on 12/25/2018.
 */


public class CustomLoader extends AsyncTaskLoader<List<Earthquake>> {
    public static final String LOG_TAG = CustomLoader.class.getName();

    private String mUrl;
    public CustomLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }
    @Override
    protected void onStartLoading() {
        Log.e(LOG_TAG,"onStartLoading is called: ");

        forceLoad();
    }


    /**
     * This is on a background thread.
     */
    @Override
    public List<Earthquake> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        Log.e(LOG_TAG,"loadInBackground is called: ");

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Earthquake> earthquakes = Utils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }
}

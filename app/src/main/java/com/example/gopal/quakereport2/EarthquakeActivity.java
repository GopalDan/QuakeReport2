package com.example.gopal.quakereport2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Earthquake>>{

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private final String USGS_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query";
    private CustomAdapter mCustomAdapter;
    private List<Earthquake> mearthquakes;
    private TextView mEmptyTextView;
    private View mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        mProgressbar = findViewById(R.id.progress_bar);
        mEmptyTextView = findViewById(R.id.empty_view);
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        // Empty view
        earthquakeListView.setEmptyView(mEmptyTextView);
        mearthquakes = new ArrayList<>();

        // Create a new {@link ArrayAdapter} of earthquakes
        mCustomAdapter = new CustomAdapter(this, mearthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mCustomAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                //Earthquake currentEarthquake = mAdapter.getItem(position);
                Earthquake itemCliked = mearthquakes.get(position);
                Uri webpage = Uri.parse(itemCliked.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        Log.e(LOG_TAG,"initLoader is called: ");
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if(networkInfo!=null && networkInfo.isConnected()){
            getLoaderManager().initLoader(0,null,this).forceLoad();
        }
        else{
            mProgressbar.setVisibility(View.GONE);
            mEmptyTextView.setText("No network connection");
        }
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        Log.e(LOG_TAG,"onCreateLoader is called: ");

       // return new CustomLoader(this,USGS_URL);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(USGS_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", "time");

        // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=time
        return new CustomLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        // Set empty state text to display "No earthquakes found."
        mEmptyTextView.setText("no_earthquakes");
        mProgressbar.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
       mCustomAdapter.clear();
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.

        if (earthquakes != null && !earthquakes.isEmpty()) {
            Log.e(LOG_TAG,"onLoadFinished is called: ");

            mCustomAdapter.addAll(earthquakes);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.e(LOG_TAG,"onLoadReset is called: ");
        mCustomAdapter.clear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);

    }
}




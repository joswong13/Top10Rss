package com.example.ricey.top10rss;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //list view widget
    private ListView listApps;

    // initial feedURL is set to top free apps
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";

    // initial option is set to top 10
    private int feedLimit = 10;


    private String feedCachedUrl = "INVALID DATA";

    public static final String STATE_URL = "feedUrl";
    public static final String STATE_LIMIT = "feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listApps = findViewById(R.id.xmlListView);

        // this is call when screen gets rotated
        if (savedInstanceState != null)
        {
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadUrl(String.format(feedUrl, feedLimit));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.feeds_menu, menu);

       if (feedLimit == 10)
       {
           menu.findItem(R.id.mnu10).setChecked(true);
       }
       else
       {
           menu.findItem(R.id.mnu25).setChecked(true);
       }

       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.mnuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            // set feed limit to top 10 if top 25 was checked
            case R.id.mnu10:
                if (!item.isChecked())
                {
                    item.setChecked(true);
                    feedLimit = 10;

                }
                break;
            case R.id.mnu25:
                if (!item.isChecked())
                {
                    item.setChecked(true);
                    feedLimit = 25;

                }
            case R.id.mnuRefresh:
                feedCachedUrl = "INVALIDATED";
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        downloadUrl(String.format(feedUrl, feedLimit));
        return true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);


        super.onSaveInstanceState(outState);
    }

    private void downloadUrl (String feedUrl)
    {
        if (!feedUrl.equalsIgnoreCase(feedCachedUrl))
        {
            DownloadData downloadData = new DownloadData();
            downloadData.execute(feedUrl);
            feedCachedUrl = feedUrl;
        }


    }

    private class DownloadData extends AsyncTask<String, Void, String>
    {
        private static final String TAG = "DownloadData";
        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            //Log.d(TAG, "onPostExecute: parameter is " + s);

            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

            // custom adapter for list view
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings)
        {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null)
            {
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        }

        private String downloadXML (String urlPath)
        {
            StringBuilder xmlResult = new StringBuilder();

            try
            {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);

                int charsRead;
                char[] inputBuffer = new char[500];
                while(true)
                {
                    charsRead = reader.read(inputBuffer);
                    if (charsRead < 0)
                    {
                        break;
                    }
                    if (charsRead>0)
                    {
                        //Log.d(TAG, "downloadXML: charsRead is " + String.valueOf(charsRead));
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();

                return xmlResult.toString();


            } catch(MalformedURLException e){
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch(IOException e){
                Log.e(TAG, "downloadXML: IO exception " + e.getMessage());
            } catch(SecurityException e){
                Log.e(TAG, "downloadXML: needs permission " + e.getMessage());
            }

            return null;
        }


    }

}

package com.example.pelicanremote;

import android.os.AsyncTask;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class PelicanRequest extends AsyncTask<String, String, String> {

    /**
     * Hits Pelican with a basic HTTP GET request as the provided endpoint.
     * @param params The endpoint as a string.
     * @return The HTTP response body.
     */
    @Override
    protected String doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();
        try {
            url = new URL(params[0]);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                result.append(current);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);
    }
}
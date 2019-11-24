package ru.renelogist.chat;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class GetData extends AsyncTask<String, Void, String>{

    @Override
    protected String doInBackground(String... params) {
        StringBuffer response = null;
        HttpURLConnection connection = null;
        try {
            URL uri = new URL(params[0]);
            connection = (HttpURLConnection) uri.openConnection();
            //add reuqest header
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0" );
            connection.setRequestProperty("Accept-Language", ",en;q=0.5");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("x-auth-token", params[1]);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();

            Log.i("HTTP","Response string: " + response.toString());
        } catch (IOException e) {
            Log.e("HTTP", e.getMessage());
        } finally {
            if (connection!=null) try {
                connection.disconnect();
            } finally {
            }
        }
        if (response == null) return null;
        return response.toString();

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e("RESULT", result==null?"null":result);
    }


}

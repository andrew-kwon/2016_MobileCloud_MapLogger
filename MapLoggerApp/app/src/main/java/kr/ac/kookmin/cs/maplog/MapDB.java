package kr.ac.kookmin.cs.maplog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016-06-06.
 */
public class MapDB {

    public void sendLogtoDB(String logName, String latitude, String longtitude) {
        String urlSuffix = "?logName="+logName+"&latitude="+latitude+"&longtitude="+longtitude;
        class sendLogClass extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loading = ProgressDialog.show(MeetUpJoinListActivity.this, "Please Wait",null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equals("successfully Upload")) {
                } else {
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL("http://52.207.214.66/maplog/uploadMaplog.php" + s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String result;
                    result = bufferedReader.readLine();
                    return result;
                } catch (Exception e) {
                    return null;
                }
            }
        }
        sendLogClass ru = new sendLogClass();
        ru.execute(urlSuffix);
    }


    public GoogleMap showMyLog(GoogleMap myMap)
    {
        final GoogleMap myFinalMap = myMap;
        class RegisterUser extends AsyncTask<String, Void, String> {

//            private Dialog loadingDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loadingDialog = ProgressDialog.show(MapsActivity.getContext(), "Please wait", "Loading...");

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loadingDialog.dismiss();
//                Toast.makeText(MapsActivity.getContext(),s,Toast.LENGTH_LONG).show();

                String[] splitS = s.split("--:--");
                    for(int k=0; k<splitS.length; k++)
                    {
                        String logName = splitS[k].split(":::")[1];
                        String latitude = splitS[k].split(":::")[2];
                        String longtitude = splitS[k].split(":::")[3];
                        LatLng sydney = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longtitude));
                        myFinalMap.addMarker(new MarkerOptions().position(sydney).title(logName));
                    }
            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL("http://52.207.214.66/maplog/showMaplog.php"+s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    return result;
                }catch(Exception e){
                    return null;
                }
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute("");
        return myFinalMap;
    }

}


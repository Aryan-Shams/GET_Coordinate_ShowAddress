package com.shopnosoft.getco_ordinatesandshowaddress;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView coordinate,address;


    private WifiManager wfm;
    private ConnectivityManager cntm;
    // GPSTracker class
    GPSTracker gps;


//For Address
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinate = (TextView)findViewById(R.id.txtcoordinates);
        address = (TextView)findViewById(R.id.txtAddress);

        requestQueue = Volley.newRequestQueue(this);


        if(!network()){
            createNetErrorDialog();
        }
        else{
            Location();
            LocationToAddress();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!network()){
            createNetErrorDialog();
        }
        else{
            Location();
            LocationToAddress();
        }
    }



    ////////<<-------------------------------CHECKING NETWORK CONNECTION STATUS------------------------->>///////////////////////


    public boolean network() {

//Get the wifi service of device
        wfm = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
//Get the Mobile Dtata service of device
        cntm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo nInfo = cntm.getActiveNetworkInfo();

        if ((wfm.isWifiEnabled()) || (nInfo != null && nInfo.isConnected())) {
            return true;
        }

        else{
            return false;
        }

    }



////////<<-------------------------------CHECKING NETWORK CONNECTION STATUS ENDS-------------------->>///////////////////////





///////<<-------------------------------CREATING ERROR DIALOG--------------------------------------->>////////////////////////

    protected void createNetErrorDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Enable",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_SETTINGS);

                                //startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));

                                startActivity(i);
                                //finish();
                            }
                        }
                )  .setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                }
        )

        ;
        AlertDialog alert = builder.create();
        alert.show();
    }

///////<<-------------------------------CREATING ERROR DIALOG ENDS--------------------------------------->>///////////////////



    ///////<<-------------------------------- SHOWING LOCATION CO-ORDINATES  --------------------------->>////////////////////////
    public void Location(){

        gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            final String lat = String.valueOf(latitude);
            final String log = String.valueOf(longitude);

            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            String lcncordnt = "Latitude : "+ latitude + "\nLongitude : "+longitude;
            coordinate.setText(lcncordnt);

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }
///////<<-------------------------------- SHOWING LOCATION CO-ORDINATES  ENDS--------------------------->>////////////////////////


///////<<-------------------------------- Get Address From Location--------------------------->>////////////////////////
public void LocationToAddress(){

//
    gps = new GPSTracker(MainActivity.this);


        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

        final String lattoadd = String.valueOf(latitude);
        final String logtoadd = String.valueOf(longitude);


    //    JsonObjectRequest request = new JsonObjectRequest("https://maps.googleapis.com/maps/api/geocode/json?latlng=23.781522,"90.3704991&key=AIzaSyBma_A78YGbZwGav3SR3vSGoAXka8FGFzQ", new Response.Listener<JSONObject>() {

    JsonObjectRequest request = new JsonObjectRequest("https://maps.googleapis.com/maps/api/geocode/json?latlng="+lattoadd+","+logtoadd+"&key=AIzaSyBma_A78YGbZwGav3SR3vSGoAXka8FGFzQ", new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            try {
                String get_address = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                address.setText(get_address);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });
    requestQueue.add(request);
}



///////<<-------------------------------- Get Address From Location Ends--------------------------->>////////////////////////




}

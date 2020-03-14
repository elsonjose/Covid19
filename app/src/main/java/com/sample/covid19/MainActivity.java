package com.sample.covid19;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.sample.covid19.Helper.HttpHandler;
import com.sample.covid19.Models.Confirmed;
import com.sample.covid19.Models.Coordinates;
import com.sample.covid19.Models.Locations;
import com.sample.covid19.Models.Marker;
import com.sample.covid19.Models.dataModel;
import com.sample.covid19.Models.dataModelLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    MapView mapView;
    GoogleMap map;
    ProgressBar mainProgressbar;
    RelativeLayout mainRoot;
    Dialog MarkerDialog, infoDialog;
    dataModel mainDataModel = null;
    List<Marker> markerList = null;
    float average = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainRoot = findViewById(R.id.mainRoot);

        mainProgressbar = findViewById(R.id.mainProgressbar);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    getApplicationContext(), R.raw.mapstyle));

                    if (!success) {
                        Toast.makeText(getApplicationContext(), "Map rendering failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (Resources.NotFoundException e) {

                }

                map = googleMap;
                map.getUiSettings().setMapToolbarEnabled(false);
                map.getUiSettings().setTiltGesturesEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);

            }
        });
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new getConfirmedList().execute();


    }

    private void showInfoDialog() {

        infoDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        infoDialog.setCancelable(true);
        infoDialog.setCanceledOnTouchOutside(true);
        infoDialog.setContentView(R.layout.info_layout);
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window MarkerDialogwindow = infoDialog.getWindow();
        MarkerDialogwindow.setGravity(Gravity.BOTTOM);
        MarkerDialogwindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        MarkerDialogwindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        MarkerDialogwindow.setDimAmount(0.50f);

        WebView infoWebView = infoDialog.findViewById(R.id.info_webview);
        infoWebView.loadData("<div>Launcher icon made by <a title=\"DinosoftLabs\" href=\"https://www.flaticon.com/authors/dinosoftlabs\">DinosoftLabs</a> from <a title=\"Flaticon\" href=\"https://www.flaticon.com/\">www.flaticon.com</a></div>", "text/html", null);
        infoDialog.show();
    }


    private void ShowMarkerInfo(String id, List<Marker> markerModelList) {

        MarkerDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        MarkerDialog.setCancelable(true);
        MarkerDialog.setCanceledOnTouchOutside(true);
        MarkerDialog.setContentView(R.layout.bottomsheet_marker_layout);
        MarkerDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window MarkerDialogwindow = MarkerDialog.getWindow();
        MarkerDialogwindow.setGravity(Gravity.BOTTOM);
        MarkerDialogwindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        MarkerDialogwindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        MarkerDialogwindow.setDimAmount(0.50f);

        TextView markerPlace, markerDeaths, markerConfirmed, markerRecovery, markerStatus;
        ImageButton markerIconStatus, markerInfoBtn;


        markerPlace = MarkerDialog.findViewById(R.id.marker_place);
        markerDeaths = MarkerDialog.findViewById(R.id.marker_deaths);
        markerConfirmed = MarkerDialog.findViewById(R.id.marker_confirmed);
        markerRecovery = MarkerDialog.findViewById(R.id.marker_recovery);
        markerStatus = MarkerDialog.findViewById(R.id.marker_status);
        markerIconStatus = MarkerDialog.findViewById(R.id.marker_imagebutton);
        markerInfoBtn = MarkerDialog.findViewById(R.id.markerInfoBtn);

        markerInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showInfoDialog();

            }
        });


        for (int i = 0; i < markerModelList.size(); i++) {
            if (markerModelList.get(i).getMarkerID().equals(id)) {

                Marker marker = markerModelList.get(i);
                if (marker.getProvince().equals("")) {
                    markerPlace.setText(marker.getCountry());
                } else {
                    markerPlace.setText(marker.getProvince() + ", " + marker.getCountry());

                }
                markerConfirmed.setText("Confirmed : " + marker.getConfirmed());
                markerDeaths.setText("Deaths : " + marker.getDeaths());
                markerRecovery.setText("Recovered : " + marker.getRecovered());


                float percentage = ((float) ((marker.getConfirmed() - marker.getRecovered())) / (float) (average)) * 100;
                markerIconStatus.setImageBitmap(createCustomMarker(percentage, MainActivity.this));
                if (percentage <= 20) {
                    markerStatus.setText("Status :   0  -  20 %");

                } else if (percentage <= 40) {
                    markerStatus.setText("Status : 20  -  40 %");

                } else if (percentage <= 60) {
                    markerStatus.setText("Status : 40  -  60 %");

                } else if (percentage <= 80) {
                    markerStatus.setText("Status : 60  -  80 %");

                } else {
                    markerStatus.setText("Status : 80 - 100 %");
                }

                MarkerDialog.show();
            }
        }
    }

    private void saveJsonAsTextFile(Context context, String data) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput("Json.txt", Context.MODE_PRIVATE));
            writer.write(data);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readJsonFromTextFile(Context context) {
        String data = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput("Json.txt")));
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append("\n").append(line);
            }
            reader.close();
            data = builder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

    private class getConfirmedList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mainProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://coronavirus-tracker-api.herokuapp.com/all";
            String jsonStr = "";
            if (isConnected()) {
                jsonStr = sh.makeServiceCall(url);
                saveJsonAsTextFile(getApplicationContext(), jsonStr);
            } else {
                jsonStr = readJsonFromTextFile(getApplicationContext());
                if (jsonStr.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Snackbar.make(mainRoot, "Unable to read data.", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }

            }
            if (jsonStr != null && !jsonStr.equals("")) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    //confirmed data
                    JSONObject confirmed = jsonObj.getJSONObject("confirmed");
                    String last_updated = confirmed.getString("last_updated");
                    JSONArray LocationObj = confirmed.getJSONArray("locations");
                    String Lattitude = "NA", Longitude = "NA", Country = "NA", countryConfirmed = "NA", Province = "NA";
                    final List<Locations> confirmedLocationsList = new ArrayList<>();

                    for (int i = 0; i < LocationObj.length(); i++) {
                        JSONObject location = LocationObj.getJSONObject(i);

                        JSONObject coordinates = location.getJSONObject("coordinates");
                        Lattitude = coordinates.getString("lat");
                        Longitude = coordinates.getString("long");
                        Coordinates coord = new Coordinates(Lattitude, Longitude);

                        Country = location.getString("country");
                        countryConfirmed = location.getString("latest");
                        Province = location.getString("province");

                        confirmedLocationsList.add(new Locations(coord, Country, Integer.parseInt(countryConfirmed), Province));
                    }

                    //deathlist
                    JSONObject deaths = jsonObj.getJSONObject("deaths");
                    JSONArray deathsLocationObj = deaths.getJSONArray("locations");
                    String countryDeathLatest = "NA";
                    final List<Integer> countryDeathCount = new ArrayList<>();
                    for (int i = 0; i < deathsLocationObj.length(); i++) {
                        JSONObject death = deathsLocationObj.getJSONObject(i);
                        countryDeathLatest = death.getString("latest");
                        countryDeathCount.add(Integer.parseInt(countryDeathLatest));
                    }

                    //recoverlist
                    JSONObject recovered = jsonObj.getJSONObject("recovered");
                    JSONArray recoveredLocationObj = recovered.getJSONArray("locations");
                    String countryRecoveredLatest = "NA";
                    final List<Integer> recoveryCount = new ArrayList<>();
                    for (int i = 0; i < recoveredLocationObj.length(); i++) {
                        JSONObject death = recoveredLocationObj.getJSONObject(i);
                        countryRecoveredLatest = death.getString("latest");
                        recoveryCount.add(Integer.parseInt(countryRecoveredLatest));
                    }

                    // latest-data
                    JSONObject latest = jsonObj.getJSONObject("latest");
                    String confirmedCount = latest.getString("confirmed");
                    String deathCount = latest.getString("deaths");
                    String recoveredCount = latest.getString("recovered");

                    List<dataModelLocation> LocationsList = new ArrayList<>();
                    for (int c = 0; c < confirmedLocationsList.size(); c++) {
                        average += confirmedLocationsList.get(c).getConfirmedCount();
                        dataModelLocation model = new dataModelLocation(confirmedLocationsList.get(c).getCoordinates(),
                                confirmedLocationsList.get(c).getCountry(),
                                confirmedLocationsList.get(c).getConfirmedCount(),
                                countryDeathCount.get(c), recoveryCount.get(c),
                                confirmedLocationsList.get(c).getProvince());
                        LocationsList.add(model);

                    }
                    average = ((float) average) / ((float) confirmedLocationsList.size());
                    mainDataModel = new dataModel(confirmedCount, deathCount, recoveredCount, last_updated, LocationsList);

                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mainProgressbar.clearAnimation();
            mainProgressbar.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_back_up));
            mainProgressbar.getAnimation().start();
            mainProgressbar.setVisibility(View.GONE);
            mapView.clearAnimation();
            mapView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
            mapView.getAnimation().start();

            if (mainDataModel != null) {
                String input = mainDataModel.getLast_updated().replace("Z", "").replace("T", "");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SSS", Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date date = null;
                try {
                    date = format.parse(input);
                    final long millis = date.getTime();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(mainRoot, "Last updated at " + getDate(String.valueOf(millis)), Snackbar.LENGTH_LONG).show();
                        }
                    });

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                markerList = new ArrayList<>();
                for (int i = 0; i < mainDataModel.getLocations().size(); i++) {
                    final dataModelLocation location = mainDataModel.getLocations().get(i);
                    final float percentage = ((float) ((location.getConfirmedCount() - location.getRecoveredCount())) / (float) (average)) * 100;
                    MarkerOptions markerOptions = new MarkerOptions().alpha(0.6f).position(new LatLng(Double.valueOf(location.getCoordinates().getLat()), Double.valueOf(location.getCoordinates().getLong()))).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(percentage, MainActivity.this)));
                    com.google.android.gms.maps.model.Marker marker = map.addMarker(markerOptions);
                    markerList.add(new Marker(marker.getId(), location.getConfirmedCount(), location.getCountry(), location.getProvince(), location.getDeathCount(), location.getRecoveredCount()));
                }

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                        if (marker != null && markerList != null) {
                            ShowMarkerInfo(marker.getId(), markerList);
                        }
                        return false;
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(mainRoot, "Failed to fetch data.", Snackbar.LENGTH_LONG).show();
                    }
                });
            }


        }

    }

    
    public String getDate(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            CharSequence Time = DateUtils.getRelativeDateTimeString(getApplicationContext(), time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
            return String.valueOf(Time);
        } catch (NumberFormatException e) {
            return "Nil";
        }
    }

    public static Bitmap createCustomMarker(float percentage, Context context) {

        View marker;
        marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_user_marker_layout, null);
        ImageButton button = marker.findViewById(R.id.custom_imagebutton);

        if (percentage <= 20) {
            button.setImageResource(R.drawable.ic_brightness_low);

        } else if (percentage <= 40) {
            button.setImageResource(R.drawable.ic_brightness_low_medium);

        } else if (percentage <= 60) {
            button.setImageResource(R.drawable.ic_brightness_medium);

        } else if (percentage <= 80) {
            button.setImageResource(R.drawable.ic_brightness_high_medium);

        } else {
            button.setImageResource(R.drawable.ic_brightness_high);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}

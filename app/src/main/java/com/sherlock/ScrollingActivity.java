package com.sherlock;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sherlock.database.StaticInmemoryDatabase;
import com.sherlock.database.data.StoreCoordinate;
import com.sherlock.databinding.ActivityScrollingBinding;
import com.sherlock.firebase.FirebaseDBImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ScrollingActivity extends AppCompatActivity {

    private ActivityScrollingBinding binding;

    private LocationManager locationManager;
    boolean firebaseActivated = false;


    private static Location createLocation(String locationName, double latitude, double longitude) {
        Location location = new Location(locationName);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    private static List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(createLocation("Walmart Bentonville", 36.3682482d, -94.2252754d));
        locations.add(createLocation("Walmart IDC", 12.9332304d,77.6923389d));

        return locations;
    }

    private static String calculateDistanceFromLocations(Location currentLocation) {
        StringBuilder builder = new StringBuilder();
        for (Location eachLocation : getLocations()) {
            float distance = currentLocation.distanceTo(eachLocation); // meters
            builder.append(eachLocation.getProvider() + " -> " + distance + " meters away !! \n");
        }
        return builder.toString();
    }

    private String detectAndComputeAisle(Location currentLocation) {
        StringBuilder builder = new StringBuilder();

        Map<String, StoreCoordinate> storeAileCoordinates = FirebaseDBImpl.getStoreAisleCoordinates(getApplicationContext()); // StaticInmemoryDatabase.STORE_AILE_COORDINATES; FirebaseDBImpl.getInstance().getStoreAisleCoordinates()
        if(storeAileCoordinates == null) {
            int reqCode = 1;
            Intent intent = new Intent(getApplicationContext(), ScrollingActivity.class);
            showNotification(this, "Kindly load DB", "Press the floating action button to reload db.", intent, reqCode);
            return "Initializing DB..";
        }
        float configuredProximity = StaticInmemoryDatabase.AISLE_PROXIMITY;

        Map<Float, String> sortedDistances = new TreeMap<Float, String>();
        builder.append("\n\nDISTANCES FROM AISLES : !! \n");

        for (Map.Entry<String, StoreCoordinate> eachAisle : storeAileCoordinates.entrySet()) {

            String aisleName = eachAisle.getKey();
            StoreCoordinate coordinate = eachAisle.getValue();

            float distance = currentLocation.distanceTo(createLocation(aisleName, coordinate.getLatitude(), coordinate.getLongitude())); // meters
//            if (Float.compare(distance, configuredProximity) < 0) {
//                aisleName = aisleName + " (*) ";
//            }

            sortedDistances.put(distance, aisleName);

//            builder.append("Looks like you are " + distance + " away from -> " + aisleName + " aisle !! \n");
//            if (Float.compare(distance, configuredProximity) > 0) {
//                builder.append("Looks like you are in -> " + aisleName + " aisle !! \n");
//                break;
//            }
        }

        for (Map.Entry<Float, String> each : sortedDistances.entrySet()) {
            builder.append(each.getKey() + "  -> " + each.getValue() + " aisle !! \n");
        }

        if(!sortedDistances.isEmpty()) {
            Map.Entry<Float, String> entry = sortedDistances.entrySet().iterator().next();
            Float key = entry.getKey();
            String aisleName = entry.getValue();
            if (Float.compare(key, configuredProximity) < 0) {
                List<String> suggestedProducts = FirebaseDBImpl.getPreferredProductNames(aisleName, getApplicationContext());;

                String notificationHeader = "YOU ARE IN -> " + aisleName + " aisle !! ";
                String notificationContent = "Your suggested products are ";

                builder.append("\n\n YOU ARE IN -> " + aisleName + " aisle !! \n");
                if(suggestedProducts != null) {
                    builder.append("\n" + String.join(", ", suggestedProducts) + " \n");

                    notificationContent += String.join(", ", suggestedProducts);
                    int reqCode = 1;
                    Intent intent = new Intent(getApplicationContext(), ScrollingActivity.class);
                    showNotification(this, notificationHeader, notificationContent, intent, reqCode);
                }
            }
        }

        return builder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());
        TextView textViewFirst = (TextView) findViewById(R.id.body_maintextview);

        String[] PermissionsLocation =
                {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                };

        checkAndAskPermissions(PermissionsLocation);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            View parentLayout = findViewById(android.R.id.content);

            final double[] currentLatitude = {0};
            final double[] currentLongitude = {0};

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {

                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            if(Double.compare(latitude, currentLatitude[0]) != 0
                                    && Double.compare(longitude, currentLongitude[0]) != 0) {
                                currentLatitude[0] = latitude;
                                currentLongitude[0] = longitude;

                                String currentLocation = "Latitude:" + location.getLatitude() + "\nLongitude:" + location.getLongitude();
                                Snackbar.make(parentLayout,
                                        currentLocation, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                                String newTextContent = "YOUR LOCATION:\n" + currentLocation + " \n\n";
                                newTextContent += calculateDistanceFromLocations(location);

                                newTextContent += detectAndComputeAisle(location);

                                textViewFirst.setText(newTextContent);
                            }

                        }
                    });
            Snackbar.make(parentLayout,
                    "Location listener enabled. ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view," Starting to sync Firebase data. ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                FirebaseDBImpl.cacheDatabaseToLocal(getApplicationContext(), view);

            }
        });
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + reqCode);
    }

    private void checkAndAskPermissions(String[] permissions) {
        int iter = 0;
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != getPackageManager().PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScrollingActivity.this, permissions, 101 + iter);
            }
            ++iter;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
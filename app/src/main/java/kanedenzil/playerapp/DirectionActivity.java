package kanedenzil.playerapp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    TextView textView;

    //    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
//    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("players");
    private static final String TAG = DirectionActivity.class.getSimpleName();
    Location flagLocation = new Location("");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        textView = findViewById(R.id.textView3);
        createGoogleApi();

        googleApiClient.connect();
        Log.d(TAG, " googleApiClient HAS BEEN CONNECTED");
        // mMap.setMyLocationEnabled(true)
        String teamName = getIntent().getExtras().getString("team");
        if(teamName == "Team-A") {

            flagLocation.setLatitude(43.773705);
            flagLocation.setLongitude(-79.335894);
        }
        else{

            flagLocation.setLatitude(43.772738);
            flagLocation.setLongitude(-79.333472);
        }

//        getUpdateOnMap();

    }

    // Create GoogleApiClient instance
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    //Connecting GooGle api client on start()
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    //Disconnecting google api client on start()
    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }

    // MARK : PERMISSIONS HANDLING
    private final int REQ_PERMISSION = 888;

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }

    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    Log.w(TAG, "permissionsDenied()");
                }
                break;
            }
        }
    }
    //-------------PERMISSIONS HANDLING FINISHED -------------


    private LocationRequest locationRequest;
    //    // Defined in mili seconds.
//    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL = 60;
    private final int FASTEST_INTERVAL = 40;

    // Start location Updates
    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (checkPermission())
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged [" + location + "]");
        lastLocation = location;
        writeActualLocation(location);
    }

    // GoogleApiClient.ConnectionCallbacks connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            getLastKnownLocation();
        } else {
            askPermission();
        }

    }


    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    // Get last known location
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if (checkPermission()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        } else askPermission();
    }

    private void writeActualLocation(Location location) {

//        String key;
//        key = root.child("players").getKey();
//        Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
        String playerReferenceId = getIntent().getStringExtra("playerId");
        DatabaseReference updatePlayerLocation = databaseReference.child(playerReferenceId);
        updatePlayerLocation.child("latitude").setValue(location.getLatitude());
        updatePlayerLocation.child("longitude").setValue(location.getLongitude());
        Log.d(TAG, "writeActualLocation: " + location.getLatitude() + location.getLongitude());

        float distanceInmeters = flagLocation.distanceTo(location);
//        Log.d(TAG, "writeActualLocation: $$$$$$$$$$$$$$$" + distanceInmeters);
        if(textView.getText().toString().isEmpty()) {
            textView.setText("");
        }
        else {
            textView.setText("" + distanceInmeters + "meters");

        }

    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }


}
package kanedenzil.playerapp;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class StartActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {


    EditText nameOfPerson;
    Button submit;
    Spinner spinnerTeam;

    FirebaseDatabase db;
    DatabaseReference root;
    private ChildEventListener childEventListener;
    List<Player> players = new ArrayList<>();

    private static final String TAG = StartActivity.class.getSimpleName();
    //    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("players").push();

    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent(context, StartActivity.class);
        intent.putExtra(NOTIFICATION_MSG, msg);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        nameOfPerson = (EditText) findViewById(R.id.editTextName);
        submit = (Button) findViewById(R.id.submit);
        spinnerTeam = (Spinner) findViewById(R.id.teamNames);

        db = FirebaseDatabase.getInstance();
        root = db.getReference();

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                submitClicked();

            }
        });

        // create GoogleApiClient
        createGoogleApi();


//        listenForEvent();
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
    private final int UPDATE_INTERVAL = 500;
    private final int FASTEST_INTERVAL = 100;

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
//        getLastKnownLocation();
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

        String key;
        key = root.child("players").getKey();
        Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "####################################### " + key);
//        root.child("players").child(uniqueReferenceID).child("latitude").setValue(location.getLatitude());
//        root.child("players").child(uniqueReferenceID).child("longitude").setValue(location.getLongitude());


//        DatabaseReference updatePlayerLocation = databaseReference.child(databaseReference.getKey());
        databaseReference.child("latitude").setValue(location.getLatitude());
        databaseReference.child("longitude").setValue(location.getLongitude());
        Log.d(TAG, "writeActualLocation: " + location.getLatitude() + location.getLongitude());
//        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

//    private Marker locationMarker;
//    private void markerLocation(LatLng latLng) {
//        Log.i(TAG, "markerLocation("+latLng+")");
//        String title = latLng.latitude + ", " + latLng.longitude;
//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(latLng)
//                .title(title);
//
//            if ( locationMarker != null )
//                locationMarker.remove();
//            locationMarker = map.addMarker(markerOptions);
//            float zoom = 14f;
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
//            map.animateCamera(cameraUpdate);
//
//    }


//when submit button is clicked


    public void submitClicked() {
        String name = nameOfPerson.getText().toString().trim();
        String team = spinnerTeam.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name)) {

            Player player = new Player(databaseReference.getKey(), name,team, 0.0, 0.0);

            players.add(player);
            databaseReference.setValue(player);
            if (checkPermission()) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                getLastKnownLocation();
            } else {
                askPermission();
            }

            Toast.makeText(this, "Player is added", Toast.LENGTH_LONG).show();


            Intent intent = new Intent(this, DirectionActivity.class);
            intent.putExtra("name", nameOfPerson.getText().toString());
            intent.putExtra("team", team);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Your should enter your name", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onClick(View v) {

    }

//    public void listenForEvent(){
//        if(childEventListener == null) {
//            childEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    String key = dataSnapshot.getKey();
//                    Player p = dataSnapshot.getValue(Player.class);
//                    Log.d(TAG, "onChildChanged: CHILD HAS CHANGED");
//                    for(Player pl : players){
//                        if(pl.getPlayerId().equals(key)){
//                            pl.latitude = p.latitude;
//                            pl.longitude = p.longitude;
//                            break;
//                        }
//                    }
//
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                    String key = dataSnapshot.getKey();
//                    Player p = dataSnapshot.getValue(Player.class);
//                    Log.d(TAG, "onChildChanged: CHILD HAS CHANGED");
//                    for(Player pl : players){
//                        if(pl.getPlayerId().equals(key)){
//                            pl.latitude = p.latitude;
//                            pl.longitude = p.longitude;
//                            break;
//                        }
//                    }
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            };
//        }
//        root.child("players").addChildEventListener(childEventListener);
//    }
}


package kanedenzil.playerapp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private ValueEventListener valueEventListener;
//  private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("players");
    DatabaseReference databaseReferenceflag = FirebaseDatabase.getInstance().getReference().child("flag");
    DatabaseReference databaseWinReference = FirebaseDatabase.getInstance().getReference().child("WIN");
    private static final String TAG = DirectionActivity.class.getSimpleName();
    Location flagLocation = new Location("");
    Location myCurrentLocation = new Location("");
    Location prisonCenterLocation = new Location("");
    Location geofenceCenterLocation = new Location("");
    List<Location> locationList = new ArrayList<>();
    String teamName;
    String playerName;
    String playerReferenceId;
    DatabaseReference updatePlayer;
    Boolean isOutFromArena = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        playerReferenceId = getIntent().getStringExtra("playerId");
        updatePlayer = databaseReference.child(playerReferenceId);

        Flag flag = new Flag(false);
        databaseReferenceflag.setValue(flag);
        teamName = getIntent().getExtras().getString("team");
        playerName = getIntent().getExtras().getString("name");
        textView = findViewById(R.id.textView3);
        createGoogleApi();

        googleApiClient.connect();
        Log.d(TAG, " googleApiClient HAS BEEN CONNECTED");
        // mMap.setMyLocationEnabled(true)

        if(teamName.equals("Team-A")) {

            flagLocation.setLatitude(43.773705);
            flagLocation.setLongitude(-79.335894);
        }
        else{

            flagLocation.setLatitude(43.772738);
            flagLocation.setLongitude(-79.333472);
        }

//        getUpdateOnMap();
        databaseReferenceflag.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ==============DATASanapshot==========="+dataSnapshot.toString());
//                List<Player> players =  new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: ==============snapshot==========="+snapshot.toString());
                    Boolean flagValue = (Boolean) snapshot.getValue();
                    Log.d(TAG, "onDataChange: "+ flagValue);
                    if(flagValue.equals(true)){
                        Toast.makeText(DirectionActivity.this, "FLAG HAS BEEN PICKED by " + teamName, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseWinReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ==============DATASanapshot==========="+dataSnapshot.toString());
//                List<Player> players =  new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: ==============snapshot==========="+snapshot.toString());
                    Boolean winValue = (Boolean) snapshot.getValue();
                    Log.d(TAG, "onDataChange: "+ winValue);
                    if(winValue.equals(true)){
                        Toast.makeText(DirectionActivity.this, "" + teamName + "has Won. Game Over", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ==============Sanapshot==========="+dataSnapshot.toString());
                List<Player> players =  new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Player player = snapshot.getValue(Player.class);
                    Boolean flagvalue1 = player.flagValue;
                            players.add(player);
                    Log.d(TAG, "Name: "+ player.playerName);
//                    if(flagvalue1.equals(true)){
//                        Toast.makeText(DirectionActivity.this, "FLAG HAS BEEN PICKED!", Toast.LENGTH_SHORT).show();
//                    }
                }
                setPlayerDistance(players);
            }
            @Override
            public void onCancelled(DatabaseError databaseError){
            }
        });

    }

    public void setPlayerDistance(List<Player> players){
        if(players.size() != 0) {
            List<Float> distanceList =  new ArrayList<>();
            Location opponentCurrentLocation = new Location("");
            Location partnerCurrentLocation = new Location("");


            for (Player player : players) {

                geofenceCenterLocation.setLatitude(43.773219);
                geofenceCenterLocation.setLongitude(-79.334874);
                float distanceFromGeoCenter = geofenceCenterLocation.distanceTo(myCurrentLocation);

                Log.d(TAG, "setPlayerDistance: +_+_+_+_+_+_+_");
                if (isOutFromArena == true && player.prisonValue.equals(false)) {
                    Log.d(TAG, "setPlayerDistance: ////////////////////////" + distanceFromGeoCenter);
                    Toast.makeText(this, "You are out of Arena. Move To the Prison", Toast.LENGTH_SHORT).show();
                }

                prisonCenterLocation.setLatitude(43.775793);
                prisonCenterLocation.setLongitude(-79.336210);
                float distanceFromPrisonCenter = prisonCenterLocation.distanceTo(myCurrentLocation);
                Log.d(TAG, "distanceFromPrisonCenter: " + distanceFromPrisonCenter);
                if (distanceFromPrisonCenter < 30 && player.prisonValue.equals(true)) {
                    Toast.makeText(this, "You are in Prison Wait for your team player to Rescue you", Toast.LENGTH_SHORT).show();
                }

                if(player.flagValue.equals(true) && player.prisonValue.equals(false)){

                }

                if (!player.playerName.equals(playerName)) {

                    if (teamName.equals("Team-A")) {
                        if (player.playerTeam.equals("Team-B")) {
                            opponentCurrentLocation.setLatitude(player.latitude);
                            opponentCurrentLocation.setLongitude(player.longitude);

                            float distanceInmeters = opponentCurrentLocation.distanceTo(myCurrentLocation);
                            if (distanceInmeters < 5 && player.prisonValue.equals(false)) {
                                Toast.makeText(this, "You are caught by " + player.playerName + " . Now You will be taken to prison", Toast.LENGTH_SHORT).show();
                            }
                            String distanceInmetersString = String.format("%.2f", distanceInmeters);
                            Log.d(TAG, "setPlayerDistance: " + distanceInmetersString);
                        }
                        //OF THE SAME TEAM
                        else {
                            partnerCurrentLocation.setLatitude(player.latitude);
                            partnerCurrentLocation.setLongitude(player.longitude);

                            float partnerdistanceInmeters1 = partnerCurrentLocation.distanceTo(myCurrentLocation);
                            if (partnerdistanceInmeters1 < 5 && player.prisonValue.equals(true)) {
                                Toast.makeText(this, "You have been rescued. Back in the Game", Toast.LENGTH_SHORT).show();

                            }

                        }
                    } else {
                        if (player.playerTeam.equals("Team-A")) {
                            opponentCurrentLocation.setLatitude(player.latitude);
                            opponentCurrentLocation.setLongitude(player.longitude);
                            float distanceInmeters = opponentCurrentLocation.distanceTo(myCurrentLocation);
                            if (distanceInmeters < 5) {
                                Toast.makeText(this, "You caught " + player.playerName + ". Escort "+ player.playerName +" to prison", Toast.LENGTH_SHORT).show();
                            }
                            String distanceInmetersString = String.format("%.2f", distanceInmeters);
                            Log.d(TAG, "setPlayerDistance: iAM on team B" + distanceInmetersString);
                        }
                        //OF THE SAME TEAM
                        else {
                            partnerCurrentLocation.setLatitude(player.latitude);
                            partnerCurrentLocation.setLongitude(player.longitude);

                            float partnerdistanceInmeters = partnerCurrentLocation.distanceTo(myCurrentLocation);
                            if (partnerdistanceInmeters < 5 && player.prisonValue.equals(true)) {
                                Toast.makeText(this, "You have been rescued.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
            }
        }
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
        Flag flag = new Flag(false);
        databaseReferenceflag.setValue(flag);
        databaseWinReference.child("hasWon").setValue(false);
        updatePlayer.child("flagValue").setValue(false);
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
//                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
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
//            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
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

        updatePlayer.child("latitude").setValue(location.getLatitude());
        updatePlayer.child("longitude").setValue(location.getLongitude());
        myCurrentLocation = location;
        Log.d(TAG, "writeActualLocation: " + location.getLatitude() + location.getLongitude());

        float distanceInmeters = flagLocation.distanceTo(location);
        float distanceFromPrison = prisonCenterLocation.distanceTo(location);
        String distanceInmetersString = String.format("%.2f", distanceInmeters);

        geofenceCenterLocation.setLatitude(43.773219);
        geofenceCenterLocation.setLongitude(-79.334874);
        float distanceFromGeoCenter = geofenceCenterLocation.distanceTo(location);

        Log.d(TAG, "setPlayerDistance: ***" + distanceFromGeoCenter);
        if (distanceFromGeoCenter > 15.00) {

            isOutFromArena = true;
        }else{
            Log.d(TAG, "isOutFromArena"  + isOutFromArena);
            isOutFromArena = false;
        }

//        Log.d(TAG, "writeActualLocation: $$$$$$$$$$$$$$$" + distanceInmeters);
        if(textView.getText().toString().isEmpty()) {
            textView.setText("");
        }
        else {
            textView.setText("" + distanceInmetersString );
            if(distanceInmeters<10){
                Flag flag = new Flag(true);
                databaseReferenceflag.setValue(flag);
                updatePlayer.child("flagValue").setValue(true);
            }
            if(distanceFromPrison<30){
                updatePlayer.child("prisonValue").setValue(true);
            }else{
                updatePlayer.child("prisonValue").setValue(false);
            }

            Location winning1 = new Location("");
            winning1.setLatitude(43.772949);
            winning1.setLongitude(-79.336022);
            float distFromWinning1 = winning1.distanceTo(location);
            Location winning2 = new Location("");
            winning2.setLatitude(43.772978);
            winning2.setLongitude(-79.335909);
            float distFromWinning2 = winning2.distanceTo(location);
            Location winning3 = new Location("");
            winning3.setLatitude(43.773003);
            winning3.setLongitude(-79.335795);
            float distFromWinning3 = winning3.distanceTo(location);
            Location winning4 = new Location("");
            winning4.setLatitude(43.773031);
            winning4.setLongitude(-79.335678);
            float distFromWinning4 = winning4.distanceTo(location);
            Location winning5 = new Location("");
            winning5.setLatitude(43.773062);
            winning5.setLongitude(-79.335527);
            float distFromWinning5 = winning5.distanceTo(location);
            Location winning6 = new Location("");
            winning6.setLatitude(43.773111);
            winning6.setLongitude(-79.335321);
            float distFromWinning6 = winning6.distanceTo(location);
            Location winning7 = new Location("");
            winning7.setLatitude(43.773257);
            winning7.setLongitude(-79.334697);
            float distFromWinning7 = winning7.distanceTo(location);
            Location winning8 = new Location("");
            winning8.setLatitude(43.773286);
            winning8.setLongitude(-79.334559);
            float distFromWinning8 = winning8.distanceTo(location);
            Location winning9 = new Location("");
            winning9.setLatitude(43.773309);
            winning9.setLongitude(-79.334444);
            float distFromWinning9 = winning9.distanceTo(location);
            Location winning10 = new Location("");
            winning10.setLatitude(43.773353);
            winning10.setLongitude(-79.334232);
            float distFromWinning10 = winning10.distanceTo(location);

            if(distFromWinning1<3 || distFromWinning2<3 || distFromWinning3<3 || distFromWinning4<3 || distFromWinning5<3 || distFromWinning6<3 || distFromWinning7<3 || distFromWinning8<3 || distFromWinning9<3 || distFromWinning10<3){
                databaseWinReference.child("hasWon").setValue(true);
            }


        }
    }
    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }
}
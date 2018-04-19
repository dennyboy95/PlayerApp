package kanedenzil.playerapp;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends AppCompatActivity {

    FirebaseDatabase database;
    TextView textView;
    DatabaseReference root;
<<<<<<< HEAD
=======
    Double latt;
    Double longg;
    LocationManager locationManager;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private static final String TAG = DirectionActivity.class.getSimpleName();
    Location flagLocation = new Location("");
>>>>>>> 53980a0a64b555c8bd4e7396d2ea1cedcace80d8

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        database = FirebaseDatabase.getInstance();
        root = database.getReference();
        textView = findViewById(R.id.textView3);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("players");

//       if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // user did NOT give permission
//            Log.d("surinderbhago", "no permission granted, requesting p now...");
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 5);
//
//
//        } else {

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

        getUpdateOnMap();
    }

    public void getUpdateOnMap(){
//        = null;
        if(valueEventListener  == null){
            valueEventListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: ==============Sanapshot==========="+dataSnapshot.toString());
                    List<Player> players =  new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Player player = snapshot.getValue(Player.class);
                        players.add(player);
                        Log.d(TAG, "Name: "+ player.playerName);
                    }
                    setPlayerMaker(players);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            databaseReference.addValueEventListener(valueEventListener);

        }
    }

    public void setPlayerMaker(List<Player> players){
        if(players.size() != 0) {
//            map.clear();
            String value = getIntent().getExtras().getString("name");
            for (Player player : players) {
                if(player.playerName == value) {
                    Location loc = new Location("");
                    loc.setLatitude(player.latitude);
                    loc.setLongitude(player.longitude);


                    float distanceInmeters = flagLocation.distanceTo(loc);

                    textView.setText(""+distanceInmeters+"meters");

//                map.addMarker(new MarkerOptions().position(latLng).title(player.getPlayerName()));
                }
            }
        }
    }







}
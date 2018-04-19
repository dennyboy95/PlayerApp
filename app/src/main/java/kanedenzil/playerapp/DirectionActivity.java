package kanedenzil.playerapp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DirectionActivity extends AppCompatActivity {

    FirebaseDatabase database;
    TextView textView;
    DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        database = FirebaseDatabase.getInstance();
        root = database.getReference();
        textView = (TextView) findViewById(R.id.textView3) ;


       if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // user did NOT give permission
            Log.d("surinderbhago", "no permission granted, requesting p now...");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 5);


        } else {

            // mMap.setMyLocationEnabled(true);
            Location location = .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                Location loc1 = new Location("");
                loc1.setLatitude(43.729169);
                loc1.setLongitude(-79.608217);
                float distanceInMeters = loc1.distanceTo(location);
                Log.d("surinderbhago", String.valueOf(distanceInMeters));

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                User user = new User(latitude,longitude);
                Log.d("kahlonn", String.valueOf(location));
                // String key = root.child("Players").push().getKey();
                // Log.d("kahlonn", key);
                key = root.child("Players").push().getKey();
                root.child("Players").child(key).setValue(user);
                //String key = root.child("Players").push().getKey();
                Log.d("keyyyy", key);
                Log.d("surinderbhago", String.valueOf(latitude));
                System.out.print(latitude);
            }

            //  System.out.print(mMap.g);
            // Double latitude = mMap.getMyLocation().getLatitude();
            // Double longitude = mMap .getMyLocation().getLongitude();
            // LatLng sydney = new LatLng( latitude, longitude);
            //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
        attachLocationListener();


    }


}

    Double latt;
    Double longg;
    private void attachLocationListener() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                User user = new User(latitude,longitude);
                Log.d("kahlonn", String.valueOf(location));;
                // String key = root.child("Players").push().getKey();
                Log.d("Node ID", key);
                root.child("Players").child(key).setValue(user);


                root.child("Flag").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User usr = dataSnapshot.getValue(User.class);
                        latt = usr.latitude;
                        longg = usr.longitude;
                        Location loc1 = new Location("");
                        loc1.setLatitude(latt);
                        loc1.setLongitude(longg);
                        Log.d("Flag latitude", String.valueOf(latt));
                        float distanceInMeters = loc1.distanceTo(location);
                        textView.setText("Flag is "+distanceInMeters+" meters away from you...Keep trying..");
                        /*AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Flag is at distance of "+distanceInMeters+"meters");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();*/
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // user did NOT give permission
            Log.d("surinderbhago", "no permission granted, requesting p now...");
            // ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION}, 5);


        } else {
            Log.d("surinderbhago", "Yup, I have permission");
            mMap.setMyLocationEnabled(true);
            // System.out.print(mMap.g);
            try {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                LatLng myLocation = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
            }
            catch(Exception e){
                Log.d("error", String.valueOf(e));
            }
        }

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng( 31.817575, 75.184714);
        //  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "GO GO GO!", Toast.LENGTH_SHORT).show();
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //  mMap.setMyLocationEnabled(true);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location != null)
                        {
                            Location location1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            Double latitude = location1.getLatitude();
                            Double longitude = location1.getLongitude();
                            LatLng myLocation = new LatLng( latitude, longitude);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                            mMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );
                        }
                    }
                } else {
                    // permission was denied!
                    Toast.makeText(this, "Sorry, person clicked DENY", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }
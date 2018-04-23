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
import android.widget.TextView;
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


public class StartActivity extends AppCompatActivity {


    EditText nameOfPerson;
    Button submit;
    Spinner spinnerTeam;

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


        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submitClicked();
            }
        });



    }


//when submit button is clicked
    public void submitClicked() {
        String name = nameOfPerson.getText().toString().trim();
        String team = spinnerTeam.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name)) {

            Player player = new Player(databaseReference.getKey(), name,team, 0.0, 0.0, false);

            players.add(player);
            databaseReference.setValue(player);

            Toast.makeText(this, "Player is added", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, DirectionActivity.class);
            intent.putExtra("name", nameOfPerson.getText().toString());
            intent.putExtra("playerId", databaseReference.getKey());
            intent.putExtra("team", team);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Your should enter your name", Toast.LENGTH_LONG).show();

        }

    }

}


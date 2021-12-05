package com.example.projectchibi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;

public class ChibiActivity extends FragmentActivity  implements OnMapReadyCallback{

    public static final String GENERAL_SHARED_PREFS = "generalSharedPrefs";
    public static final String LOGGED_IN = "loggedIn";

    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;
    private FirebaseAuth mAuth;
    public boolean loggedIn = false;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    private Context context = this;
    private ImageView chibiImageView;
    private RelativeLayout chibiLayout;

    private Button walkerButton;
    private Chibi currentChibi;

    //private LocationManager locationManager;
    private SupportMapFragment mapFragment;
    private LatLng latLng;
    private GoogleMap map;
    private String personId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chibiactivitydesign);
        chibiLayout = findViewById(R.id.chibiLayout);
        chibiImageView = (ImageView) findViewById(R.id.chibiImageView);
        chibiImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.jalter_chibi));
        currentChibi = new Chibi();
        walkerButton = (Button) findViewById(R.id.walkerButton);
        walkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExerciseActivity();
            }
        });
        loadPrefData();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //Somehow fixed itself ???
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //signIn();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        /*LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                map.addMarker(new MarkerOptions().position(latLng).title("I am here right now"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
            }
        };
        if(ActivityCompat.checkSelfPermission(ChibiActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }*/
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        chibiLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    void loadPrefData()
    {
        //General preferences
        SharedPreferences sharedPreferences = getSharedPreferences(GENERAL_SHARED_PREFS, MODE_PRIVATE);
        loggedIn = sharedPreferences.getBoolean(LOGGED_IN, false);
    }

    void savePrefData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(GENERAL_SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGGED_IN, loggedIn);
        editor.apply();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loggedIn = false;
                        savePrefData();
                    }
                });
    }

    private void openExerciseActivity()
    {
        //signOut();
        Intent intent = new Intent(getBaseContext(), ExerciseActivity.class);
        intent.putExtra("Data", currentChibi);
        intent.putExtra("userId", personId);
        //intent.putExtra(this);?
        startActivityForResult(intent, 1);
        /**/
        //finish();
    }

    //protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
            loggedIn = true;
            savePrefData();
        } catch (ApiException e) {
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            personId = acct.getId();
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference("users");
            try{
                User u = new User(personGivenName, personFamilyName, personEmail, String.valueOf(personId.hashCode()) + personGivenName);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //map = googleMap;
        /*LatLng kaunas = new LatLng(54.898521, 23.903597);
        if(latLng != null)
        {
            googleMap.addMarker(new MarkerOptions().position(latLng).title("I am here right now"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
            Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();
        } else {
            googleMap.addMarker(new MarkerOptions().position(kaunas).title("Kaunas"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kaunas, 12.0f));
            Toast.makeText(context, "Kaunas", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

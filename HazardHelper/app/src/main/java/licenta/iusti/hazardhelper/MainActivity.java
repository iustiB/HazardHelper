package licenta.iusti.hazardhelper;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import licenta.iusti.hazardhelper.domain.CustomPlace;
import licenta.iusti.hazardhelper.domain.Hazard;
import licenta.iusti.hazardhelper.utils.GooglePlacesHelper;

import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapLongClickListener, GooglePlacesHelper.PlacesHelperListener, GoogleMap.OnInfoWindowClickListener {

    public static final String HAZARDS_DB_KEY = "hazards";
    public static final String SAFEPOINTS_DB_KEY = "safepoints";
    private GoogleMap mMap;
    private TextView mRadiusTextView;
    private int mRadiusValue;
    private Circle mHazardRadius;
    private CircleOptions mHazardRadiusCircle;

    private boolean mHasAddHazardStarted = false;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    int PLACE_PICKER_REQUEST = 1105;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 6395;

    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();


    FloatingActionButton fab2, fabAddHazard, fabAddSafepoint, fabMain;
    LinearLayout fabLayout1, fabLayout2, fabLayout3;
    View fabBGLayout;
    boolean isFABOpen = false;
    private ArrayList<Marker> mMapMarkers = new ArrayList<>();
    private ArrayList<Circle> mMapCircles = new ArrayList<>();
    private SeekBar mRadiusSeekbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //radius size click listeners
        findViewById(R.id.radius_minus_btn).setOnClickListener(this);
        findViewById(R.id.radius_plus_btn).setOnClickListener(this);
//        findViewById(R.id.fab_add).setOnClickListener(this);
        findViewById(R.id.save_hazard_btn).setOnClickListener(this);
        mRadiusTextView = (TextView) findViewById(R.id.radius_text_view);
        mRadiusTextView.setText(mRadiusValue + "");

        mRadiusSeekbar = (SeekBar) findViewById(R.id.seek_bar);
        mRadiusSeekbar.setEnabled(false);
        mRadiusSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRadiusTextView.setText(mRadiusValue + "");
                convertSeekbarProgresToRadius();

                mHazardRadius.setRadius(mRadiusValue);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        // FAB configurations

        fabLayout1 = (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) findViewById(R.id.fabLayout2);
        fabLayout3 = (LinearLayout) findViewById(R.id.fabLayout3);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2); // DE NADA
        fabMain = (FloatingActionButton) findViewById(R.id.fab);
        fabAddHazard = (FloatingActionButton) findViewById(R.id.fab_add_hazard);
        fabAddSafepoint = (FloatingActionButton) findViewById(R.id.fab_add_safepoint);
        fabAddSafepoint.setOnClickListener(this);
        fabAddHazard.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fabBGLayout = findViewById(R.id.fabBGLayout);

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });
    }


    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fabMain.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fabMain.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_search_button:
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(TYPE_FILTER_ESTABLISHMENT)
                            .build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setBoundsBias(new LatLngBounds(mMap.getCameraPosition().target,mMap.getCameraPosition().target))
//                                    .setFilter(typeFilter)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
//        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {

            }
        });
        populateHazardsMap();


        // Atttributes for hazard radius
//        mHazardRadius.setClickable(true);
//        mHazardRadius.setStrokeColor(Color.GREEN);
//        mHazardRadius.setFillColor(Color.argb(128, 255, 0, 0));
//        mHazardRadius.setStrokeWidth(10);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, AddPlaceDetailsActivity.class);
                intent.putExtra(AddPlaceDetailsActivity.GOOGLE_PLACE_ID_EXTRA, place.getId());
                startActivity(intent);
            }
        }
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                Log.i("TAG", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onBackPressed() {///TODO: ask to exit dialog
        if (isFABOpen) {
            closeFABMenu();
        } else {
            super.onBackPressed();
        }
    }

    private void populateHazardsMap() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(HAZARDS_DB_KEY);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (Circle circle : mMapCircles) {
                    circle.remove();
                }
                mMapCircles.clear();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    Hazard hazard = iterator.next().getValue(Hazard.class);
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(hazard.getLatitude(), hazard.getLongitude()))
                            .radius(hazard.getHazardRadius())
                            .strokeWidth(10)
                            .strokeColor(Color.GREEN)
                            .fillColor(Color.argb(64, 255, 0, 0))
                            .clickable(true));

                    mMapCircles.add(circle);
                    circle.setTag(mMapCircles.indexOf(circle));
                    i++;

                    System.out.println(hazard.getHazardRadius() + "-------------------------------------");

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Hazard read failed: " + databaseError.getCode());
            }
        });


    }


    private void getSafepointsFromDB() {

        final ArrayList<CustomPlace> result = new ArrayList<>();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(SAFEPOINTS_DB_KEY);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result.clear();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    CustomPlace place = iterator.next().getValue(CustomPlace.class);
                    result.add(place);
                }
                onSafepointsFetched(result);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();


    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
        }


        getSafepointsFromDB();


        //get safepoints firebase
        //convert
        // show on map


    }

    public void onSafepointsFetched(ArrayList<CustomPlace> result) {
        for (Marker marker : mMapMarkers) {
            marker.remove();
            Log.e("TAG", "Place removed");

        }
        GooglePlacesHelper placesHelper = new GooglePlacesHelper(this, mGoogleApiClient);
        for (CustomPlace safepoint : result) {
            placesHelper.convertSinglePlace(safepoint);
            Log.e("TAG", "Place draw");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onClick(View v) {
        if (isFABOpen) {
            closeFABMenu();
        }
        switch (v.getId()) {

            case R.id.radius_minus_btn:
                decreaseRadius();
                break;
            case R.id.radius_plus_btn:
                increaseRadius();
                break;
            case R.id.fab_add_hazard:
                initializeAddingHazard();
                break;
            case R.id.save_hazard_btn:
                saveHazard();
                break;
            case R.id.fab_add_safepoint:
                Log.e("TAG", "Add safepoint pressed");
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.toolbar_search_button:
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(TYPE_FILTER_ESTABLISHMENT)
                            .build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setBoundsBias(new LatLngBounds(mMap.getCameraPosition().target,mMap.getCameraPosition().target))
//                                    .setFilter(typeFilter)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
        }
    }

    private void saveHazard() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(HAZARDS_DB_KEY).push();
        myRef.setValue(new Hazard(mHazardRadius.getCenter().latitude, mHazardRadius.getCenter().longitude, mRadiusValue));
        finalizeAddingHazard();
    }

    private void finalizeAddingHazard() {
        mHasAddHazardStarted = false;
        mHazardRadius.remove();
        mRadiusSeekbar.setEnabled(false);
        fabMain.setVisibility(View.VISIBLE);
        findViewById(R.id.add_hazard_layout).setVisibility(View.GONE);
    }

    private void
    initializeAddingHazard() {
        mHasAddHazardStarted = true;
        fabMain.setVisibility(View.GONE);
        findViewById(R.id.add_hazard_layout).setVisibility(View.VISIBLE);

    }

    private void increaseRadius() {
        mRadiusValue += 100;
        mRadiusTextView.setText(mRadiusValue + "");
        mHazardRadius.setRadius(mRadiusValue);
        if (mRadiusValue > 100) {
            View minusBtn = findViewById(R.id.radius_minus_btn);
            minusBtn.getBackground().setColorFilter(null);
            minusBtn.setEnabled(true);

        }

    }

    private void decreaseRadius() {
        mRadiusValue -= 100;
        mRadiusTextView.setText(mRadiusValue + "");
        mHazardRadius.setRadius(mRadiusValue);
        if (mRadiusValue < 200) {

            View minusBtn = findViewById(R.id.radius_minus_btn);
            minusBtn.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
            minusBtn.setEnabled(false);
        }

    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mHasAddHazardStarted) {
            if (mHazardRadius == null) {
                mRadiusSeekbar.setEnabled(true);
                convertSeekbarProgresToRadius();
                System.out.print("Creating circle");
                mHazardRadiusCircle = new CircleOptions()
                        .center(latLng)
                        .radius(mRadiusValue)
                        .strokeWidth(10)
                        .strokeColor(Color.GREEN)
                        .fillColor(Color.argb(128, 255, 0, 0))
                        .clickable(true);
                mHazardRadius = mMap.addCircle(mHazardRadiusCircle);
            }
            mHazardRadius.setCenter(latLng);
            mHazardRadius.setRadius(mRadiusValue);
            findViewById(R.id.radius_minus_btn).setEnabled(true);
            findViewById(R.id.radius_plus_btn).setEnabled(true);
        }

    }

    private void convertSeekbarProgresToRadius() {
        mRadiusValue = (int) Math.pow(mRadiusSeekbar.getProgress(), 1.8) + 20;
    }

    @Override
    public void onPlaceFound(Place place,CustomPlace customPlace) {
        mMapMarkers.add(
                mMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .title(customPlace.getCategory())
                        .snippet("snippet test")
                ));

    }

    @Override
    public void onConversionFinished() {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // dialogul din android
    }
}

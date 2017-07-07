package licenta.iusti.hazardhelper.features;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

import licenta.iusti.hazardhelper.MainActivity;
import licenta.iusti.hazardhelper.R;
import licenta.iusti.hazardhelper.domain.Safepoint;
import licenta.iusti.hazardhelper.utils.GooglePlacesHelper;
import licenta.iusti.hazardhelper.utils.ImageAdapterGridView;

public class AddPlaceDetailsActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GooglePlacesHelper.PlacesHelperListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String GOOGLE_PLACE_ID_EXTRA = "google_place_id";
    private EditText mPlaceNameEditText;
    private Button mSaveBtn;
    private GooglePlacesHelper placesHelper;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<String> selectedUtilities = new ArrayList<>();
    String placeId;
    private TextView mGridviewInstructions;

    GridView androidGridView;


    public ArrayList<String> mUtilitiesImages = new ArrayList<>(Arrays.asList(
            "ic_utilities_water",
            "ic_utilities_food",
            "ic_utilities_medic",
            "ic_utilities_bed",
            "ic_utilities_phone",
            "ic_utilities_pills",
            "ic_utilities_toilets",
            "ic_utilities_power_supply",
            "ic_utilities_shower",
            "ic_utilies_internet"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place_details);
        mPlaceNameEditText = (EditText) findViewById(R.id.place_category);
        mSaveBtn = (Button) findViewById(R.id.btn_save_safepoint);
        mSaveBtn.setOnClickListener(this);
        mGridviewInstructions = (TextView) findViewById(R.id.gridview_insructions);
        placeId = getIntent().getStringExtra(GOOGLE_PLACE_ID_EXTRA);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();

        androidGridView = (GridView) findViewById(R.id.utilities_gridview);
        androidGridView.setAdapter(new ImageAdapterGridView(this, mUtilitiesImages));

        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                if (selectedUtilities.size() > 1 && mUtilitiesImages.get(position) == selectedUtilities.get(0)) {
                    Toast.makeText(getBaseContext(), "To deselect main utility, you must deselect others first.", Toast.LENGTH_LONG).show();

                } else {
                    if (v.getBackground() == null) {
                        if (selectedUtilities.size() == 0) {
                            v.setBackground(ContextCompat.getDrawable(AddPlaceDetailsActivity.this, R.drawable.gridview_main_utility_bg));
                            selectedUtilities.add(mUtilitiesImages.get(position));
                            mGridviewInstructions.setText("Now choose secondary utilities you can find here:");
                            mSaveBtn.setEnabled(true);
                        } else {
                            v.setBackground(ContextCompat.getDrawable(AddPlaceDetailsActivity.this, R.drawable.gridview_secondary_utility_bg));
                            selectedUtilities.add(mUtilitiesImages.get(position));
                        }
                    } else {
                        v.setBackground(null);
                        selectedUtilities.remove(mUtilitiesImages.get(position));
                        if (selectedUtilities.size() == 0) {
                            mGridviewInstructions.setText("Please choose main utility you can find here:");
                            mSaveBtn.setEnabled(false);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save_safepoint) {
            saveNewPlace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void saveNewPlace() {
        Safepoint place = new Safepoint();
        place.setmGooglePlaceId(placeId);

        place.setCategory(mPlaceNameEditText.getText().toString());
        place.setUtilitiesImages(selectedUtilities);
        FirebaseDatabase.getInstance().getReference().child(MainActivity.SAFEPOINTS_DB_KEY).push().setValue(place);
        this.finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        placesHelper = new GooglePlacesHelper(this, mGoogleApiClient);
        Safepoint safepoint = new Safepoint();
        safepoint.setmGooglePlaceId(placeId);
        placesHelper.convertSinglePlace(safepoint);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onPlaceFound(Place place, Safepoint safepoint) {
        if (place.getName().toString().contains("\"N") || place.getName().toString().contains("\"E")) {
            mPlaceNameEditText.setHint("Please name this place");

        } else {
            mPlaceNameEditText.setText(place.getName());

        }

    }

    @Override
    public void onConversionFinished() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}

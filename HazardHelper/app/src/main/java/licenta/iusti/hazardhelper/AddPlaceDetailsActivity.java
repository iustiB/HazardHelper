package licenta.iusti.hazardhelper;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

import licenta.iusti.hazardhelper.domain.CustomPlace;
import licenta.iusti.hazardhelper.utils.GooglePlacesHelper;
import licenta.iusti.hazardhelper.utils.ImageAdapterGridView;

public class AddPlaceDetailsActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GooglePlacesHelper.PlacesHelperListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String GOOGLE_PLACE_ID_EXTRA = "google_place_id";
    private EditText mPlaceCategoryEditText;
    private Button mSaveBtn;
    private GooglePlacesHelper placesHelper;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<String> selectedUtilities = new ArrayList<>();
    String placeId;

    GridView androidGridView;



    ArrayList<String> mUtilitiesImages = new ArrayList<>(Arrays.asList("ic_utilies_internet",
            "ic_utilities_bed",
            "ic_utilities_food",
            "ic_utilities_medic",
            "ic_utilities_phone",
            "ic_utilities_pills",
            "ic_utilities_power_supply",
            "ic_utilities_shower",
            "ic_utilities_toilets",
            "ic_utilities_water"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place_details);
        mPlaceCategoryEditText = (EditText) findViewById(R.id.place_category);
        mSaveBtn = (Button) findViewById(R.id.btn);
        mSaveBtn.setOnClickListener(this);
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
        androidGridView.setAdapter(new ImageAdapterGridView(this,mUtilitiesImages));

        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                if(v.getBackground() == null) {
                    v.setBackground(ContextCompat.getDrawable(AddPlaceDetailsActivity.this, R.drawable.chat_item_background));
                    selectedUtilities.add(mUtilitiesImages.get(position));
                }else {
                    v.setBackground(null);
                    selectedUtilities.remove(selectedUtilities.get(position));
                }
                Toast.makeText(getBaseContext(), "Grid Item " + (position + 1) + " Selected", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn) {
            saveNewPlace();
        }
    }

    private void saveNewPlace() {
        CustomPlace place = new CustomPlace();
        place.setmGooglePlaceId(placeId);
        place.setCategory(mPlaceCategoryEditText.getText().toString());
        place.setUtilitiesImages(mUtilitiesImages);
        FirebaseDatabase.getInstance().getReference().child(MainActivity.SAFEPOINTS_DB_KEY).push().setValue(place);
        this.finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        placesHelper = new GooglePlacesHelper(this, mGoogleApiClient);
        CustomPlace customPlace = new CustomPlace();
        customPlace.setmGooglePlaceId(placeId);
        placesHelper.convertSinglePlace(customPlace);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onPlaceFound(Place place,CustomPlace customPlace) {
        Log.e("TAG", place.getName().toString());
    }

    @Override
    public void onConversionFinished() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}

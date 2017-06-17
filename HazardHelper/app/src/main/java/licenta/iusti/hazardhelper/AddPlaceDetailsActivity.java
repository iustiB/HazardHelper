package licenta.iusti.hazardhelper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.FirebaseDatabase;

import licenta.iusti.hazardhelper.domain.CustomPlace;
import licenta.iusti.hazardhelper.utils.GooglePlacesHelper;

public class AddPlaceDetailsActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GooglePlacesHelper.PlacesHelperListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String GOOGLE_PLACE_ID_EXTRA = "google_place_id";
    private EditText mPlaceCategoryEditText;
    private Button mSaveBtn;
    private GooglePlacesHelper placesHelper;
    private GoogleApiClient mGoogleApiClient;
    String placeId;

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

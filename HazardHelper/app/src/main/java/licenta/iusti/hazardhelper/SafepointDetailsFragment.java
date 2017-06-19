package licenta.iusti.hazardhelper;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;

import licenta.iusti.hazardhelper.domain.CustomPlace;
import licenta.iusti.hazardhelper.utils.DownloadImageTask;
import licenta.iusti.hazardhelper.utils.GooglePlacesHelper;
import licenta.iusti.hazardhelper.utils.PhotoTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class SafepointDetailsFragment extends Fragment implements OnStreetViewPanoramaReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GooglePlacesHelper.PlacesHelperListener {


    private String safepointID;
    private StreetViewPanorama mStreetViewPanorama;
    private GoogleApiClient mGoogleApiClient;
    private FrameLayout mStreetviewContainer;
    private ImageView mImage;
    private TextView mText;
    private View mImageContainer;

    public String getSafepointID() {
        return safepointID;
    }

    public void setSafepointID(String safepointID) {
        this.safepointID = safepointID;
    }

    public SafepointDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        FragmentManager fm = getChildFragmentManager();
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment) fm.findFragmentById(R.id.streetviewpanorama);
        if (streetViewPanoramaFragment == null) {
            streetViewPanoramaFragment = SupportStreetViewPanoramaFragment.newInstance();
            fm.beginTransaction().replace(R.id.streetviewpanorama, streetViewPanoramaFragment).commit();
        }
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_safepoint_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStreetviewContainer = (FrameLayout) view.findViewById(R.id.streetview_container);
        mImage = (ImageView) view.findViewById(R.id.google_image);
        mImageContainer = view.findViewById(R.id.image_container);
        mText = (TextView) view.findViewById(R.id.atributions_text);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;
        getSafepointById(safepointID);
    }

    private void getSafepointById(String safepointId) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(MainActivity.SAFEPOINTS_DB_KEY).child(safepointId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CustomPlace customPlace = dataSnapshot.getValue(CustomPlace.class);
                customPlace.setUid(dataSnapshot.getKey());
                onSafepointFetched(customPlace);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void onSafepointFetched(CustomPlace customPlace) {
        GooglePlacesHelper placesHelper = new GooglePlacesHelper(this, mGoogleApiClient);
        placesHelper.convertSinglePlace(customPlace);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();
        mGoogleApiClient.connect();


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPlaceFound(final Place place, final CustomPlace customPlace) {
        mStreetViewPanorama.setPosition(place.getLatLng());
        mStreetviewContainer.setVisibility(View.INVISIBLE);

        mStreetViewPanorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
                if (streetViewPanoramaLocation != null && streetViewPanoramaLocation.links != null) {
                    Log.i("Panorama set succes", customPlace.toString());
                    mStreetviewContainer.setVisibility(View.VISIBLE);

                } else {
                    mImageContainer.setVisibility(View.VISIBLE);
                    mStreetviewContainer.setVisibility(View.GONE);
                    placePhotosTask(place);
                }
            }
        });
    }

    private void placePhotosTask(Place place) {
        final String placeId = place.getId();

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(mGoogleApiClient) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                //mImageView.setImageResource(R.drawable.empty_photo);
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.
                    mImage.setImageBitmap(attributedPhoto.bitmap);

                    // Display the attribution as HTML content if set.
                    if (attributedPhoto.attribution == null) {
                        mText.setVisibility(View.GONE);
                    } else {
                        mText.setVisibility(View.VISIBLE);
                        mText.setText(Html.fromHtml(attributedPhoto.attribution.toString()));
                    }

                }
            }
        }.execute(placeId);
    }

    @Override
    public void onConversionFinished() {

    }
}

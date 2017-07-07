package licenta.iusti.hazardhelper.utils;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import licenta.iusti.hazardhelper.domain.Safepoint;

/**
 * Created by Iusti on 6/13/2017.
 */

public class GooglePlacesHelper {


    private final PlacesHelperListener mListener;
    private GoogleApiClient mGoogleApiClient;

    int count;

    public interface PlacesHelperListener {
        void onPlaceFound(Place place,Safepoint safepoint);
        void onConversionFinished();
    }

    public GooglePlacesHelper(PlacesHelperListener listener, GoogleApiClient client) {
        this.mGoogleApiClient = client;
        this.mListener = listener;
    }

    public void convertSinglePlace(final Safepoint place) {
        if (place != null) {

            Places.GeoDataApi.getPlaceById(mGoogleApiClient, place.getmGooglePlaceId())
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                final Place myPlace = places.get(0);
                                myPlace.freeze();
                                mListener.onPlaceFound(myPlace, place);
                                Log.i("TAG", "Place found: " + myPlace.getName());
                            } else {
                                Log.e("TAG", "Place not found");
                            }
                            places.release();
                        }
                    });
        }
    }
}

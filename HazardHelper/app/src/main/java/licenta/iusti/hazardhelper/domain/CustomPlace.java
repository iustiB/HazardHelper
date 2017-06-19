package licenta.iusti.hazardhelper.domain;

import com.google.android.gms.location.places.Place;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by Iusti on 6/13/2017.
 */


public class CustomPlace {


    public CustomPlace(){

    }
    public String getmGooglePlaceId() {
        return mGooglePlaceId;
    }

    public void setmGooglePlaceId(String mGooglePlaceId) {
        this.mGooglePlaceId = mGooglePlaceId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    private String mGooglePlaceId;
    private String category;

    public String getUid() {

        return uid;

    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    private String uid;

    public ArrayList<String> getUtilitiesImages() {
        return utilitiesImages;
    }

    public void setUtilitiesImages(ArrayList<String> utilitiesImages) {
        this.utilitiesImages = utilitiesImages;
    }

    private ArrayList<String> utilitiesImages;

}

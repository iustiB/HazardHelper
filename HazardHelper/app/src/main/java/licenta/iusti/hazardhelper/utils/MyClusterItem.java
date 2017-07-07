package licenta.iusti.hazardhelper.utils;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyClusterItem implements ClusterItem {


    private final LatLng mPosition;
    private String uid;

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmSnippet(String mSnippet) {
        this.mSnippet = mSnippet;
    }

    private String mTitle;
    private String mSnippet;

    public void setmIconID(int mIconID) {
        this.mIconID = mIconID;
    }

    private int mIconID;

    public MyClusterItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public MyClusterItem(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public int getIconID() {
        return mIconID;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
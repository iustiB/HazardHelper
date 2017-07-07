package licenta.iusti.hazardhelper.domain;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Iusti on 4/11/2017.
 */

public class Hazard {

    private int hazardRadius;
    private double latitude;
    private double longitude;


    public Hazard(){}
    public Hazard(double latitude, double longitude, int hazardRadius) {
        this.latitude = latitude;
        this.longitude=longitude;
        this.hazardRadius = hazardRadius;
    }


    public int getHazardRadius() {
        return hazardRadius;
    }

    public void setHazardRadius(int hazardRadius) {
        this.hazardRadius = hazardRadius;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}

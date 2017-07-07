package licenta.iusti.hazardhelper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Iusti on 6/20/2017.
 */

public class CustomClusterRenderer extends DefaultClusterRenderer<MyClusterItem> {
    private final Context mContext;

    public CustomClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<MyClusterItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        setMinClusterSize(1);

    }

    @Override
    protected void onBeforeClusterItemRendered(MyClusterItem item, MarkerOptions markerOptions) {

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), item.getIconID());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false)));
    }

}
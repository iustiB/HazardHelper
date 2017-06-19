package licenta.iusti.hazardhelper.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Iusti on 6/19/2017.
 */

public class ImageAdapterGridView extends BaseAdapter {
    private final ArrayList<String> mutilitiesImages;
    private Context mContext;


    public ImageAdapterGridView(Context c,ArrayList<String>  utilitiesImages) {
        mContext = c;
        mutilitiesImages=utilitiesImages;
    }

    public int getCount() {
        return mutilitiesImages.size();
    }

    public Object getItem(int position) {
        return mutilitiesImages.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView mImageView;

        if (convertView == null) {
            mImageView = new ImageView(mContext);
            mImageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mImageView.setPadding(16, 16, 16, 16);

        } else {
            mImageView = (ImageView) convertView;
        }
        mImageView.setImageResource(mImageView.getContext().getResources().getIdentifier(mutilitiesImages.get(position),"drawable",mContext.getPackageName()));
        return mImageView;
    }
}
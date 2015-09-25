package movies.chekflix.com.popflix;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Sparta on 9/12/15.
 */
public class ImageAdapter extends ArrayAdapter<String>{
    private Context mContext;
    private List<String> movieList;
    private int resource;
    private int resourceID;
    private final String LOG_TAG = ImageAdapter.class.getSimpleName();
    public ImageAdapter(Context c,int resource, int resourceId, List<String> movieList) {
        super(c,resource,resourceId,movieList);
       mContext = c;
        this.resource = resource;
        this.resourceID = resourceId;
        this.movieList = movieList;
    }

    public int getCount() {
        return movieList.size();
    }

    public String getItem(int position) {
        return movieList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(550,750));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
          //  imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        String movieString = getItem(position);
        String[] movieValues = movieString.split("#");
        String imageValue = movieValues[1];

        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185//" + imageValue).error(R.drawable.no_image_available).into(imageView);
        return imageView;
    }

    public void replace(List<String> movieList) {
        this.movieList.clear();
        this.movieList.addAll(movieList);
        notifyDataSetChanged();
    }

}

package movies.chekflix.com.popflix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

        private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

        private String mMovieStr;

        public DetailActivityFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mMovieStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                //parsing the values
                String[] movieImageValues = mMovieStr.split("#");

                String title = movieImageValues[2];
                if(title == null)
                    title = "No title Found";
                else
                    title = "Title: " + title;

                String posterPath = movieImageValues[1];
                if(posterPath == null)
                    posterPath = "No Image Available";

                String overView = movieImageValues[3];
                if(overView == null)
                    overView = "No overView Found";
                else
                    overView = "Overview: " + overView;

                String voterAverage = movieImageValues[4];
                if(voterAverage == null)
                    voterAverage = "No voterAverage Found";
                else
                    voterAverage = "Average Rating: " + voterAverage;

                String releaseDate = movieImageValues[5];
                if(releaseDate == null)
                    releaseDate = "No release date Found";
                else
                    releaseDate = "Release Date: " + releaseDate;

                ((TextView) rootView.findViewById(R.id.detail_title))
                        .setText(title);

                ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image);
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185//" + posterPath).error(R.drawable.no_image_available).into(imageView);

                ((TextView) rootView.findViewById(R.id.detail_overview))
                        .setText(overView);
                ((TextView) rootView.findViewById(R.id.detail_userRating))
                        .setText(voterAverage);
                ((TextView) rootView.findViewById(R.id.detail_releaseDate))
                        .setText(releaseDate);
            }

            return rootView;
        }
}

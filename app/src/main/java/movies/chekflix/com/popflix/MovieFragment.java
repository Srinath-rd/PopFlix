package movies.chekflix.com.popflix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MovieFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private ImageAdapter imageAdapter;
    private String mParam2;

  //  private OnFragmentInteractionListener mListener;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_movie, container, false);
        View rootView = inflater.inflate(R.layout.fragment_movie,container,false);
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        imageAdapter =  new ImageAdapter(getActivity(), R.layout.grid_item_movie, R.id.grid_item_movie_imageview, new ArrayList<String>());
        gridview.setAdapter(imageAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();
                String movieDetail = imageAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movieDetail);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortByKey = prefs.getString(getString(R.string.sort_by_key), "mp");
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(sortByKey);

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<String>> {


        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected List<String> doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            String sortByKey = params[0];
            String key = null;
            if(sortByKey.equals("mp"))
                key = "popularity.desc";
            else if(sortByKey.equals("hr"))
                key = "vote_average.desc";
            /// / These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String SORT_PARAM_VALUE = key;
                final String API_KEYPARAM = "api_key";
                final String API_KEYPARAM_VALUE = "d094aee99018937c03e6d492ebb80d11";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, SORT_PARAM_VALUE)
                        .appendQueryParameter(API_KEYPARAM, API_KEYPARAM_VALUE)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.d(LOG_TAG, "movieJsonStr: " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        private List<String> getMovieDataFromJson(String movieJsonStr) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MOV_LIST = "results";
            final String MOV_ADULT = "adult";
            final String MOV_BACKDROP_PATH = "backdrop_path";
            final String MOV_GENRE_IDS_LIST = "genre_ids";
            final String MOV_ID = "id";
            final String MOV_ORIGINAL_LANGUAGE = "original_language";
            final String MOV_ORIGINAL_TITLE = "origial_title";
            final String MOV_OVERVIEW = "overview";
            final String MOV_RELEASE_DATE = "release_date";
            final String MOV_POSTER_PATH = "poster_path";
            final String MOV_POPULARITY = "popularity";
            final String MOV_TITLE = "title";
            final String MOV_VIDEO = "video";
            final String MOV_VOTE_AVERAGE = "vote_average";
            final String MOV_VOTE_COUNT = "vote_count";

            List<String> resultStrs = new ArrayList<String>();
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOV_LIST);

            for(int i = 0; i < movieArray.length(); i++) {

                // Get the JSON object representing the movie
                JSONObject movieObject = movieArray.getJSONObject(i);

                // description is in a child array called "weather", which is 1 element long.
                String id = movieObject.getString(MOV_ID);
                String overview = movieObject.getString(MOV_OVERVIEW);
                String title = movieObject.getString(MOV_TITLE);
                String popularity = movieObject.getString(MOV_POPULARITY);
                String posterPath = movieObject.getString(MOV_POSTER_PATH);
                String background_path = movieObject.getString(MOV_BACKDROP_PATH);
                String vote_average = movieObject.getString(MOV_VOTE_AVERAGE);
                String releaseDate = movieObject.getString(MOV_RELEASE_DATE);
                resultStrs.add( id + "#" + posterPath + "#" + title + "#" + overview + "#" + vote_average + "#" + releaseDate);
             //   resultStrs.add(posterPath);

            }
            return resultStrs;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (result != null) {
               imageAdapter.replace(result);
            }
        }
    }

/* // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    *//**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }*/

}

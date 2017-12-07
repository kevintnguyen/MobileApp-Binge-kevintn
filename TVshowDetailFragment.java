package kevintn.uw.tacoma.edu.webserviceslab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import kevintn.uw.tacoma.edu.webserviceslab.tvshowoptions.LikeTVshow;
import kevintn.uw.tacoma.edu.webserviceslab.tvshow.TVshow;



/**
 * @author Kevin Nguyen && Cynthia Tran
 *
 * TheTVshowDetailFragment class creates a fragment
 * to show the detail of the TVShow when a user presses
 * the TVShow.
 *
 **/
public class TVshowDetailFragment extends Fragment {

    private TextView mTitle;
    private TextView mContent;
    private TextView mRating;

//    protected static DislikeFragment mDFrag;
    protected static LikeFragment mLFrag;

    public final static String TVSHOW_ITEM_SELECTED = "tvshow_selected";
    private final static String DELETE_SHOWS_URL = "http://cssgate.insttech.washington.edu/~cyntran/DeleteShows.php?";

    /**
     * Constructor
     */
    public TVshowDetailFragment() {

    }

    /**
     * Creates a View object and instantiates TextViews
     *
     * @param inflater - Inflater for layout.
     * @param container - ViewGroup object.
     * @param savedInstanceState - Bundle for last saved state.
     * @return view - View object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_course_detail, container, false);
        mTitle = (TextView) view.findViewById(R.id.show_item_title);
        mContent = (TextView) view.findViewById(R.id.show_content_desc);
        mRating = (TextView) view.findViewById(R.id.show_rating);
        Button likeButton = (Button) view.findViewById(R.id.like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikeTVshow.addnewItem(mTitle.getText().toString());
                mLFrag = new LikeFragment();
            }
        });

        Button disLikeButton = (Button) view.findViewById(R.id.dislike_button);
        disLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikeTVshow.removeItem(mTitle.getText().toString());
                String url = buildDislikesListURL(mTitle.getText().toString());
                DeleteShowsTask deleteShowsTask = new DeleteShowsTask();
                deleteShowsTask.execute(url);
            }
        });
        return view;
    }

    /**
     * Updates the view with a new TV show.
     *
     * @param TVshow - A TVShow that gets added when we do an update
     */
    public void updateView(TVshow TVshow) {
        if (TVshow != null) {
            mTitle.setText(TVshow.getMtitle());
            mRating.setText(TVshow.getMrating());
            mContent.setText(TVshow.getMshortDescription());

        }
    }

    /**
     * onStart is called when the program starts. Updates
     * the view if there is a TV Show that was selected.
     */
    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateView((TVshow) args.getSerializable(TVSHOW_ITEM_SELECTED));
        }
    }

        private String buildDislikesListURL(String title) {
        StringBuilder sb = new StringBuilder(DELETE_SHOWS_URL);
        try {
            SharedPreferences usrEmail = getContext().
                    getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            String email = usrEmail.getString("email", "");
            sb.append("email=");
            sb.append(URLEncoder.encode(email, "UTF-8"));

            sb.append("&title=");
            sb.append(URLEncoder.encode(title, "UTF-8"));

        }
        catch(Exception e) {
            Toast.makeText(getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    /**
     * This AsyncTask checks to see if the registration has been successful.
     */
    private class DeleteShowsTask extends AsyncTask<String, Void, String> {

        /**
         * Calls superclass' PreExecute function
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        /**
         * Open connection, runs in background.
         *
         * @param urls - A URL
         * @return response - Should return nothing unless something went wrong.
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to add show, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result - data retrieved
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getContext(), "Show removed!"
                            , Toast.LENGTH_LONG)
                            .show();

                    Intent i = new Intent(getActivity(), TVshowActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }
            } catch (JSONException e) {
                Toast.makeText(getContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}

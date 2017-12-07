package kevintn.uw.tacoma.edu.webserviceslab;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import kevintn.uw.tacoma.edu.webserviceslab.tvshow.TVshow;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Nguyen && Cynthia Tran
 * TVShowFragment is a class which is a fragment as a
 * representation of TV Shows. This class creates
 * the fragments and views used in the RecyclerView.
 */
public class TVshowFragment extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private static final String TV_SHOWS_LIST = "http://cssgate.insttech.washington.edu/~cyntran/list.php?";
    private RecyclerView mRecyclerView;
    public static List<TVshow> allTVShows;

    /**
     * Constructor
     */
    public TVshowFragment() {
        allTVShows = new ArrayList<>();
    }

    /**
     * OnCreate saves the state of the program
     *
     * @param savedInstanceState - State of program
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get column count
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    /**
     * Create the view for TVshowFragment which is a a RecyclerView
     * Checks to see how many columns there are to know what layout to use.
     *
     * @param inflater - Inflater for layout
     * @param container - Container
     * @param savedInstanceState - Saved state of program
     * @return view - A view object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            DownloadTvShowsTask task = new DownloadTvShowsTask();
            task.execute(new String[]{TV_SHOWS_LIST});
        }

        return view;
    }


    /**
     * Attach activity (listener) to this fragment
     *
     * @param context - Context of what's being attached
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    /**
     * Detach activity (listener) to fragment.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *  Class that is an AsyncTask and grabs data from database,
     *  creates the TV Show objects and creates a list of TV Show.
     *
     * */
    private class DownloadTvShowsTask extends AsyncTask<String, Void, String> {


        /**
         * Runs in the background while program is running
         *
         * @param urls - A URL
         * @return response - A string that shouldn't return anything unless
         * something is wrong.
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
                    response = "Unable to download the list, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * After doInBackground is done, grabbed data will create
         * a list of TVShow object. Then set adapter to the list
         * in creating a RecyclerView.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            List<TVshow> TVshowList = new ArrayList<TVshow>();
            result = TVshow.parseShowsJSON(result, TVshowList);
            allTVShows.addAll(TVshowList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            // Everything is good, show the list of courses.
            if (!TVshowList.isEmpty()) {
                mRecyclerView.setAdapter(new MytvshowRecyclerViewAdapter(TVshowList, mListener));
            }
        }

    }

    /**
     * Interface for ListFragment Listener
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(TVshow item);
    }

}

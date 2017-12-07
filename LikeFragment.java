package kevintn.uw.tacoma.edu.webserviceslab;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kevintn.uw.tacoma.edu.webserviceslab.localstorage.FeedReaderContract;
import kevintn.uw.tacoma.edu.webserviceslab.localstorage.FeedReaderDbHelper;
import kevintn.uw.tacoma.edu.webserviceslab.tvshow.TVshow;
import kevintn.uw.tacoma.edu.webserviceslab.tvshowoptions.LikeTVshow;


public class LikeFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private static final String ADD_SHOWS_URL
            = "http://cssgate.insttech.washington.edu/~cyntran/addShows.php?";
    private RecyclerView mRecyclerView;
    public static boolean isDownloadingLikes;

    public LikeFragment() {
        isDownloadingLikes = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list2, container, false);

        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());

        SharedPreferences usrEmail = getContext().
                getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        String email = usrEmail.getString("email", "");

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                List<String> recentLikes = LikeTVshow.ITEMS;
                List<String> pastLikes = TVshowActivity.likeTvlist;

                for (int i = 0; i < recentLikes.size(); i++) {
                    if (!pastLikes.contains(recentLikes.get(i))) {
                        String url = addShowsListURL(view, recentLikes.get(i));
                        SaveLikesTask saveLikes = new SaveLikesTask();
                        saveLikes.execute(new String[]{url.toString()});
                        pastLikes.add(recentLikes.get(i));
                    } else {
                        Log.d("Already added", recentLikes.get(i));
                    }
                }

                //To ensure there are no duplicates!
                Set<String> st = new HashSet<>();
                st.addAll(pastLikes);
                pastLikes.clear();
                pastLikes.addAll(st);

                SQLiteDatabase db = mDbHelper.getWritableDatabase();

//                db.execSQL("delete from " + FeedReaderContract.FeedEntry.TABLE_NAME +
//                        " where ROWID NOT IN " +
//                        "(select MIN(ROWID) from " + FeedReaderContract.FeedEntry.TABLE_NAME +
//                        " group by " + FeedReaderContract.FeedEntry.COLUMN_NAME_EMAIL + ")");
                db.execSQL("delete from " + FeedReaderContract.FeedEntry.TABLE_NAME);

                for(int i = 0; i < pastLikes.size(); i++) {
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_EMAIL, email);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, pastLikes.get(i));
                    db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
                    Log.d("Values", values.toString());
                }

                Toast.makeText(getContext(), "Syncing data to local storage", Toast.LENGTH_SHORT).show();

                TVshowActivity.likeTvlist = pastLikes;
//                TVshowActivity.likeTvlist.clear();
//                TVshowActivity.likeTvlist.addAll(pastLikes);

                mRecyclerView.setAdapter(new MyLikesRecyclerViewAdapter(pastLikes, mListener));

            } else {
                Toast.makeText(getContext(), "No network connection available. ",
                        Toast.LENGTH_SHORT) .show();

                SQLiteDatabase db = mDbHelper.getReadableDatabase();

                String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_EMAIL,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE
                };

                String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_EMAIL + " = ?";
                Log.d("selection", selection);
                String[] selectionArgs = { email };

                Cursor cursor = db.query(
                        FeedReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                      // The sort order
                );

                List localTVShows = new ArrayList<>();
                Log.d("cursor", "cursor:" + cursor.getCount());
                while(cursor.moveToNext()) {
                    String show = cursor.getString(
                            cursor.getColumnIndex(
                                    FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE
                            ));
                    localTVShows.add(show);
                    Log.d("cursor", show);
                }
                cursor.close();
                mRecyclerView.setAdapter(new MyLikesRecyclerViewAdapter(localTVShows, mListener));
            }
        }
        return view;
    }


    private String addShowsListURL(View v, String title) {
        StringBuilder sb = new StringBuilder(ADD_SHOWS_URL);
        try {
            SharedPreferences usrEmail = getContext().
                    getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            String email = usrEmail.getString("email", "");

            sb.append("title=");
            sb.append(URLEncoder.encode(title, "UTF-8"));
            sb.append("&email=");
            sb.append(URLEncoder.encode(email, "UTF-8"));
        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(String item);
    }

    /**
     * This AsyncTask checks to see if the registration has been successful.
     */
    private class SaveLikesTask extends AsyncTask<String, Void, String> {

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
                    Toast.makeText(getContext(), "Database updated!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
//                    Toast.makeText(getContext(), "Database not updated: "
//                            , Toast.LENGTH_LONG)
//                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


}

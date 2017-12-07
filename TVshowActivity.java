package kevintn.uw.tacoma.edu.webserviceslab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import kevintn.uw.tacoma.edu.webserviceslab.authenticate.SignInActivity;
import kevintn.uw.tacoma.edu.webserviceslab.tvshow.TVshow;
import kevintn.uw.tacoma.edu.webserviceslab.tvshowoptions.LikeTVshow;
import kevintn.uw.tacoma.edu.webserviceslab.TVshowFragment;


/*
 * @author Kevin Nguyen && Cynthia Tran
 *
 * TheTVshowActivity class implements the activity
 * for the TVShow fragments. When a user presses a TV Show fragment,
 * then the user will then see detail about the fragment.
 *
 */
public class TVshowActivity extends AppCompatActivity implements
        TVshowFragment.OnListFragmentInteractionListener,
        LikeFragment.OnListFragmentInteractionListener {

    public static List<String> dislikeTvList;
    public static List<String> likeTvlist;
    public static DownloadLikedShowsTask task;
    private static final String LIKES_SHOWS_LIST
            = "http://cssgate.insttech.washington.edu/~cyntran/showsList.php?";
    private static final String DISLIKE_SHOWS_LIST
            = "http://cssgate.insttech.washington.edu/~cyntran/dislikeList.php?";

    public TVshowActivity() {
        dislikeTvList = new ArrayList<>();
        likeTvlist = new ArrayList<>();
    }

    /**
     * OnCreate checks to see if the saved state was null,
     * then create new TVShow fragment.
     *
     * @param savedInstanceState - A bundle, saved state of the program
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.list) == null) {
            TVshowFragment TVshowFragment = new TVshowFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, TVshowFragment)
                    .commit();
        }

        task = new DownloadLikedShowsTask();
        String url = buildShowsListURL();
        task.execute(new String[]{url.toString()});
    }

    /**
     * Creates the Menu with option to log-out.
     *
     * @param menu - A menu object
     * @return true - Returns bool value true
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        inflater.inflate(R.menu.menu_course_list, menu);
        return true;
    }

    /**
     * Checks if user has logged out. If so, change the
     * shared preferences so that the user does not stay
     * signed in. Then restart SignInActivity.
     *
     * @param item - MenuItem chosen
     * @return boolean - Returns bool value true if user chooses to logout and false otherwise.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences sharedPreferences =
                    getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false)
                    .commit();
            Intent i = new Intent(this, SignInActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        switch (item.getItemId()) {
            case R.id.Shows:
//                Intent i = new Intent(this, TVshowActivity.class);
//                startActivity(i);
//                finish();
                TVshowFragment tvfrag = new TVshowFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, tvfrag)
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.Liked:
                if (LikeTVshow.ITEMS.isEmpty()) {
                    LikeFragment likefrag = new LikeFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, likefrag)
                            .addToBackStack(null)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, TVshowDetailFragment.mLFrag)
                            .addToBackStack(null)
                            .commit();
                }
                return true;
            case R.id.EmailList:
                EmailFragment em = new EmailFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, em)
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.Search:
                Intent search = new Intent(this, SearchActivity.class);
                startActivity(search);
                finish();
                return true;
            default:
                return false;
        }
    }

    /**
     * Create a new TVShowDetailFragment that informs user
     * about the show they pressed. Make sure user can
     * go back to the list by adding null to backstack.
     *
     * @param TVshow - A TVShow object
     */
    @Override
    public void onListFragmentInteraction(kevintn.uw.tacoma.edu.webserviceslab.tvshow.TVshow TVshow) {
        TVshowDetailFragment TVshowDetailFragment = new TVshowDetailFragment();
        if (TVshow.getMtitle().equals("TV Show")) {
            return;
        }
        Bundle args = new Bundle();
        args.putSerializable(TVshowDetailFragment.TVSHOW_ITEM_SELECTED, TVshow);
        TVshowDetailFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, TVshowDetailFragment)
                .addToBackStack(null)
                .commit();
    }


    private String buildShowsListURL() {
        StringBuilder sb = new StringBuilder(LIKES_SHOWS_LIST);
        try {
            SharedPreferences usrEmail = getApplication().
                    getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            String email = usrEmail.getString("email", "");
            sb.append("email=");
            sb.append(URLEncoder.encode(email, "UTF-8"));
        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    /**
     * LikeFragment's OnListFragment listener
     * @param item
     */
    @Override
    public void onListFragmentInteraction(String item) {
    }

    private class DownloadLikedShowsTask extends AsyncTask<String, Void, String> {
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
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
                            .show();
                return;
            }
            result = LikeTVshow.parseShowsJSON(result, likeTvlist);
            if (result != null && result.startsWith("Unable to parse data, Reason")) {
                Toast.makeText(getApplicationContext(), "Nothing's been added to your Liked list yet! Add now!", Toast.LENGTH_LONG)
                        .show();
                Log.d("Toast:", result);
                return;
            }
            if (!likeTvlist.isEmpty()) {
            }
        }

    }

}


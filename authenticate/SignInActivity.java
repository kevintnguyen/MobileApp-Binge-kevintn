package kevintn.uw.tacoma.edu.webserviceslab.authenticate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import kevintn.uw.tacoma.edu.webserviceslab.R;
import kevintn.uw.tacoma.edu.webserviceslab.TVshowActivity;


/**
 * @author Kevin Nguyen && Cynthia Tran
 *
 * The SignInActivity class is the main activity for logging in
 * a user and for signing up a user.
 */
public class SignInActivity extends AppCompatActivity implements
        LoginFragment.LogInListener, RegisterFragment.RegisterListener {
    private SharedPreferences mSharedPreferences;

    /**
     * Creates shared preferences, sets the layout for signing in, and
     * starts the TVShowActivity.
     *
     * @param savedInstanceState - Saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        if (!mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new LoginFragment())
                    .commit();
        } else {
            Intent i = new Intent(this, TVshowActivity.class);
            startActivity(i);
            finish();
        }
    }


    /**
     * Executes given URL with a task
     * @param url - A URL
     */
    @Override
    public void logIn(String url, boolean login) {
        if(login == true) {
            LogInTask task = new LogInTask();
            task.execute(new String[]{url.toString()});
        } else {
            RegisterTask task = new RegisterTask();
            task.execute(new String[]{url.toString()});
        }

    // Takes you back to the previous fragment by popping the current fragment out.
        getSupportFragmentManager().popBackStackImmediate();
    }


    /**
     * AsyncTask that will run in the background and
     * create a JSONObject when a user is registered
     * to be added to the database.
     *
     */
    private class LogInTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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
                    response = "Unable to add course, Reason: "
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
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                  callact();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_SHORT)
                            .show();
                    Log.d("error ms", jsonObject.get("error").toString());
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Sets the shared preferences object so the
     * user stays logged in. Then starts the activity
     * to show the TV shows.
     */
    private void callact() {
        mSharedPreferences
                .edit()
                .putBoolean(getString(R.string.LOGGEDIN), true)
                .commit();
        Intent i = new Intent(this, TVshowActivity.class);
        startActivity(i);
        finish();
    }


    /**
     * Create Users with task
     * @param url
     */
    @Override
    public void register(String url) {
        RegisterTask task = new RegisterTask();
        task.execute(new String[]{url.toString()});

        // Takes you back to the previous fragment by popping the current fragment out.
        getSupportFragmentManager().popBackStackImmediate();
    }


    /**
     * This AsyncTask checks to see if the registration has been successful.
     */
    private class RegisterTask extends AsyncTask<String, Void, String> {

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
                    response = "Unable to add course, Reason: "
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
                    Toast.makeText(getApplicationContext(), "Account successfully registered!"
                            , Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to Register: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_SHORT)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package kevintn.uw.tacoma.edu.webserviceslab.authenticate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;
import java.util.regex.Pattern;

import kevintn.uw.tacoma.edu.webserviceslab.R;

/**
 * @author Kevin Nguyen && Cynthia Tran
 *
 * LoginFragment is a fragment class which creates
 * the login functionality. This class uses the php file
 * to check if user has been registered into the database.
 */

public class LoginFragment extends Fragment {

    private Button mRegbutton;

    private LogInListener mListener;

    private final static String LOGIN_URL
            = "http://cssgate.insttech.washington.edu/~kevintn/login.php?";
    private final static String ADD_USER_URL
            = "http://cssgate.insttech.washington.edu/~kevintn/adduser.php?";
    private EditText mEmailEditText;

    private EditText mPasswordEditText;


    /**
     * Constructor
     */
    public LoginFragment() {}


    /**
     * Interface used for mListener
     */
    public interface LogInListener {
        public void logIn(String url, boolean answer);
    }

    /**
     * Generates when Log-in fragment is created.
     *
     * @param savedInstanceState - Saved state of program
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);}



    /**
     * OnCreateView creates the log-in view EditText Log-in
     * objects such as the email EditText object and the
     * password EditText object.
     *
     * @param inflater - Creates layout
     * @param container - ViewGroup object
     * @param savedInstanceState - The current state of program
     * @return - a View object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mRegbutton = (Button) v.findViewById(R.id.reg_button);
        mRegbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url = buildAddUserURL(v);
                boolean answer;
                answer=false;
                mListener.logIn(url,answer);
            }

        });

        mEmailEditText = (EditText) v.findViewById(R.id.userid_edit);
        mPasswordEditText = (EditText) v.findViewById(R.id.pwd_edit);
        Button loginButton = (Button) v.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity()
                        .getSharedPreferences(getString(R.string.LOGIN_PREFS),
                                Context.MODE_PRIVATE);
                        sharedPreferences.edit()
                        .putString("email", mEmailEditText.getText().toString())
                        .commit();
//                Toast.makeText(getContext(), "You are logged in as " + mEmailEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                String url = buildLogInURL(v);
                boolean answer;
                answer=true;
                mListener.logIn(url,answer);
            }
        });
        return v;
    }


    /**
     * Generates URL with from EditText views for email
     * and password
     *
     * @param v - A View object
     * @return sb - StringBuilder
     */
    private String buildLogInURL(View v) {

        StringBuilder sb = new StringBuilder(LOGIN_URL);
        try {
            String email = mEmailEditText.getText().toString();
            sb.append("email=");
            sb.append(email);

            String password = mPasswordEditText.getText().toString();
            sb.append("&password=");
            sb.append(URLEncoder.encode(password, "UTF-8"));

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    /**
     * Adds user to database
     * @param v a view object
     * @return URL for Query
     */
    private String buildAddUserURL(View v) {

        StringBuilder sb = new StringBuilder(ADD_USER_URL);

        try {
            String email = mEmailEditText.getText().toString();
            sb.append("email=");
            sb.append(email);

            String password = mPasswordEditText.getText().toString();
            sb.append("&password=");
            sb.append(URLEncoder.encode(password, "UTF-8"));

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    /**
     * Assign activity to fragment.
     *
     * @param context - Current environment.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterFragment.RegisterListener) {
            mListener = (LogInListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginAddListener");
        }
    }


}

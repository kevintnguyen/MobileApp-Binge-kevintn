package kevintn.uw.tacoma.edu.webserviceslab.authenticate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;
import java.util.regex.Pattern;

import kevintn.uw.tacoma.edu.webserviceslab.R;


/**
 * @author Kevin Nguyen && Cynthia Tran
 *
 * The RegisterFragment class extends Fragment and
 * creates the view (the TextView and EditText)
 * and functionality for Registering a new user.
 *
 */
public class RegisterFragment extends Fragment {

    /**
     * Constructor
     */
    public RegisterFragment() {}
    private RegisterListener mListener;
    private final static String USER_ADD_URL
            = "http://cssgate.insttech.washington.edu/~kevintn/adduser.php?";
    private TextView mEmail;
    private EditText mPassword;

    /**
     * Interface for listener for user registration.
     */
    public interface RegisterListener {
        public void register(String url);
    }

    /**
     * Generates URL with from views for email
     * and password. Catches exception.
     *
     * @param v - A view object
     * @return sb.toString() - A String
     */
    private String buildRegisterUserURL(View v) {
        StringBuilder sb = new StringBuilder(USER_ADD_URL);
        try {
            String email = mEmail.getText().toString();
            sb.append("email=");
            sb.append(email);

            String password = mPassword.getText().toString();
            sb.append("&password=");
            sb.append(URLEncoder.encode(password, "UTF-8"));

            Log.i("UserAddFragment", sb.toString());
        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    /**
     * onCreate calls it's parent class' onCreate method.
     *
     * @param savedInstanceState - Saved state of program
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Creates the view and layout for the Register Fragment.
     *
     * @param inflater - Inflater for layout
     * @param container - Container object
     * @param savedInstanceState - Bundle object saved state
     * @return v - A view object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        mEmail = (TextView) v.findViewById(R.id.edit_course_long_desc);
        mPassword = (EditText) v.findViewById(R.id.edit_course_prereqs);
        Button editCourseButton = (Button) v.findViewById(R.id.reg_button);
        editCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidEmail(mEmail.getText().toString())) {
                    Toast.makeText(getContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                }
                if (!isValidPassword(mPassword.getText().toString())){
                    Toast.makeText(getContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                }
                String url = buildRegisterUserURL(v);
                mListener.register(url);
            }
        });
        return v;
    }

    /*
     * NEXT FEW METHODS are to test the validity of username and passwords
     */
    /**
     * Email validation pattern.
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    /**
     * Validates if the given input is a valid email address.
     *
     * @param email        The email to validate.
     * @return {@code true} if the input is a valid email. {@code false} otherwise.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }


    private final static int PASSWORD_LEN = 6;


    /**
     * Validates if the given password is valid.
     * Valid password must be at last 6 characters long
     * with at least one digit and one symbol.
     *
     * @param password        The password to validate.
     * @return {@code true} if the input is a valid password.
     * {@code false} otherwise.
     */
    public static boolean isValidPassword(String password) {
        boolean foundDigit = false, foundSymbol = false;
        if  (password == null ||
                password.length() < PASSWORD_LEN)
            return false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i)))
                foundDigit = true;
            if (!Character.isLetterOrDigit(password.charAt(i)))
                foundSymbol = true;
        }
        return foundDigit && foundSymbol;
    }


    /**
     * OnAttach calls it's super class.
     * Attaches activity to the fragment.
     *
     * @param context - Context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterListener) {
            mListener = (RegisterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RegisterAddListener");
        }
    }

}

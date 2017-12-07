package kevintn.uw.tacoma.edu.webserviceslab;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import kevintn.uw.tacoma.edu.webserviceslab.tvshowoptions.LikeTVshow;

/**
 * Cynthia Tran
 * This fragment allows the user to send an email
 * containing their Liked list. The fragment assumes the
 * user has configured their email client on their phones
 * because it sends the email through the client..
 */
public class EmailFragment extends Fragment {

    /*
     * constructor
     */
    public EmailFragment() {
    }

    /**
     * Creates the view with an editText for email and a button.
     *
     * @param inflater - to inflate the fragment layout
     * @param container - a container
     * @param savedInstanceState - saved state
     * @return view - returns the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email, container, false);
        final EditText et = (EditText) view.findViewById(R.id.send_email_text);
        Button sendButton = (Button) view.findViewById(R.id.send_email_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences usrEmail = getContext().
                        getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                String subject = usrEmail.getString("email", "");
                subject = subject + " Sends You Their Favorites Shows List From The Binge App!";
                sendEmail(et.getText().toString(), subject, TVshowActivity.likeTvlist);
            }
        });
        return view;
    }


    /**
     * Sends the email by using an email client on the phone.
     *
     * @param recipient - A recipient
     * @param subject - The email subject
     * @param content - Contents of the email
     */
    protected void sendEmail(String recipient, String subject, List<String> content) {
        Log.i("Send email", "");

        String[] TO = {recipient};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        StringBuilder sb = new StringBuilder();
        sb.append("Current favorites:\n");

        Log.d("Subject", "subject" + subject);

        if (content != null) {
            for (int i = 0; i < content.size(); i++) {
                sb.append(content.get(i));
                sb.append("\n");
            }
            Log.d("content", sb.toString());
            emailIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        } else {
            Toast.makeText(getContext(), "No List to show", Toast.LENGTH_LONG).show();
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
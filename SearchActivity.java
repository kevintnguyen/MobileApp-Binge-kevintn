package kevintn.uw.tacoma.edu.webserviceslab;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kevintn.uw.tacoma.edu.webserviceslab.authenticate.SignInActivity;
import kevintn.uw.tacoma.edu.webserviceslab.tvshow.TVshow;
import kevintn.uw.tacoma.edu.webserviceslab.R.id.*;
import kevintn.uw.tacoma.edu.webserviceslab.tvshowoptions.LikeTVshow;

import static kevintn.uw.tacoma.edu.webserviceslab.TVshowFragment.allTVShows;


/**
 * Cynthia Tran
 *
 * This class creates the search functionality in the app.
 */
public class SearchActivity extends AppCompatActivity implements
        TVshowFragment.OnListFragmentInteractionListener{


    /**
     * This creates the UI and functionality of the search.
     * When user presses search button, a TV detail fragment pops up..
     * allowing the user to read the fragment and check the description.
     *
     * @param savedInstanceState - Saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final EditText query = (EditText) findViewById(R.id.search_text);
        final Button search = (Button) findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String show = query.getText().toString();
                Log.d("mTVshows", allTVShows.toString());
                if (allTVShows.isEmpty()) {
                    Log.d("TVSHOWS LIST", "EMPTY");
                } else {
                     for (int i = 0; i < allTVShows.size(); i++) {
                         if (show.equalsIgnoreCase(allTVShows.get(i).getMtitle())) {
                                Log.d("SHOW", show);
                             TVshowDetailFragment TVshowDetailFragment = new TVshowDetailFragment();
                             Bundle args = new Bundle();
                             args.putSerializable(TVshowDetailFragment.TVSHOW_ITEM_SELECTED, allTVShows.get(i));
                             TVshowDetailFragment.setArguments(args);
                             getSupportFragmentManager().beginTransaction()
                                     .replace(R.id.search_activity_id, TVshowDetailFragment)
                                     .addToBackStack(null)
                                     .commit();
                         }
                     }
                }

            }
        });

        final Button goBack = (Button) findViewById(R.id.go_to_activity);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showShows = new Intent(getApplicationContext(), TVshowActivity.class);
                startActivity(showShows);
                finish();
            }
        });
    }

    /**
     * Overidded method for listener
     * @param item - A show
     */
    @Override
    public void onListFragmentInteraction(TVshow item) {
    }

}
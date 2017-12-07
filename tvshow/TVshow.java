package kevintn.uw.tacoma.edu.webserviceslab.tvshow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 4/28/2017.
 * This class is a model class for TV shows.
 */

public class TVshow implements Serializable {

    public String mtitle, mrating, mshortDescription;

    public TVshow(String title, String rating, String shortDescription) {
        mtitle = title;
        mrating = rating;
        mshortDescription = shortDescription;

    }
    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param showsJSON
     * @return reason or null if successful.
     */
    public static String parseShowsJSON(String showsJSON, List<TVshow> TVshowList) {
        String reason = null;
        if (showsJSON != null) {
            try {
                JSONArray arr = new JSONArray(showsJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    TVshow tvshow = new TVshow(obj.getString("title"), obj.getString("rating")
                            , obj.getString("description"));
                    TVshowList.add(tvshow);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }

    // Getters & Setters below

    public String getMtitle() {
        return mtitle;
    }

    @Override
    public String toString() {
        return mtitle;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public String getMshortDescription() {
        return mshortDescription;
    }

    public void setMshortDescription(String mshortDescription) {
        this.mshortDescription = mshortDescription;
    }

    public String getMrating() {
        return mrating;
    }

    public void setMrating(String mrating) {
        this.mrating = mrating;
    }
}

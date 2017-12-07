package kevintn.uw.tacoma.edu.webserviceslab.tvshowoptions;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Cynthia Tran & Kevin Nguyen
 * This class stores a Map of liked items to be shown
 * in the LikeFragment fragment.
 */
public class LikeTVshow {

    public static List<String> ITEMS = new ArrayList<>();

    /**
     * Adds a new item to the map if it does not exist
     *
     * @param name - Title of show
     */
    public static void addnewItem(String name) {
        if (!ITEMS.contains(name)) {
            ITEMS.add(name);
        }
    }

    public static void removeItem(String name) {
        if (ITEMS.contains(name)) {
            ITEMS.remove(name);
            Log.d("Removed", name);
        }
    }
    /**
     * TVShow object
     */
    public static class TVshow {
        public final String title;

        public TVshow(String name) {
            this.title = name;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public static String parseShowsJSON(String showsJSON, List<String> TVshowList) {
        String reason = null;
        if (showsJSON != null) {
            try {
                JSONArray arr = new JSONArray(showsJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    TVshow tvshow = new TVshow(obj.getString("title"));;
                    TVshowList.add(tvshow.title);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }
}

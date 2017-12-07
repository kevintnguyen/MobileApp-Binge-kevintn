package kevintn.uw.tacoma.edu.webserviceslab;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kevintn.uw.tacoma.edu.webserviceslab.TVshowFragment.OnListFragmentInteractionListener;
import kevintn.uw.tacoma.edu.webserviceslab.tvshow.TVshow;


import java.util.List;

/**
 * @author Kevin Nguyen && Cynthia Tran
 *
 * This class creates the adapter for the TV Show fragments when using
 * RecylerView. The RecyclerView recycles the fragments when the user
 * scrolls through the list of TV Show fragments to save resources.
 *
 */
public class MytvshowRecyclerViewAdapter extends RecyclerView.Adapter<MytvshowRecyclerViewAdapter.ViewHolder> {

    private final List<TVshow> mValues;
    private final OnListFragmentInteractionListener mListener;


    /**
     * Constructor takes in a list and a listener.
     *
     * @param items - Items in the list for RecyclerView
     * @param listener - Listener to be attached to the List fragment.
     */
    public MytvshowRecyclerViewAdapter(List<TVshow> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    /**
     * Create a way to update Views to help performance when recycling.
     *
     * @param parent - ViewGroup parent object to be passed into View
     * @param viewType - A value representing type of view
     * @return the ViewHolder object
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_course, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds viewholder to item in the list of shows and set a listener that
     * interacts with list fragment.
     *
     * @param holder - A ViewHolder
     * @param position - The index of the list of shows
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitle.setText(mValues.get(position).getMtitle());
        holder.mRating.setText(mValues.get(position).getMrating());


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    /**
     * Returns size of list in the Recycler view List of TV Shows.
     *
     * @return mValues.size() - size of list
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }


    /**
     * ViewHolder class which holds information for the TV Show fragment.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle;
        public final TextView mRating;
        public TVshow mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.id);
            mRating = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}

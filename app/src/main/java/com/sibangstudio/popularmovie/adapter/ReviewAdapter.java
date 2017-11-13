package com.sibangstudio.popularmovie.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sibangstudio.popularmovie.R;
import com.sibangstudio.popularmovie.data.ReviewData;

import java.util.List;

/**
 * Created by ibti on 11/13/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.NumberViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();


    private static int viewHolderCount;


    private Activity activity;
    private List<ReviewData> reviewList;

    ReviewData review;

    public ReviewAdapter(Activity activity,  ReviewAdapter.DirAdapterOnClickHandler mClickHandler) {

        this.activity = activity;
        this.mClickHandler = mClickHandler;
    }


    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    @Override
    public ReviewAdapter.NumberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReviewAdapter.NumberViewHolder viewHolder = new ReviewAdapter.NumberViewHolder(view);



        // COMPLETED (13) Use ColorUtils.getViewHolderBackgroundColorFromInstance and pass in a Context and the viewHolderCount
        // int backgroundColorForViewHolder = ColorUtils
        //    .getViewHolderBackgroundColorFromInstance(context, viewHolderCount);
        // COMPLETED (14) Set the background color of viewHolder.itemView with the color from above
        // viewHolder.itemView.setBackgroundColor(backgroundColorForViewHolder);

        // COMPLETED (15) Increment viewHolderCount and log its value
        viewHolderCount++;

        Log.d(TAG, "#####" + viewHolderCount);
        //Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
        //   + viewHolderCount);
        return viewHolder;
    }




    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param viewHolder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ReviewAdapter.NumberViewHolder viewHolder, int position) {
        Log.d(TAG, "#" + position);


        review = reviewList.get(position);

        // COMPLETED (12) Set the text of viewHolderIndex to "ViewHolder index: " + viewHolderCount
        // viewHolder.viewHolderIndex.setText("ViewHolder index: " + viewHolderCount);
        viewHolder.txtAuthor.setText(review.getAuthor());

        viewHolder.txtContent.setText(review.getContent());


    }


    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {

        if (null == reviewList) return 0;

        //Log.d(TAG, "Besar" + dirItems.size());
        return reviewList.size();
    }

    public void clearData() {
        int size = this.reviewList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                reviewList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void setDirData(List<ReviewData> data) {
        reviewList = data;
        notifyDataSetChanged();
    }




    /**
     * Cache of the children views for a list item.
     */
    class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtAuthor;
        TextView txtContent;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         *
         * @param itemView The View that you inflated in
         *                 {@link ReviewAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public NumberViewHolder(View itemView) {
            super(itemView);

            txtAuthor = (TextView) itemView.findViewById(R.id.txtAuthor);
            txtContent = (TextView) itemView.findViewById(R.id.txtContent);

            itemView.setOnClickListener(this);
        }


        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         *
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {

            //listItemNumberView.setText(String.valueOf(listIndex));
        }


        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            // String weatherForDay = dir.getTitle();
            mClickHandler.onClick(reviewList.get(adapterPosition));
        }
    }


    private final ReviewAdapter.DirAdapterOnClickHandler mClickHandler;


    /**
     * The interface that receives onClick messages.
     */
    public interface DirAdapterOnClickHandler {
        void onClick(ReviewData weatherForDay);
    }

    /**
     * Creates a ForecastAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public ReviewAdapter(ReviewAdapter.DirAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }
}


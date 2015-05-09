package factoriallabs.com.baconbeacon.menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import factoriallabs.com.baconbeacon.R;

/**
 * Created by YuChen on 2015-03-12.

public class MenuDataSetAdapter extends RecyclerView.Adapter<MenuDataSetAdapter.ViewHolder> implements Filterable{
    private final Context context;
    private ArrayList<Menu> mMenuDataset;
    private ArrayList<Menu> mMenuDatasetFiltered; //to enable filtering

    // search by name
    public void search(String text) {
        text = text.toLowerCase(Locale.getDefault());
        mMenuDatasetFiltered.clear();
        if (text.length() == 0) {
            mMenuDatasetFiltered.addAll(mMenuDataset);
        } else {
            StringBuilder temp = new StringBuilder();
            for (Menu Menu : mMenuDataset) {
                temp.append(Menu.first_name.toLowerCase());
                temp.append(" ");
                temp.append(Menu.last_name.toLowerCase());
                if(Menu.title != null){
                    temp.append(Menu.title.toLowerCase());
                }
                if (temp.toString()
                        .contains(text)) {
                    mMenuDatasetFiltered.add(Menu);
                }
                temp.delete(0,temp.length());
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    //A view holder to hold references to each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mCompany;
        private final ImageView mImageView;
        public TextView mName;
        public ViewHolder(View v) {
            super(v);
            mName = (TextView) v.findViewById(R.id.name);
            mCompany = (TextView) v.findViewById(R.id.company);
            mImageView = (ImageView) v.findViewById(R.id.profile_image);
        }
    }

    public MenuDataSetAdapter(Context c) {
        // Initializes a lazy image loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(c)
        .build();
        ImageLoader.getInstance().init(config);
        context = c;
    }

    public void setDataSet(ArrayList<Menu> mDataset){
        mMenuDataset = mDataset;
        mMenuDatasetFiltered = new ArrayList<>();
        mMenuDatasetFiltered.addAll(mMenuDataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MenuDataSetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // get element from your Menu dataset at this position
        if(mMenuDatasetFiltered != null){
            final Menu u = mMenuDatasetFiltered.get(position);
            holder.mName.setText(u.first_name +" "+ u.last_name);
            holder.mCompany.setText(u.title);
            holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_launcher));
            if(u.cached_picture == null)
                // Load image, decode it to Bitmap and return Bitmap to callback
                ImageLoader.getInstance().loadImage(u.photo_url, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        //scale the bitmap so we save memory
                        if(u.photo_url != null && !imageUri.equals(u.photo_url)) return;
                        if(loadedImage == null){
                            holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_launcher));
                        }else{
                            Bitmap scaledImage = Bitmap.createScaledBitmap(loadedImage, 50, 50, false);
                            holder.mImageView.setImageBitmap(scaledImage);
                            u.cached_picture = scaledImage;
                        }
                        //todo check urls
                    }
                });
            else
        holder.mImageView.setImageBitmap(u.cached_picture); //reuse cached picture

        }
    }


    // Return the size
    @Override
    public int getItemCount() {
        if(mMenuDatasetFiltered != null)
            return mMenuDatasetFiltered.size();
        else return 0;
    }
} */
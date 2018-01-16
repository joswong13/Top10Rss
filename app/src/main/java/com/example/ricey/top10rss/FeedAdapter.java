package com.example.ricey.top10rss;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by Ricey on 1/8/2018.
 */

public class FeedAdapter extends ArrayAdapter {

    private static final String TAG = "FeedAdapter";

    private final int layoutResource;

    private final LayoutInflater layoutInflater;

    private List<FeedEntry> applications;


    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FeedEntry currentApp = applications.get(position);

        Log.d(TAG, "getView: image url " + currentApp.getImageURL() + " app " + currentApp.getName());

        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());
        // loads the image from the URL in the xml
        Picasso.with(this.getContext()).load(currentApp.getImageURL()).into(viewHolder.tvImage);
        viewHolder.tvRD.setText(currentApp.getReleaseDate());


        return convertView;
    }

    private class ViewHolder
    {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;
        final ImageView tvImage;
        final TextView tvRD;

        ViewHolder(View v)
        {
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);
            this.tvImage = v.findViewById(R.id.parsedIcon);
            this.tvRD = v.findViewById(R.id.tvReleaseDate);
        }
    }

}







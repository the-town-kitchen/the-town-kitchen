package com.codepath.the_town_kitchen.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.models.Founder;
import com.squareup.picasso.Picasso;

import java.util.List;

// Provide the underlying view for an individual list item.
public class FounderAdapter extends RecyclerView.Adapter<FounderAdapter.VH> {
    private Context mContext;
    private List<Founder> mFounders;

    public FounderAdapter(Context context, List<Founder> founders) {
        mContext = context;
        if (founders == null) {
            throw new IllegalArgumentException("founders must not be null");
        }
        mFounders = founders;


    }
    // Inflate the view based on the viewType provided.
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_founder, parent, false);
        return new VH(itemView, mContext);
    }

    // Display data at the specified position
    @Override
    public void onBindViewHolder(final VH holder, int position) {

        Founder founder = mFounders.get(position);
        holder.rootView.setTag(founder);
        holder.tvName.setText(founder.getName());
        holder.tvRole.setText(founder.getRole());
        Picasso.with(mContext).load(founder.getImageUrl()).into(holder.ivProfile);
    }

    @Override
    public int getItemCount() {
        return mFounders.size();
    }

    // Provide a reference to the views for each contact item
    public final static class VH extends RecyclerView.ViewHolder {
        final View rootView;
        final ImageView ivProfile;
        final TextView tvName;
        final TextView tvRole;
        public VH(View itemView, final Context context) {
            super(itemView);
            rootView = itemView;
            ivProfile = (ImageView)itemView.findViewById(R.id.ivProfile);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            tvRole = (TextView)itemView.findViewById(R.id.tvRole);

        }
    }
}

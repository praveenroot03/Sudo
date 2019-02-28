package com.project.sudo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class OrgAdapter extends RecyclerView.Adapter<OrgAdapter.FeedViewHolder> {

    private static Context mContext;
    private List<Organisation> orgList;

    public OrgAdapter(Context mContext, List<Organisation> orgList) {
        this.mContext = mContext;
        this.orgList = orgList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.feed_card, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {

        final Organisation org = orgList.get(position);

        holder.name_tv.setText(org.getName());
        holder.tag_tv.setText(org.getTagline());

        Glide.with(mContext).load(org.getPhotourl()).into(holder.imageview);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OrganisationActivity.class);
                intent.putExtra("orgInfo", (Serializable) org);
                mContext.startActivity(intent);}
        });




    }

    @Override
    public int getItemCount() {
        return orgList.size();
    }


    public class FeedViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageview;
        private TextView name_tv,tag_tv;
        private CardView cardView;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);

            imageview = itemView.findViewById(R.id.card_image);
            name_tv = itemView.findViewById(R.id.card_name);
            tag_tv = itemView.findViewById(R.id.card_tag);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}

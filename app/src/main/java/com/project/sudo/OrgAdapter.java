package com.project.sudo;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class OrgAdapter extends RecyclerView.Adapter<OrgAdapter.FeedViewHolder> {

    private static Context mContext;
    private List<Organisation> orgList;

    //Firebase Instances
    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference userCollection = firestore.collection("users");
    private SparseBooleanArray itemStateArray = new SparseBooleanArray();


    public OrgAdapter(Context mContext, List<Organisation> orgList) {
        OrgAdapter.mContext = mContext;
        this.orgList = orgList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.feed_card, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedViewHolder holder, int position) {

        final Organisation org = orgList.get(position);

        holder.name_tv.setText(org.getName());
        holder.tag_tv.setText(org.getTagline());

        Glide.with(mContext).load(org.getPhotourl()).into(holder.imageview);

        // Already Bookmarked
        int adapterPosition = holder.getAdapterPosition();
        if (org.getBookmarked() == true) {
            holder.bookmarkbtn.setImageDrawable(mContext.getDrawable(R.drawable.bookmarked));
            itemStateArray.put(adapterPosition, true);
        } else {
            holder.bookmarkbtn.setImageDrawable(mContext.getDrawable(R.drawable.unbookmarked));
            itemStateArray.put(adapterPosition, false);
        }
        
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OrganisationActivity.class);
                intent.putExtra("orgInfo", org);
                mContext.startActivity(intent);}
        });

        if (org.getBookmarked() == true)
            holder.bookmarkbtn.setImageDrawable(mContext.getDrawable(R.drawable.bookmarked));
        else holder.bookmarkbtn.setImageDrawable(mContext.getDrawable(R.drawable.unbookmarked));


        // Checkbox States Retain
        if (!itemStateArray.get(position, false))
            holder.bookmarkbtn.setImageDrawable(mContext.getDrawable(R.drawable.unbookmarked));
        else holder.bookmarkbtn.setImageDrawable(mContext.getDrawable(R.drawable.bookmarked));


        holder.bookmarkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFirebaseUser != null) {
                    // Adds bookmark
                    if (holder.bookmarkbtn.getDrawable().getConstantState().equals(mContext.getDrawable(R.drawable.unbookmarked).getConstantState())) {
                        holder.bookmarkbtn.setImageDrawable(mContext.getDrawable(R.drawable.bookmarked));

                        int adapterPosition = holder.getAdapterPosition();

                        // Change bookmark state
                        if (!itemStateArray.get(adapterPosition, false)) {
                            holder.bookmarkbtn.setImageDrawable(mContext.getDrawable(R.drawable.bookmarked));
                            itemStateArray.put(adapterPosition, true);
                        } else {
                            holder.bookmarkbtn.setImageDrawable(mContext.getDrawable(R.drawable.unbookmarked));
                            itemStateArray.put(adapterPosition, false);
                        }

                        DocumentReference addbookmark = userCollection.document(mFirebaseUser.getUid());
                        addbookmark.update("bookmarkIds", FieldValue.arrayUnion(org.getTagline())).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                org.setBookmarked(true);
                                Toast.makeText(mContext, "BookMarked!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    // Deletes bookmark
                    else {
                        holder.bookmarkbtn.setImageDrawable(mContext.getDrawable(R.drawable.unbookmarked));

                        DocumentReference userDocument = userCollection.document(mFirebaseUser.getUid());
                        userDocument.update("bookmarkIds", FieldValue.arrayRemove(org.getTagline())).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                org.setBookmarked(false);
                                Toast.makeText(mContext, "UnBookMarked!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                } else {
                    Toast.makeText(mContext, "Signin To Bookmark!!", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return orgList.size();
    }


    public class FeedViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageview, bookmarkbtn;
        private TextView name_tv,tag_tv;
        private CardView cardView;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);

            bookmarkbtn = itemView.findViewById(R.id.bookmark_iv);
            imageview = itemView.findViewById(R.id.card_image);
            name_tv = itemView.findViewById(R.id.card_name);
            tag_tv = itemView.findViewById(R.id.card_tag);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}

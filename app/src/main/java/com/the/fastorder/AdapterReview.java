package com.the.fastorder;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview>{

    private Context context;
    public ArrayList<ModelRevew> reviewArrayList;

    public AdapterReview(Context context, ArrayList<ModelRevew> reviewArrayList){
        this.context = context;
        this.reviewArrayList = reviewArrayList;

    }

    @androidx.annotation.NonNull
    @Override
    public HolderReview onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_review,parent,false);
        return new HolderReview(view);


    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull HolderReview holder, int position) {
        ModelRevew modelReview = reviewArrayList.get(position);
        String ratings = modelReview.getRatings();
        String review = modelReview.getReview();
        String timestamp = modelReview.getTimestamp();

        loadUserDetail(modelReview, holder);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.ratingBar.setRating(Float.parseFloat(ratings));
        holder.reviewTv.setText(review);
        holder.dateTv.setText(dateFormat);
    }

    private void loadUserDetail(ModelRevew modelReview, HolderReview holder) {
        String uid = modelReview.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        String name = ""+ snapshot.child("name").getValue();
                        String profileImage = ""+ snapshot.child("profileImage").getValue();

                        holder.nameTv.setText(name);
                        try {

                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person).into(holder.profileIv);

                        }catch (Exception e){

                            holder.profileIv.setImageResource(R.drawable.ic_person);
                        }
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    class HolderReview extends RecyclerView.ViewHolder{

        private TextView nameTv, dateTv, reviewTv;
        private ImageView profileIv;
        private RatingBar ratingBar;

        public HolderReview(@NonNull View itemView){
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
            profileIv = itemView.findViewById(R.id.profileIv);
            ratingBar = itemView.findViewById(R.id.ratingBar);


        }
    }
}


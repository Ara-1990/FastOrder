package com.the.fastorder;

import android.content.Context;
import android.content.Intent;
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

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop> {

    private Context context;
    public ArrayList<ModelShop> shopsList;

    public AdapterShop(Context context, ArrayList<ModelShop> shopsList) {
        this.context = context;
        this.shopsList = shopsList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_shop, parent, false);
        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {
      final   ModelShop modelShop = shopsList.get(position);


        String phone = modelShop.getPhone();
        String uid = modelShop.getUid();


        String profileImage = modelShop.getProfileImage();
        String name = modelShop.getName();

        loadRevies(modelShop,holder);

       holder.phoneIv.setText(phone);
       holder.nameTv.setText(name);

        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store).into(holder.shopIv);

        } catch (Exception e) {
            holder.shopIv.setImageResource(R.drawable.ic_store);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   Intent intent = new Intent(context, ShopDetailActivity.class);
                                                   intent.putExtra("shopUid", uid);
                                                   context.startActivity(intent);
                                               }
                                           }
        );
    }
    private float retingSum = 0;

    private void loadRevies(ModelShop modelShop, HolderShop holder) {

        String shopUid = modelShop.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        retingSum = 0;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ ds.child("ratings").getValue());
                            retingSum = retingSum + rating;

                        }

                        long numberOfRevies = snapshot.getChildrenCount();
                        float avgRating = retingSum / numberOfRevies;

                        holder.rattingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return shopsList.size();
    }

    class HolderShop extends RecyclerView.ViewHolder {


        private ImageView shopIv;

        private TextView   phoneIv,  nameTv;

        private RatingBar rattingBar;

        public HolderShop(@NonNull View itemView) {
            super(itemView);

            shopIv = itemView.findViewById(R.id.shopIv);

            phoneIv = itemView.findViewById(R.id.phoneIv);
            nameTv = itemView.findViewById(R.id.nameTv);


            rattingBar = itemView.findViewById(R.id.rattingBar);
        }
    }
}


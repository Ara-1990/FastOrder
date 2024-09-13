package com.the.fastorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShopReviesActivity extends AppCompatActivity {


    private ImageButton backBtn;
    private TextView shopNameTv, ratingsTv;
    private ImageView profileIv;
    private RatingBar ratingBar;
    private RecyclerView reviewsRv;

    private FirebaseAuth firebaseAuth;

    public ArrayList<ModelRevew> reviewArrayList;
    private AdapterReview adapterReview;

    private String shopUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_revies);


        backBtn = findViewById(R.id.backBtn);
        shopNameTv = findViewById(R.id.shopNameTv);
        profileIv = findViewById(R.id.profileIv);
        ratingBar = findViewById(R.id.ratingBar);
        ratingsTv = findViewById(R.id.ratingsTv);
        reviewsRv = findViewById(R.id.reviewsRv);

        shopUid = getIntent().getStringExtra("shopUid");

        firebaseAuth = FirebaseAuth.getInstance();
        loadShopDetails();
        loadReviews();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private float retingSum = 0;

    private void loadReviews() {

        reviewArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        reviewArrayList.clear();
                        retingSum = 0;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ ds.child("ratings").getValue());
                            retingSum = retingSum + rating;

                            ModelRevew modelReview = ds.getValue(ModelRevew.class);
                            reviewArrayList.add(modelReview);
                        }
                        adapterReview = new AdapterReview(ShopReviesActivity.this, reviewArrayList);
                        reviewsRv.setAdapter(adapterReview);

                        long numberOfRevies = snapshot.getChildrenCount();
                        float avgRating = retingSum / numberOfRevies;

                        ratingsTv.setText(String.format("%.2f", avgRating) + "[" + numberOfRevies +"]");
                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });

    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                        String shopName = ""+ snapshot.child("name").getValue();
                        String profileImage = ""+ snapshot.child("profileImage").getValue();

                        shopNameTv.setText(shopName);
                        try {

                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store).into(profileIv);

                        }catch (Exception e){

                            profileIv.setImageResource(R.drawable.ic_store);
                        }
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });
    }


}
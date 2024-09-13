package com.the.fastorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainUserActivity extends AppCompatActivity {


    private TextView nameTv,  phoneTv, tabShopsTv, tabOrdersTv;
    private ConstraintLayout shopsRl, ordersRl;
    private ImageButton  editProfileBtn, setingBtn;
    private ImageView profileIv;
    private RecyclerView shopsRv, ordersRv;

    private ArrayList<ModelShop> shopList;
    private AdapterShop adapterShop;

    private ArrayList<ModelOrderUser> ordersList;
    private AdapterOrderUser adapterOrderUser;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    FirebaseDatabase database;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);


        database = FirebaseDatabase.getInstance();

        nameTv = findViewById(R.id.nameTv);

        phoneTv = findViewById(R.id.phoneTv);
        tabShopsTv = findViewById(R.id.tabShopsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        shopsRl = findViewById(R.id.shopsRl);
        ordersRl = findViewById(R.id.ordersRl);

        editProfileBtn = findViewById(R.id.editProfileBtn);
        profileIv = findViewById(R.id.profileIv);
        shopsRv = findViewById(R.id.shopsRv);
        ordersRv = findViewById(R.id.ordersRv);
        setingBtn = findViewById(R.id.setingBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();


            checkUser();




        showShopsUI();



        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUserActivity.this, ProfileEditUserActivity.class));
            }
        });

        tabShopsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShopsUI();
            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUI();
            }
        });

        setingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainUserActivity.this, SetingsActivity.class);
                startActivity(intent);
            }
        });


    }


    private void showShopsUI() {
        shopsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.black));
        tabShopsTv.setBackgroundResource(R.drawable.shape_producthome);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.white));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {

        shopsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.white));
        tabShopsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.black));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_producthome);
    }


    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {

            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        } else {
            loadMyInfo();

        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String accountType = "" + ds.child("accountType").getValue();
                            if(accountType.equals("User")) {
                                String name = "" + ds.child("name").getValue();

                                String phone = "" + ds.child("phone").getValue();
                                String profileImage = "" + ds.child("profileImage").getValue();


                                nameTv.setText(name);

                                phoneTv.setText(phone);

                                try {
                                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person).into(profileIv);
                                } catch (Exception e) {
                                    profileIv.setImageResource(R.drawable.ic_person);

                                }

                                loadShops();

                                loadOrders();

                            }
                            else {
                                startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
                                finish();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadOrders() {
        ordersList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uid = "" + ds.getRef().getKey();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);
                                            ordersList.add(modelOrderUser);
                                        }
                                        adapterOrderUser = new AdapterOrderUser(MainUserActivity.this , ordersList);
                                        ordersRv.setAdapter(adapterOrderUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadShops() {
        shopList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        shopList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            shopList.add(modelShop);

                        }
                        adapterShop = new AdapterShop(MainUserActivity.this, shopList);
                        shopsRv.setAdapter(adapterShop);

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }




}
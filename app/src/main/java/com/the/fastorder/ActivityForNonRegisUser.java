package com.the.fastorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class ActivityForNonRegisUser extends AppCompatActivity {

    private ArrayList<Integer> productsList;

    private AdapterNongegisUser adapterProductNonuser;
    private RecyclerView recyclerView;
    private TextView  not_regis_seller;
    private TextView make_order;

    private FirebaseAuth firebaseAuth;

    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_non_regis_user);

        recyclerView = findViewById(R.id.rv_nor_regisuser);
        recyclerView = findViewById(R.id.rv_nor_regisuser);
        not_regis_seller = findViewById(R.id.not_regis_seller);
        make_order = findViewById(R.id.make_order);

        productsList = new ArrayList<>();


        productsList.add(R.drawable.burger);
        productsList.add(R.drawable.chocolate);
        productsList.add(R.drawable.fruts);
        productsList.add(R.drawable.jiuse);
        productsList.add(R.drawable.pizza);
        productsList.add(R.drawable.salad);

        not_regis_seller.setPaintFlags(not_regis_seller.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        make_order.setPaintFlags(make_order.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();



        adapterProductNonuser = new AdapterNongegisUser(ActivityForNonRegisUser.this, productsList);
        recyclerView.setAdapter(adapterProductNonuser);


        not_regis_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (firebaseUser == null) {

                    Intent intent = new Intent(ActivityForNonRegisUser.this, LoginActivity.class);
                    startActivity(intent);

                }
                else {
                    checkSeler();

                }
            }
        });


        make_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser == null){
                    Intent intent = new Intent(ActivityForNonRegisUser.this, LoginActivity.class);
                    startActivity(intent);
                }else{

                    checkUser();

                }
            }
        });
    }

    private void checkSeler(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String accountType = "" + ds.child("accountType").getValue();
                            if (accountType.equals("Seller")) {

                                startActivity(new Intent(ActivityForNonRegisUser.this, MainSelerActivity.class));
                                finish();

                            }
                            else {
                                startActivity(new Intent(ActivityForNonRegisUser.this, LoginActivity.class));
                                finish();


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void checkUser(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String accountType = "" + ds.child("accountType").getValue();
                            if (accountType.equals("User")) {
                                startActivity(new Intent(ActivityForNonRegisUser.this, MainUserActivity.class));
                                finish();

                            }
                            else {
                                startActivity(new Intent(ActivityForNonRegisUser.this, LoginActivity.class));
                                finish();


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


}



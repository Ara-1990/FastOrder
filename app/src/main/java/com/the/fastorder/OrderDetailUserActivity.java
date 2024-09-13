package com.the.fastorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class OrderDetailUserActivity extends AppCompatActivity {

    private String orderTo, orderId;
    private ImageButton backBtn, writeReviewBtn;
    private TextView orderIdTv, dateTv, orderStatusTv, sellerNameTv, totalItemsTv, amountTv, addressTv;
    private RecyclerView itemsRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderItem> orderItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_user);


        backBtn = findViewById(R.id.backBtn);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        sellerNameTv = findViewById(R.id.sellerNameTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);

        itemsRv = findViewById(R.id.itemsRv);
        writeReviewBtn = findViewById(R.id.writeReviewBtn);

        Intent intent = getIntent();
        orderTo = intent.getStringExtra("orderTo");
        orderId = intent.getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadShopInfo();
        loadOrderDetails();
        loadOrderedItems();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        writeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(OrderDetailUserActivity.this, WriteReviewActivity.class );
                intent1.putExtra("shopUid", orderTo);
                startActivity(intent1);
            }
        });

    }


    private void loadOrderedItems() {
        orderItemArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderItemArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderItem modelOrderItem = ds.getValue(ModelOrderItem.class);
                            orderItemArrayList.add(modelOrderItem);
                        }
                        adapterOrderedItem = new AdapterOrderedItem(OrderDetailUserActivity.this, orderItemArrayList);
                        itemsRv.setAdapter(adapterOrderedItem);

                        totalItemsTv.setText("" + snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrderDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String orderCost = "" + snapshot.child("orderCost").getValue();
                        String orderId = "" + snapshot.child("orderId").getValue();
                        String orderStatus = "" + snapshot.child("orderStatus").getValue();
                        String orderTime = "" + snapshot.child("orderTime").getValue();



                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();

                        if (orderStatus.equals("In progress")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.purple_500));
                        }
                        else if (orderStatus.equals("Completed")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.purple_500));

                        }
                        else if (orderStatus.equals("Cenceled")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.purple_500));

                        }
                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText("" + orderCost );

                        dateTv.setText(formatedDate);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void loadShopInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String sellerName = ""+ snapshot.child("name").getValue();
                        sellerNameTv.setText(sellerName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }







}
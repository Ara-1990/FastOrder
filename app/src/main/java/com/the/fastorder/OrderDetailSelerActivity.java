package com.the.fastorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderDetailSelerActivity extends AppCompatActivity {


    TextView  amountTv, totalItemsTv, phoneTv, emailTv, orderStatusTv, dataTv, orderIdTv;
    ImageButton mapBtn, backBtn, editBtn;
    private RecyclerView itemsRv;


    private String orderId, orderBy;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelOrderItem> orderItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    String sourceLatitude, sourceLongtidute, destinationLatitude, destinationLongtitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_seler);



        amountTv = findViewById(R.id.amountTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        dataTv = findViewById(R.id.dataTv);
        orderIdTv = findViewById(R.id.orderIdTv);
        mapBtn = findViewById(R.id.mapBtn);
        backBtn = findViewById(R.id.backBtn);
        editBtn = findViewById(R.id.editBtn);
        itemsRv = findViewById(R.id.itemsRv);


        orderId = getIntent().getStringExtra("orderTo");
        orderBy = getIntent().getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();

        loadMiInfo();
        loadBuyerInfo();
        loadOrdersDetails();
        loadOrderedItems();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editOrdersStatusDialog();
            }
        });



    }

    private void editOrdersStatusDialog() {
        final String[] options = {"In progress", "completed", "cenceled"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit order status")
                .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                String selectedOptions = options[i];
                                editOrdersStatus(selectedOptions);
                            }
                        }
                ).show();
    }

    private void editOrdersStatus(final String selectedOptions) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus", ""+ selectedOptions);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String message = "Order is now " + selectedOptions;
                        Toast.makeText(OrderDetailSelerActivity.this,   message, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailSelerActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }


    private void openMap() {
        String address = "https://maps.google.com/maps?sadder=" + sourceLatitude + "," + sourceLongtidute + "&daddr=" + destinationLatitude + "," + destinationLongtitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }



    private void loadMiInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        sourceLatitude = ""+snapshot.child("Latitude").getValue();
                        sourceLongtidute = "" + snapshot.child("Longitude").getValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadBuyerInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        destinationLatitude = ""+snapshot.child("Latitude").getValue();
                        destinationLongtitude = "" + snapshot.child("Longitude").getValue();
                        String email = "" + snapshot.child("email").getValue();
                        String phone = "" + snapshot.child("phone").getValue();

                        emailTv.setText(email);
                        phoneTv.setText(phone);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadOrdersDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
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
                        amountTv.setText("" + orderCost);

                        dataTv.setText(formatedDate);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadOrderedItems(){

        orderItemArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderItemArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelOrderItem modelOrderItem = ds.getValue(ModelOrderItem.class);

                            orderItemArrayList.add(modelOrderItem);

                        }
                        adapterOrderedItem = new AdapterOrderedItem(OrderDetailSelerActivity.this, orderItemArrayList);
                        itemsRv.setAdapter(adapterOrderedItem);

                        totalItemsTv.setText(""+ snapshot.getChildrenCount());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }




}
package com.the.fastorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetailActivity extends AppCompatActivity implements LocationListener {


    private ImageButton callBtn, backBtn, filterProductBtn;

    private ImageButton cartBtn;
    private ImageView shopIv, reviewsBtn;
    private EditText searchProductEt;
    private TextView filteredProductsTv, phoneTv, shopNameTv;

    private TextView cartCountTv;

    private RecyclerView productsRv;
    private RatingBar ratingBar;

    private String shopUid;


    private String name;
    private String myPhone;
    private String shopPhone;


    private FirebaseAuth firebaseAuth;


    private ProgressDialog progressDialog;

    private ArrayList<Category> productsList;
    private AdapterProductUser adapterProductUser;


    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    EasyDB easyDB;


    private LocationManager locationManager;
    private String[] locationPermissions;
    private static final int LOCATION_REQUEST_CODE = 100;
    private double latitude, longitude;

    String completeAddres1, city1, state1, country1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        locationPermissions = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION};


        callBtn = findViewById(R.id.callBtn);

        backBtn = findViewById(R.id.backBtn);
        cartBtn = findViewById(R.id.cartBtn);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);

        phoneTv = findViewById(R.id.phoneTv);
        shopNameTv = findViewById(R.id.shopNameTv);

        productsRv = findViewById(R.id.productsRv);
        shopIv = findViewById(R.id.shopIv);
        cartCountTv = findViewById(R.id.cartCountTv);
        reviewsBtn = findViewById(R.id.reviewsBtn);
        ratingBar = findViewById(R.id.ratingBar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();

        loadReview();

        easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();


        deleteCartData();
        cartCount();


        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductUser.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           onBackPressed();
                                       }
                                   }
        );

        cartBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {


                                           showCartDialog();


                                       }
                                   }
        );

        callBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           dialPhone();
                                       }
                                   }
        );

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopDetailActivity.this);
                builder.setTitle("Filter Products:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String selected = Constants.productCategories1[which];
                                        filteredProductsTv.setText(selected);
                                        if (selected.equals("All")) {
                                            loadShopProducts();
                                        } else {
                                            adapterProductUser.getFilter().filter(selected);
                                        }
                                    }
                                }
                        ).show();
            }
        });

        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopDetailActivity.this, ShopReviesActivity.class);
                intent.putExtra("shopUid", shopUid);
                startActivity(intent);
            }
        });


    }

    private float retingSum = 0;

    private void loadReview() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        retingSum = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                            retingSum = retingSum + rating;

                        }

                        long numberOfRevies = snapshot.getChildrenCount();
                        float avgRating = retingSum / numberOfRevies;

                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });

    }


    private void deleteCartData() {
        easyDB.deleteAllDataFromTable();


    }


    public void cartCount() {
        int count = easyDB.getAllData().getCount();
        if (count <= 0) {
            cartCountTv.setVisibility(View.GONE);
        } else {
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText("" + count);
        }
    }


    public double allTotalPrice = 0.0;

    public TextView allTotalPriceTv;


    @SuppressLint("SetTextI18n")
    private void showCartDialog() {
        cartItemList = new ArrayList<>();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);


        RecyclerView cartItemRv = view.findViewById(R.id.cartItemRv);


        allTotalPriceTv = view.findViewById(R.id.totalTv);

        Button confirmorderBtn = view.findViewById(R.id.confirmorderBtn);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);


        EasyDB easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();


        while (res.moveToNext()) {
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);

            String quantity = res.getString(6);

            allTotalPrice = allTotalPrice + Double.parseDouble(cost);


            ModelCartItem modelCartItem = new ModelCartItem(
                    "" + id,
                    "" + pId,
                    "" + name,
                    "" + price,
                    "" + cost,
                    "" + quantity
            );
            cartItemList.add(modelCartItem);
        }
        adapterCartItem = new AdapterCartItem(this, cartItemList);
        cartItemRv.setAdapter(adapterCartItem);


        allTotalPriceTv.setText("$" + (allTotalPrice));

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.00;
            }
        });

        confirmorderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myPhone.equals("") || myPhone.equals("null")) {

                    Toast.makeText(ShopDetailActivity.this, "Please enter your phone number in your profle before placing order...", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (cartItemList.size() == 0) {

                    Toast.makeText(ShopDetailActivity.this, "no item in cart", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (checkLocationPermission()) {
                    delectLocation();

                } else {
                    recuestLocetionPermission();
                }

                inputData();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cartItemRv.setVisibility(View.GONE);
                        allTotalPriceTv.setText("$0.0");
                        allTotalPrice = 0.00;


                    }
                }, 1000);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        dialog.dismiss();

                    }
                }, 2000);


            }
        });

    }

    private String completeAddres, city, state, country;

    private void inputData() {

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {


                completeAddres = completeAddres1;
                city = city1;
                state = state1;
                country = country1;
            }
        }, 40000);

        submitOrder();




        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                cartItemList.clear();


            }
        }, 1000);

    }


    private void submitOrder() {


        progressDialog.show();

        String timesTamp = "" + System.currentTimeMillis();
        String cost = allTotalPriceTv.getText().toString().trim().replace("", "");


        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", "" + timesTamp);
        hashMap.put("orderTime", "" + timesTamp);
        hashMap.put("orderStatus", "In Progress");
        hashMap.put("orderCost", "" + cost);
        hashMap.put("orderBy", "" + firebaseAuth.getUid());
        hashMap.put("orderTo", "" + shopUid);




        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timesTamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {

                        for (int i = 0; i < cartItemList.size(); i++) {
                            String pId = cartItemList.get(i).getpId();
                            String cost = cartItemList.get(i).getCost();
                            String name = cartItemList.get(i).getName();
                            String price = cartItemList.get(i).getPrice();
                            String quantity = cartItemList.get(i).getQuantity();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("pId", pId);
                            hashMap.put("name", name);
                            hashMap.put("cost", cost);
                            hashMap.put("price", price);
                            hashMap.put("quantity", quantity);

                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    HashMap<String, String> hashMap2 = new HashMap<>();

                                    hashMap2.put("completeAddres", "" + completeAddres);
                                    hashMap2.put("city", "" + city);
                                    hashMap2.put("state", "" + state);
                                    hashMap2.put("country", "" + country);

                                    ref.child(timesTamp).child("Location").setValue(hashMap2);



                                }
                            }, 40000);

                            ref.child(timesTamp).child("Items").child(pId).setValue(hashMap);

                        }


                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailActivity.this, "Order place succesfully", Toast.LENGTH_SHORT).show();
                        cartCountTv.setVisibility(View.GONE);
                        easyDB.deleteAllDataFromTable();




                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }


    private boolean checkLocationPermission() {
        boolean resualt = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return resualt;
    }

    @SuppressLint("MissingPermission")
    private void delectLocation() {
        Toast.makeText(this, "Order accepted...", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void recuestLocetionPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        findAdrres();


    }


    private void findAdrres() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            completeAddres1 = address;
            city1 = city;
            state1 = state;
            country1 = country;

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(shopPhone))));
        Toast.makeText(this, "" + shopPhone, Toast.LENGTH_SHORT).show();
    }


    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            myPhone = "" + ds.child("phone").getValue();



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                         name = "" + dataSnapshot.child("name").getValue();


                                                         shopPhone = "" + dataSnapshot.child("phone").getValue();

                                                         String profileImage = "" + dataSnapshot.child("profileImage").getValue();


                                                         shopNameTv.setText(name);


                                                         phoneTv.setText(shopPhone);

                                                         try {
                                                             Picasso.get().load(profileImage).into(shopIv);
                                                         } catch (Exception e) {

                                                         }
                                                     }

                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError databaseError) {
                                                     }
                                                 }
        );
    }

    private void loadShopProducts() {
        productsList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Category modelProduct = ds.getValue(Category.class);
                            productsList.add(modelProduct);
                            adapterProductUser = new AdapterProductUser(ShopDetailActivity.this, productsList);
                            productsRv.setAdapter(adapterProductUser);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


}
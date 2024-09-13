package com.the.fastorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

public class MainSelerActivity extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference category;

    TextView nameTv,  phoneTv, tabProductsTv, tabOrdersTv, filterProductsTv, filterOrdersTv, addProductBtn;
    ConstraintLayout productsRl, ordersRl;
    ImageButton  editProfileBtn, filterProductBtn, filterOrderBtn, reviesBtn, setingBtn;
    ImageView profileIv;
    EditText searchProductEt;
    RecyclerView recycler_menu, ordersRv;


    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;



    private ArrayList<Category> productList;
    private MenuView_Holder adapter_menuViewH;

    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrdeShop adapterOrdeShop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seler);


        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");


        nameTv = findViewById(R.id.nameTv);

        editProfileBtn = findViewById(R.id.editProfileBtn);

        addProductBtn = findViewById(R.id.addProductBtn);
        profileIv = findViewById(R.id.profileIv);
        phoneTv = findViewById(R.id.phoneTv);
        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        productsRl = findViewById(R.id.productsRl);
        ordersRl = findViewById(R.id.ordersRl);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductsTv = findViewById(R.id.filterProductsTv);
        filterOrderBtn = findViewById(R.id.filterOrderBtn);
        filterOrdersTv = findViewById(R.id.filterOrdersTv);
        recycler_menu = findViewById(R.id.recycler_menu);
        ordersRv = findViewById(R.id.ordersRv);
        reviesBtn = findViewById(R.id.reviesBtn);
        setingBtn = findViewById(R.id.setingBtn);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();


        checkUser();

        loadAllProducts();

        loadAllOrders();


        showProductsUi();





        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapter_menuViewH.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductsUi();
            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUi();

            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSelerActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        String selected = Constants.productCategories1[which];
                                        filterProductsTv.setText(selected);
                                        if (selected.equals("All")) {

                                            loadAllProducts();
                                        } else {

                                            loadFilteredProducts(selected);
                                        }
                                    }
                                }
                        ).show();
            }
        });


        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"All", "In progress", "Completed", "Canseled"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSelerActivity.this);
                builder.setTitle("Filter orders")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {

                                    filterOrdersTv.setText("showing all orders");
                                    adapterOrdeShop.getFilter().filter("");
                                } else {
                                    String optionClicked = options[which];
                                    filterOrdersTv.setText("Showing" + optionClicked + "Orders");
                                    adapterOrdeShop.getFilter().filter(optionClicked);
                                }
                            }

                        }).show();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainSelerActivity.this, AddProductActivity.class));

            }
        });


        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSelerActivity.this, ProfileEditSelerActivity.class));

            }
        });


        reviesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainSelerActivity.this, ShopReviesActivity.class);
                intent.putExtra("shopUid", "" + firebaseAuth.getUid());
                startActivity(intent);
            }
        });

        setingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSelerActivity.this, SetingsActivity.class);
                startActivity(intent);
            }
        });

    }


    private void loadAllOrders() {
        orderShopArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderShopArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelOrderShop modelOrderShop = ds.getValue(ModelOrderShop.class);
                            orderShopArrayList.add(modelOrderShop);

                        }
                        adapterOrdeShop = new AdapterOrdeShop(MainSelerActivity.this, orderShopArrayList);
                        ordersRv.setAdapter(adapterOrdeShop);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void loadFilteredProducts(String selected) {
        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String productCategory = "" + ds.child("productCategory").getValue();
                            if (selected.equals(productCategory)) {
                                Category modelProduct = ds.getValue(Category.class);
                                productList.add(modelProduct);
                            }
                        }
                        adapter_menuViewH = new MenuView_Holder(MainSelerActivity.this, productList);
                        recycler_menu.setAdapter(adapter_menuViewH);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts() {
        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Category modelProduct = ds.getValue(Category.class);
                            productList.add(modelProduct);
                        }
                        adapter_menuViewH = new MenuView_Holder(MainSelerActivity.this, productList);
                        recycler_menu.setAdapter(adapter_menuViewH);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void showProductsUi() {
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.black));
        tabProductsTv.setBackgroundResource(R.drawable.shape_producthome);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.white));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void showOrdersUi() {

        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.white));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.black));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_producthome);

    }


    private void loudMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();

                            nameTv.setText(name);

                            phoneTv.setText(phone);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person).into(profileIv);
                            } catch (Exception e) {
                                profileIv.setImageResource(R.drawable.ic_store);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainSelerActivity.this, LoginActivity.class));
            finish();
        } else {
            loudMyInfo();
        }
    }


}

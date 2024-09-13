package com.the.fastorder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {


    private Context context;
    public ArrayList<Category> productsList, filterList;
    private FilterProductUser filter;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    public AdapterProductUser(Context context, ArrayList<Category> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;

    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user, parent, false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        Category modelProduct = productsList.get(position);


        String originalPrice = modelProduct.getOriginalPrice();
        String productDiscription = modelProduct.getProductDiscription();
        String productTitle = modelProduct.getProductTitle();

        String productIcon = modelProduct.getProductIcon();

        holder.titleTv.setText(productTitle);
        holder.descriptionTv.setText(productDiscription);

        holder.originalPriceTv.setText("$" + originalPrice);

        holder.originalPriceTv.setPaintFlags(0);

        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_cart).into(holder.productIconIv);

        } catch (Exception e) {
            holder.productIconIv.setImageResource(R.drawable.ic_cart);

        }


        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firebaseUser != null) {
                    showQuantityDialog(modelProduct);


                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);

                }

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;


    private void showQuantityDialog(Category modelProduct) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity, null);
        ImageView productIv = view.findViewById(R.id.productIv);
        TextView titleTv = view.findViewById(R.id.titleTv);

        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);

        TextView finalPriceTv = view.findViewById(R.id.finalPriceTv);
        TextView quantetyTv = view.findViewById(R.id.quantetyTv);
        ImageButton decreceBtn = view.findViewById(R.id.decreceBtn);
        ImageButton increceBtn = view.findViewById(R.id.increceBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);

        String productId = modelProduct.getProductId();
        String title = modelProduct.getProductTitle();


        String image = modelProduct.getProductIcon();

        String price;
        price = modelProduct.getOriginalPrice();

        cost = Double.parseDouble(price.replaceAll("$", ""));
        finalCost = Double.parseDouble(price.replaceAll("$", ""));
        quantity = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_cart).into(productIv);

        } catch (Exception e) {
            productIv.setImageResource(R.drawable.ic_cart);

        }
        titleTv.setText("" + title);


        quantetyTv.setText("" + quantity);
        originalPriceTv.setText("$" + modelProduct.getOriginalPrice());

        finalPriceTv.setText("$" + finalCost);

        AlertDialog dialog = builder.create();
        dialog.show();

        increceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity++;

                finalPriceTv.setText("$" + finalCost);
                quantetyTv.setText("" + quantity);
            }
        });

        decreceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    finalCost = finalCost - cost;
                    quantity--;

                    finalPriceTv.setText("$" + finalCost);
                    quantetyTv.setText("" + quantity);
                }
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().trim().replace("$", "");
                String quantity = quantetyTv.getText().toString().trim();

                addToCart(productId, title, priceEach, totalPrice, quantity);

                dialog.dismiss();
            }
        });
    }

    private int itemId = 1;

    private void addToCart(String productId, String title, String priceEach, String totalPrice, String quantity) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_Id", itemId)
                .addData("Item_PID", productId)
                .addData("Item_Name", title)
                .addData("Item_Price_Each", priceEach)
                .addData("Item_Price", totalPrice)
                .addData("Item_Quantity", quantity)
                .doneDataAdding();

        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();

        ((ShopDetailActivity) context).cartCount();
    }


    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterProductUser(this, filterList);
        }

        return filter;
    }


    class HolderProductUser extends RecyclerView.ViewHolder {

        private ImageView productIconIv;
        private TextView titleTv, descriptionTv, addToCartTv, originalPriceTv;


        public HolderProductUser(@NonNull View itemView) {
            super(itemView);


            productIconIv = itemView.findViewById(R.id.productIconIv);

            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);


        }
    }


}


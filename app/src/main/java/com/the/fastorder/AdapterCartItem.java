package com.the.fastorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem> {

    private Context context;
    private ArrayList<ModelCartItem> cartItems;


    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart, parent, false);
        return new HolderCartItem(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, @SuppressLint("RecyclerView") int position) {

        ModelCartItem modelCartItem = cartItems.get(position);
        String id = modelCartItem.getId();
        String title = modelCartItem.getName();
        final String cost = modelCartItem.getCost();
        String price = modelCartItem.getPrice();
        String quantity = modelCartItem.getQuantity();


        holder.itemTitleTv.setText("" + title);
        holder.itemPriceTv.setText("$" + cost);
        holder.itemEachPrice.setText("$" + price);
        holder.itemQuantityTv.setText("[" + quantity + "]");


        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onClick(View v) {
                EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                        .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1, id);

                Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show();

                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                View view = LayoutInflater.from(context).inflate(R.layout.dialog_cart, null);
                TextView totalSum = view.findViewById(R.id.totalTv);
                totalSum.setText("");

                try {


                    double tx = Double.parseDouble((((ShopDetailActivity) context).allTotalPriceTv.getText().toString().trim().replace("$", "")));
                    double totalPrice = tx - Double.parseDouble(cost.replace("$", ""));


                    ((ShopDetailActivity) context).allTotalPrice = 0.0;



                    ((ShopDetailActivity) context).allTotalPriceTv.setText("$" + String.format(Locale.US, "%.2f", Double.parseDouble(String.format(Locale.US, "%.2f", totalPrice))));


                    ((ShopDetailActivity) context).cartCount();
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder {



        private TextView itemTitleTv, itemPriceTv, item_quantity, itemQuantityTv, itemRemoveTv, itemEachPrice;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            item_quantity = itemView.findViewById(R.id.item_quantity);
            itemEachPrice = itemView.findViewById(R.id.itemEachPrice);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv = itemView.findViewById(R.id.itemRemoveTv);

        }
    }
}



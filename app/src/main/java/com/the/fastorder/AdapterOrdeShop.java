package com.the.fastorder;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrdeShop extends RecyclerView.Adapter<AdapterOrdeShop.HolderOrderShop> implements Filterable {

    private Context context;
    ArrayList<ModelOrderShop> orderShopArrayList, filterList;
    private FilterOrderShop filter;

    public AdapterOrdeShop(Context context, ArrayList<ModelOrderShop> orderShopArrayList) {
        this.context = context;
        this.orderShopArrayList = orderShopArrayList;
        this.filterList = orderShopArrayList;
    }

    @NonNull
    @Override
    public HolderOrderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_seler, parent, false);

        return new HolderOrderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrdeShop.HolderOrderShop holder, int position) {
        ModelOrderShop modelOrderShop = orderShopArrayList.get(position);
        String orderId = modelOrderShop.getOrderId();
        String orderBy = modelOrderShop.getOrderBy();
        String orderCost = modelOrderShop.getOrderCost();
        String orderStatus = modelOrderShop.getOrderStatus();
        String orderTime = modelOrderShop.getOrderTime();


        loadUserInfo(modelOrderShop, holder);

        holder.amountTv.setText("Amount: " + orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("Order Id" + orderId);

        if (orderStatus.equals("In progress")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.purple_500));
        } else if (orderStatus.equals("Completed")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.purple_200));
        } else if (orderStatus.equals("Cenceled")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.purple_700));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formateDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.orderDateTv.setText(formateDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailSelerActivity.class);
                intent.putExtra("orderTo", orderId);
                intent.putExtra("orderId", orderBy);
                context.startActivity(intent);
            }
        });

    }

    private void loadUserInfo(ModelOrderShop modelOrderShop, HolderOrderShop holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderShop.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String phone = ""+ snapshot.child("phone").getValue();
                        holder.phoneTv.setText(phone);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderShopArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterOrderShop(this, filterList);
        }
        return filter;
    }

    class HolderOrderShop extends RecyclerView.ViewHolder {

        private TextView orderDateTv, orderIdTv, phoneTv, amountTv, statusTv;

        public HolderOrderShop(@NonNull View itemView) {
            super(itemView);

            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);



        }
    }
}



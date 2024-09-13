package com.the.fastorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterOrderedItem extends RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem>{

    private Context context;
    private ArrayList<ModelOrderItem> orderedItemArrayList;

    public AdapterOrderedItem(Context context, ArrayList<ModelOrderItem> orderedItemArrayList){
        this.context = context;
        this.orderedItemArrayList = orderedItemArrayList;
    }


    @androidx.annotation.NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ordered_item, parent, false);
        return new HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull HolderOrderedItem holder, int position) {

        ModelOrderItem modelOrderItem = orderedItemArrayList.get(position);
        String name = modelOrderItem.getName();
        String cost = modelOrderItem.getCost();
        String price = modelOrderItem.getPrice();
        String quantity = modelOrderItem.getQuantity();

        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText("$"+price);
        holder.itemPriceTv.setText("$"+cost);
        holder.itemQuantityTvTv.setText("["+ quantity +"]");

    }

    @Override
    public int getItemCount() {
        return orderedItemArrayList.size();
    }

    class HolderOrderedItem extends RecyclerView.ViewHolder{

        private TextView itemTitleTv, itemPriceTv, itemPriceEachTv, itemQuantityTvTv;
        public HolderOrderedItem(@NonNull View itemView){
            super(itemView);

            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTvTv = itemView.findViewById(R.id.itemQuantityTvTv);
        }
    }
}


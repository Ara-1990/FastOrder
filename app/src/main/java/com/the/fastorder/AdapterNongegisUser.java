package com.the.fastorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterNongegisUser extends RecyclerView.Adapter<AdapterNongegisUser.holderNonRegisUser> {
        private Context context;
    public ArrayList<Integer> productsList;



    public AdapterNongegisUser(Context context,ArrayList<Integer> productsList) {

        this.context = context;
        this.productsList = productsList;



    }

    @NonNull
    @Override
    public holderNonRegisUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_non_regisuser, parent, false);
        return new AdapterNongegisUser.holderNonRegisUser(view);
    }




    @Override
    public void onBindViewHolder(@NonNull holderNonRegisUser holder, int position) {
        Glide.with(context).load(productsList.get(position))
                .into(holder.productIconIv);


    }

        @Override
    public int getItemCount() {
        return productsList.size();
    }


    class holderNonRegisUser extends RecyclerView.ViewHolder {

        private ImageView productIconIv;


        public holderNonRegisUser(@NonNull View itemView) {
            super(itemView);



            productIconIv = itemView.findViewById(R.id.productIconIv);


        }
    }


}

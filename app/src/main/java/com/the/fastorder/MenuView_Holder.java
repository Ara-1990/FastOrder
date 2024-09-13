package com.the.fastorder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MenuView_Holder extends RecyclerView.Adapter<MenuView_Holder.HolderProductSeler> implements Filterable  {

    private Context context;
    public ArrayList<Category> productList, filterLists;
    private Filter_Products filter;

    public MenuView_Holder(Context context, ArrayList<Category> productList) {
        this.context = context;
        this.productList = productList;
        this.filterLists = productList;


    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new Filter_Products(this, filterLists);
        }
        return filter;

    }


    @NonNull
    @Override
    public HolderProductSeler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.m_item, parent, false);

        return new HolderProductSeler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeler holder, int position) {
        final Category model = productList.get(position);

        String productDescription = model.getProductDiscription();
        String icon = model.getProductIcon();
        String quantuty = model.getProductQuantaty();
        String title = model.getProductTitle();
        String originalPrice = model.getOriginalPrice();


        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantuty);
        holder.discreptionTv.setText(productDescription);

        holder.originalPriceTv.setText("$" + originalPrice);



        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_store).into(holder.productIconIv);

        } catch (Exception e) {
            holder.productIconIv.setImageResource(R.drawable.ic_store);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detailButtomSheet(model);

            }


        });

    }

       private void detailButtomSheet(Category category) {

        BottomSheetDialog buttomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bs_product_detail_seller, null);
        buttomSheetDialog.setContentView(view);



        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
        ImageButton editBtn = view.findViewById(R.id.editBtn);
        ImageView productIconIv = view.findViewById(R.id.productIconIv);

        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView discriptionTv = view.findViewById(R.id.discriptionTv);
        TextView categoryTv = view.findViewById(R.id.categoryTv);
        TextView quantityTv = view.findViewById(R.id.quantityTv);

        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);

     final String id = category.getProductId();


        String productCategory = category.getProductCategory();
        String productDescription = category.getProductDiscription();
        String icon = category.getProductIcon();
        String quantuty = category.getProductQuantaty();
        String title = category.getProductTitle();
        String originalPrice = category.getOriginalPrice();

        titleTv.setText(title);
        discriptionTv.setText(productDescription);
        categoryTv.setText(productCategory);
        quantityTv.setText(quantuty);

        originalPriceTv.setText("$" + originalPrice);



        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_cart).into(productIconIv);
        } catch (Exception e) {
            productIconIv.setImageResource(R.drawable.ic_cart);

        }

        buttomSheetDialog.show();

         editBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             buttomSheetDialog.dismiss();

                                             Intent intent = new Intent(context, EditProductActivity.class);
                                             intent.putExtra("productId", id);
                                             context.startActivity(intent);
                                         }
                                     }
        );

         deleteBtn.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               buttomSheetDialog.dismiss();
                                               AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                               builder.setTitle("Delete")
                                                       .setMessage("Are you shore you whant to delete product" + title + " ?")
                                                       .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                                           @Override
                                                           public void onClick(DialogInterface dialog, int which) {
                                                               deleteProduct(id);
                                                           }
                                                       }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                           @Override
                                                           public void onClick(DialogInterface dialog, int which) {
                                                               dialog.dismiss();
                                                           }
                                                       }).show();
                                           }
                                       }
        );


         backBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             buttomSheetDialog.dismiss();
                                         }
                                     }
        );


    }

       private void deleteProduct(String id){


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                                          @Override
                                          public void onSuccess(Void avoid){
                                              Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();
                                          }
                                      }
                ).addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e){
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
    }






    @Override
    public int getItemCount() {
        return productList.size();
    }

    class HolderProductSeler extends RecyclerView.ViewHolder {

        public TextView titleTv, quantityTv,  originalPriceTv, discreptionTv;
        public ImageView productIconIv;

        public HolderProductSeler(@NonNull View itemView) {
            super(itemView);

            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            quantityTv = (TextView) itemView.findViewById(R.id.quantityTv);

            originalPriceTv = (TextView) itemView.findViewById(R.id.originalPriceTv);
            productIconIv = (ImageView) itemView.findViewById(R.id.productIconIv);
            productIconIv = (ImageView) itemView.findViewById(R.id.productIconIv);
            discreptionTv = (TextView) itemView.findViewById(R.id.discreption);


        }
    }
}





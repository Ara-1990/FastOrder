package com.the.fastorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProductActivity extends AppCompatActivity {


    private String productId;

    ImageButton backBtn;
    ImageView productIconIv;
    EditText titleEt, discriptionEt, categoryEt, priceEt,  quantetyEt;
    Button updateProductBtn;
    RelativeLayout edit_pr;


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;


    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri image_uri;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);


        backBtn = findViewById(R.id.backBtn);
        productIconIv = findViewById(R.id.productIconIv);
        titleEt = findViewById(R.id.titleEt);
        discriptionEt = findViewById(R.id.discriptionEt);
        categoryEt = findViewById(R.id.categoryEt);
        priceEt = findViewById(R.id.priceEt);
        quantetyEt = findViewById(R.id.quantetyEt);
        updateProductBtn = findViewById(R.id.updateProductBtn);
        edit_pr = findViewById(R.id.edit_pr);

        productId = getIntent().getStringExtra("productId");


        firebaseAuth = FirebaseAuth.getInstance();
        loadProductDitails();


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edit_pr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImagePickDialog();
            }
        });

        categoryEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                categoryDialog();
            }
        });

        updateProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });



    }

    private String productTitle, productDiscription, productCategory, productQuantaty, originalPrice;


    private void inputData() {
        productTitle = titleEt.getText().toString().trim();
        productDiscription = discriptionEt.getText().toString().trim();
        productCategory = categoryEt.getText().toString().trim();
        productQuantaty = quantetyEt.getText().toString().trim();
        originalPrice = priceEt.getText().toString().trim();


        if (TextUtils.isEmpty(productTitle)) {
            Toast.makeText(this, "Title is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(productCategory)) {
            Toast.makeText(this, "Category is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(originalPrice)) {
            Toast.makeText(this, "Price is required...", Toast.LENGTH_SHORT).show();
            return;
        }




        updateProduct();
    }

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String category = Constants.productCategories[which];

                        categoryEt.setText(category);
                    }
                }).show();
    }

    private void showImagePickDialog() {
        String[] options = {"Cammera", "Galery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (checkCamerraPermission()) {
                                pickFromCamerra();
                            } else {
                                requestCammeraPermission();
                            }
                        } else {

                            if (checkStoragePermission()) {

                                pickFromGallery();
                            } else {

                                requestStoragePermission();
                            }
                        }
                    }
                }).show();
    }

    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamerra() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Discription");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        Boolean resualt = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return resualt;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCamerraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCammeraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestcode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestcode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cammeraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cammeraAccepted) {

                        pickFromCamerra();
                    } else {

                        Toast.makeText(this, "Camera & Storage permissions are requied...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {

                        pickFromGallery();

                }
            }
        }
        super.onRequestPermissionsResult(requestcode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, @Nullable Intent data) {
        if (resultcode == RESULT_OK) {
            if (requestcode == IMAGE_PICK_GALLERY_CODE) {

                image_uri = data.getData();
                productIconIv.setImageURI(image_uri);
            } else if (requestcode == IMAGE_PICK_CAMERA_CODE) {

                productIconIv.setImageURI(image_uri);


            }
        }
        super.onActivityResult(requestcode, resultcode, data);
    }


    private void loadProductDitails() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                .addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                               String productTitle = "" + dataSnapshot.child("productTitle").getValue();
                                               String productDescription = "" + dataSnapshot.child("productDiscription").getValue();
                                               String productCategory = "" + dataSnapshot.child("productCategory").getValue();
                                               String productQuantity = "" + dataSnapshot.child("productQuantaty").getValue();
                                               String productIcon = "" + dataSnapshot.child("productIcon").getValue();
                                               String originalPrice = "" + dataSnapshot.child("originalPrice").getValue();

                                               titleEt.setText(productTitle);
                                               discriptionEt.setText(productDescription);
                                               categoryEt.setText(productCategory);
                                               quantetyEt.setText(productQuantity);
                                               priceEt.setText(originalPrice);


                                               try {
                                                   Picasso.get().load(productIcon).placeholder(R.drawable.ic_store).into(productIconIv);

                                               } catch (Exception e) {
                                                   productIconIv.setImageResource(R.drawable.ic_store);

                                               }

                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                           }
                                       }
                );
    }

    private void updateProduct(){


        progressDialog.setMessage("Updating product...");
        progressDialog.show();

        if(image_uri == null){

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("productTitle", ""+ productTitle);
            hashMap.put("productDiscription", ""+ productDiscription);
            hashMap.put("productCategory", ""+ productCategory);
            hashMap.put("productQuantaty", ""+ productQuantaty);
            hashMap.put("originalPrice", ""+ originalPrice);
            hashMap.put("productIcon", "");





            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                    .updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>(){
                                              @Override
                                              public void onSuccess(Void avoid){

                                                  progressDialog.dismiss();
                                                  Toast.makeText(EditProductActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                                              }
                                          }
                    ).addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e){

                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    });

        } else {

            String filePathAndName = "product_images/"+""+productId;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                                              @Override
                                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){

                                                  Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                  while(!uriTask.isSuccessful());
                                                  Uri downloadImageUri = uriTask.getResult();

                                                  if(uriTask.isSuccessful()){

                                                      HashMap<String, Object> hashMap = new HashMap<>();
                                                      hashMap.put("productTitle", ""+ productTitle);
                                                      hashMap.put("productDiscription", ""+ productDiscription);
                                                      hashMap.put("productCategory", ""+ productCategory);
                                                     hashMap.put("productIcon", ""+ downloadImageUri);
                                                      hashMap.put("productQuantaty", ""+ productQuantaty);
                                                      hashMap.put("originalPrice", ""+ originalPrice);

                                                      DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                                      reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                                                              .updateChildren(hashMap)
                                                              .addOnSuccessListener(new OnSuccessListener<Void>(){
                                                                                        @Override
                                                                                        public void onSuccess(Void avoid){
                                                                                            progressDialog.dismiss();
                                                                                            Toast.makeText(EditProductActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                                                                                        }
                                                                                    }
                                                              ).addOnFailureListener(new OnFailureListener(){
                                                                  @Override
                                                                  public void onFailure(@NonNull Exception e){
                                                                      progressDialog.dismiss();
                                                                      Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                                                  }

                                                              });

                                                  }



                                              }
                                          }
                    ).addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e){
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    });

        }

    }




}
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
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class RegisterSelerActivity extends AppCompatActivity {


    ImageButton backBtn;;
    ImageView profileIv;
    EditText passwordEt, emailIv, fullNameEt,  phoneEt,  compirmPasswordEt;
    RelativeLayout rel_seller;

    Button registerBtn;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri image_uri;



    private ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seler);


        passwordEt = findViewById(R.id.passwordEt);
        emailIv = findViewById(R.id.emailIv);
        registerBtn = findViewById(R.id.registerBtn);
        backBtn = findViewById(R.id.backBtn);

        profileIv = findViewById(R.id.profileIv);
        fullNameEt = findViewById(R.id.fullNameEt);

        phoneEt = findViewById(R.id.phoneEt);

        compirmPasswordEt = findViewById(R.id.compirmPasswordEt);
        rel_seller = findViewById(R.id.rel_seller);


        cameraPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        rel_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();


            }
        });



    }

    private String name,  phone,  email, password, compirmPassword;



    private void inputData() {
        name = fullNameEt.getText().toString().trim();

        phone = phoneEt.getText().toString().trim();

        email = emailIv.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        compirmPassword = compirmPasswordEt.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter name...", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter phone number...", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email patters...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(compirmPassword)) {
            Toast.makeText(this, "Password dosent mutch...", Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount();
    }

    private void createAccount() {
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveFirebaseData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterSelerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void saveFirebaseData() {
        progressDialog.setMessage("saving Account Info...");

        String timesTemp = ""+ System.currentTimeMillis();

        if (image_uri == null){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid","" +firebaseAuth.getUid());
            hashMap.put("email","" +email);
            hashMap.put("password","" +password);
            hashMap.put("confirmpassword","" +compirmPassword);
            hashMap.put("name","" +name);

            hashMap.put("timesTemp","" +timesTemp);
            hashMap.put("accountType","Seller");

            hashMap.put("profileImage","");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterSelerActivity.this, MainSelerActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterSelerActivity.this, MainSelerActivity.class));
                            finish();

                        }
                    });

        } else {
            String filePathandName = "Profile image/" + ""+ firebaseAuth.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathandName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();
                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid","" +firebaseAuth.getUid());
                                hashMap.put("email","" +email);
                                hashMap.put("name","" +name);
                                hashMap.put("password","" +password);
                                hashMap.put("confirmpassword","" +compirmPassword);

                                hashMap.put("phone","" +phone);

                                hashMap.put("timesTemp","" +timesTemp);
                                hashMap.put("accountType","Seller");

                                hashMap.put("profileImage",""+ downloadImageUri);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterSelerActivity.this, MainSelerActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterSelerActivity.this, MainSelerActivity.class));
                                                finish();

                                            }
                                        });


                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterSelerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void showImagePickDialog() {
        String[] options = {"Camera", "Galery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (checkCamerraPermission()) {
                        pickFromCamerra();
                    } else {
                        requestCamerraPermission();
                    }
                } else {
                    if (checkStroragePermission()) {
                        pickFromGalery();
                    } else {
                        requestStoragePermission();
                    }
                }
            }
        }).show();

    }

    private void pickFromGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamerra() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp image description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }




    private boolean checkStroragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);

    }

    private boolean checkCamerraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }


    private void requestCamerraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResualt) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResualt.length > 0) {
                    boolean cameraAccepted = grantResualt[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted ) {
                        pickFromCamerra();
                    } else {
                        Toast.makeText(this, "Camera permissions are nesesery...", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            case STORAGE_REQUEST_CODE: {
                if (grantResualt.length > 0) {

                        pickFromGalery();

                }
            }


            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResualt);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                profileIv.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                profileIv.setImageURI(image_uri);

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
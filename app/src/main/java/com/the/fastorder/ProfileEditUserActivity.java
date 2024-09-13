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
import com.google.firebase.auth.FirebaseUser;
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

public class ProfileEditUserActivity extends AppCompatActivity {


    private ImageButton backBtn;
    private ImageView profileIv;
    private EditText nameEt, phoneEt;
    private Button updateBtn;
    private RelativeLayout edit_profile;


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;


    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private Uri image_uri;


    private String[] cameraPermissions;
    private String[] storagePermissions;


    private ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_user);

        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);

        updateBtn = findViewById(R.id.updateBtn);
        edit_profile = findViewById(R.id.edit_profile);


        cameraPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

    }


    private String name, phone;


    private void inputData() {
        name = nameEt.getText().toString().trim();
        phone = phoneEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter name...", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter phone number...", Toast.LENGTH_SHORT).show();
            return;
        }


        updateProfile();
    }

    private void updateProfile() {
        progressDialog.setMessage("updating Profile....");
        progressDialog.show();

        if (image_uri == null) {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", "" + name);
            hashMap.put("phone", "" + phone);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileEditUserActivity.this, "Profile update", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileEditUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        } else {

            String filePathAndName = "profile_images" + "" + firebaseAuth.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;

                            if (uriTask.isSuccessful()) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("name", "" + name);
                                hashMap.put("phone", "" + phone);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(ProfileEditUserActivity.this, "Profile update", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(ProfileEditUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileEditUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            loudMyInfo();
        }
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


                            nameEt.setText(name);
                            phoneEt.setText(phone);


                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person).into(profileIv);

                            } catch (Exception e) {
                                profileIv.setImageResource(R.drawable.ic_person);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);

    }

    private void requestCamerraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);

    }


    private boolean checkStroragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
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


    private void pickFromGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }


    private void pickFromCamerra() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "image description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResualt) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResualt.length > 0) {
                    boolean cameraAccepted = grantResualt[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted) {
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